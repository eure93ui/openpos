package org.codeberg.assertix.openpos.app.ui.startup.validators

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.codeberg.assertix.openpos.app.ui.startup.StartupCreationStep
import org.codeberg.assertix.openpos.app.ui.startup.StartupDefaults
import org.codeberg.assertix.openpos.data.model.company.CompanyType

@Stable
class DatabaseCreationFormState {
    var currentStep by mutableStateOf<StartupCreationStep>(StartupCreationStep.CompanyInfo)
    var companyType by mutableStateOf(CompanyType.LegalEntity)

    val legalName = LegalNameValidator(StartupDefaults.DEFAULT_LEGAL_NAME)
    val legalAddress = LegalAddressValidator(StartupDefaults.DEFAULT_LEGAL_ADDRESS)
    val phone = PhoneValidator(StartupDefaults.DEFAULT_PHONE_NUMBER)

    val inn = InnValidatorState(StartupDefaults.DEFAULT_INN) { companyType }
    val kpp = KppValidatorState(StartupDefaults.DEFAULT_KPP) { companyType }

    val bankName = BankNameValidator(StartupDefaults.DEFAULT_BANK_NAME)
    val bic = BicValidatorState(StartupDefaults.DEFAULT_BIC)
    val checkingAccount = BankAccountValidatorState(StartupDefaults.DEFAULT_CHECKING_ACCOUNT, "Расчетный счет")
    val correspondentAccount = BankAccountValidatorState(StartupDefaults.DEFAULT_CORRESPONDENT_ACCOUNT, "Корреспондентский счет")

    val invoicePrefix = mutableStateOf("INV-2026-")
    val taxPercent = mutableStateOf("22")

    val isStep0Valid: Boolean
        get() = legalName.isValid && legalAddress.isValid && phone.isValid

    val isStep1Valid: Boolean
        get() = inn.isValid && (companyType != CompanyType.LegalEntity || kpp.isValid)

    val isStep2Valid: Boolean
        get() = bankName.isValid && bic.isValid && checkingAccount.isValid && correspondentAccount.isValid

    val isStep3Valid: Boolean
        get() = invoicePrefix.value.isNotBlank() && taxPercent.value.toIntOrNull() != null && (taxPercent.value.toIntOrNull() ?: -1) >= 0

    val isCurrentStepValid: Boolean
        get() = when (currentStep) {
            is StartupCreationStep.CompanyInfo -> isStep0Valid
            is StartupCreationStep.CompanyCredentials -> isStep1Valid
            is StartupCreationStep.BankDetails -> isStep2Valid
            is StartupCreationStep.FinancialSettings -> isStep3Valid
        }

    fun proceedToNextStep() {
        currentStep.next?.let { currentStep = it }
    }

    fun proceedToPreviousStep() {
        currentStep.previous?.let { currentStep = it }
    }
}
