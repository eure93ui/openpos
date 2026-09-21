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
import org.codeberg.assertix.openpos.database.api.repository.CompanyProfileRepository
import org.codeberg.assertix.openpos.database.api.repository.finance.FinancialRepository

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
    val phoneNumber: String = "+7 (999) 123-45-67",
    val companyType: CompanyType = CompanyType.LegalEntity,
    val taxPercent: Int = 20,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val databasePath: String = ""
)

@Stable
class SettingsViewModel(
    private val companyProfileRepository: CompanyProfileRepository,
    private val appSettings: AppSettings,
    private val financialRepository: FinancialRepository
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
                val dbPath = appSettings.rememberedDatabase.value?.toAbsolutePath()?.toString() ?: ""
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
                        phoneNumber = profile.companyInfo.phoneNumber.value,
                        companyType = profile.companyInfo.companyType,
                        taxPercent = financialSettings.taxPercent,
                        invoicePrefix = financialSettings.invoicePrefix,
                        databasePath = dbPath,
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
    fun updateBankName(value: String) { _uiState.update { it.copy(bankName = value) } }
    fun updateBic(value: String) { _uiState.update { it.copy(bic = value) } }
    fun updateAccount(value: String) { _uiState.update { it.copy(account = value) } }
    fun updateCorrAccount(value: String) { _uiState.update { it.copy(corrAccount = value) } }
    fun updateInvoicePrefix(value: String) { _uiState.update { it.copy(invoicePrefix = value) } }
    fun updateTaxPercent(value: Int) { _uiState.update { it.copy(taxPercent = value) } }

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
                        phoneNumber = PhoneNumber(state.phoneNumber)
                    ),
                    companyCredentials = CompanyCredentials(
                        inn = state.inn,
                        kpp = state.kpp
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
}
