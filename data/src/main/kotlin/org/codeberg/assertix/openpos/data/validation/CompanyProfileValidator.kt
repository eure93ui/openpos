package org.codeberg.assertix.openpos.data.validation

import org.codeberg.assertix.openpos.data.model.company.CompanyProfile

data class ValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap(),
)

object CompanyProfileValidator {
    fun validate(profile: CompanyProfile): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (profile.companyInfo.fullNaming.isBlank()) {
            errors["fullNaming"] = "Наименование не может быть пустым"
        }
        if (profile.companyInfo.legalAddress.isBlank()) {
            errors["legalAddress"] = "Адрес не может быть пустым"
        }

        val innError = InnValidator.getValidationError(profile.companyCredentials.inn, profile.companyInfo.companyType)
        if (innError != null) errors["inn"] = innError

        val kppError = KppValidator.getValidationError(profile.companyCredentials.kpp, profile.companyInfo.companyType)
        if (kppError != null) errors["kpp"] = kppError

        if (profile.bankDetails.bankName.isBlank()) {
            errors["bankName"] = "Наименование банка не может быть пустым"
        }

        val bicError = BicValidator.getValidationError(profile.bankDetails.bic)
        if (bicError != null) errors["bic"] = bicError

        val accError = BankAccountValidator.getValidationError(profile.bankDetails.checkingAccount, "Расчетный счет")
        if (accError != null) errors["checkingAccount"] = accError

        val corrError = BankAccountValidator.getValidationError(profile.bankDetails.correspondentAccount, "Корреспондентский счет")
        if (corrError != null) errors["correspondentAccount"] = corrError

        return ValidationResult(isValid = errors.isEmpty(), errors = errors)
    }
}
