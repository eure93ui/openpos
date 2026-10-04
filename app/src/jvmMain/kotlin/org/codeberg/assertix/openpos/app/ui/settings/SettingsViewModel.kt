package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.app.settings.AppSettings
import org.codeberg.assertix.openpos.data.model.FinancialConfiguration
import org.codeberg.assertix.openpos.data.model.PhoneNumber
import org.codeberg.assertix.openpos.data.model.company.*
import org.codeberg.assertix.openpos.database.api.SessionHolder
import org.codeberg.assertix.openpos.database.api.repository.CompanyProfileRepository
import org.codeberg.assertix.openpos.database.api.repository.finance.FinancialRepository
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

data class SettingsUiState(
    val legalName: String = "",
    val inn: String = "",
    val kpp: String = "",
    val address: String = "",
    val bankName: String = "",
    val bic: String = "",
    val account: String = "",
    val corrAccount: String = "",
    val invoicePrefix: String = "INV-2026-",
    val phoneNumbers: List<String> = listOf("+7 (999) 123-45-67"),
    val companyType: CompanyType = CompanyType.LegalEntity,
    val taxPercent: Int = 20,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val databasePath: String = "",
    val defaultPrinter: String? = null,
    val availablePrinters: List<String> = emptyList()
)

@Stable
class SettingsViewModel(
    private val companyProfileRepository: CompanyProfileRepository,
    private val appSettings: AppSettings,
    private val financialRepository: FinancialRepository,
    private val sessionHolder: SessionHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
                val profile = companyProfileRepository.get()
                val financialSettings = financialRepository.get()
                val dbPath = sessionHolder.currentPath?.toAbsolutePath()?.toString()
                    ?: appSettings.rememberedDatabase.value?.toAbsolutePath()?.toString()
                    ?: ""
                val defaultPrinter = appSettings.defaultPrinter.value
                val availablePrinters = org.codeberg.assertix.openpos.app.util.PrinterManager.getAvailablePrinters()
                _uiState.update {
                    it.copy(
                        legalName = profile.companyInfo.fullNaming,
                        inn = profile.companyCredentials.inn,
                        kpp = profile.companyCredentials.kpp,
                        address = profile.companyInfo.legalAddress,
                        bankName = profile.bankDetails.bankName,
                        bic = profile.bankDetails.bic,
                        account = profile.bankDetails.checkingAccount,
                        corrAccount = profile.bankDetails.correspondentAccount,
                        phoneNumbers = profile.companyInfo.phoneNumbers.map { it.value }.ifEmpty { listOf("+7 (999) 123-45-67") },
                        companyType = profile.companyInfo.companyType,
                        taxPercent = financialSettings.taxPercent,
                        invoicePrefix = financialSettings.invoicePrefix,
                        databasePath = dbPath,
                        defaultPrinter = defaultPrinter,
                        availablePrinters = availablePrinters,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateLegalName(value: String) { _uiState.update { it.copy(legalName = value) } }
    fun updateInn(value: String) { _uiState.update { it.copy(inn = value) } }
    fun updateKpp(value: String) { _uiState.update { it.copy(kpp = value) } }
    fun updateAddress(value: String) { _uiState.update { it.copy(address = value) } }
    fun updatePhoneNumber(index: Int, value: String) {
        _uiState.update { state ->
            val updated = state.phoneNumbers.toMutableList()
            if (index in updated.indices) {
                updated[index] = value
            }
            state.copy(phoneNumbers = updated)
        }
    }
    fun addPhoneNumber() {
        _uiState.update { state ->
            state.copy(phoneNumbers = state.phoneNumbers + "")
        }
    }
    fun removePhoneNumber(index: Int) {
        if (index > 0) {
            _uiState.update { state ->
                val updated = state.phoneNumbers.toMutableList()
                if (index in updated.indices) {
                    updated.removeAt(index)
                }
                state.copy(phoneNumbers = updated.ifEmpty { listOf("") })
            }
        }
    }
    fun updateCompanyType(type: CompanyType) {
        _uiState.update {
            it.copy(
                companyType = type,
                kpp = if (type == CompanyType.Individual) "" else it.kpp
            )
        }
    }
    fun updateBankName(value: String) { _uiState.update { it.copy(bankName = value) } }
    fun updateBic(value: String) { _uiState.update { it.copy(bic = value) } }
    fun updateAccount(value: String) { _uiState.update { it.copy(account = value) } }
    fun updateCorrAccount(value: String) { _uiState.update { it.copy(corrAccount = value) } }
    fun updateInvoicePrefix(value: String) { _uiState.update { it.copy(invoicePrefix = value) } }
    fun updateTaxPercent(value: Int) { _uiState.update { it.copy(taxPercent = value) } }
    fun updateDefaultPrinter(printerName: String?) {
        appSettings.updateDefaultPrinter(printerName)
        _uiState.update { it.copy(defaultPrinter = printerName) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
                val state = _uiState.value
                val profile = CompanyProfile(
                    companyInfo = CompanyInfo(
                        fullNaming = state.legalName,
                        companyType = state.companyType,
                        legalAddress = state.address,
                        phoneNumbers = state.phoneNumbers.map { PhoneNumber(it) }
                    ),
                    companyCredentials = CompanyCredentials(
                        inn = state.inn,
                        kpp = if (state.companyType == CompanyType.Individual) "" else state.kpp
                    ),
                    bankDetails = BankDetails(
                        bankName = state.bankName,
                        bic = state.bic,
                        checkingAccount = state.account,
                        correspondentAccount = state.corrAccount
                    )
                )
                companyProfileRepository.update(profile)
                financialRepository.update(
                    FinancialConfiguration(
                        taxPercent = state.taxPercent,
                        invoicePrefix = state.invoicePrefix
                    )
                )
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message, saveSuccess = false) }
            }
        }
    }

    fun importDatabase(path: Path) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
                val isValid = sessionHolder.validateDatabase(path)
                if (isValid) {
                    sessionHolder.switchSession(path)
                    appSettings.rememberDatabase(path)
                    loadProfile()
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Выбранный файл не является корректной базой данных SQLite!") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun exportDatabase(path: Path) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
                val currentPath = sessionHolder.currentPath
                if (currentPath != null && currentPath.exists()) {
                    Files.copy(currentPath, path, StandardCopyOption.REPLACE_EXISTING)
                    _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Активная база данных не найдена") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun exportEmptySchema(path: Path) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
                val bytes = org.codeberg.assertix.openpos.resources.Res.readBytes("files/schema.sqlite")
                Files.write(path, bytes)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun switchDatabase() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                sessionHolder.close()
                appSettings.clearRememberedDatabase()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
