package org.codeberg.assertix.openpos.data.validation

import org.codeberg.assertix.openpos.data.model.company.CompanyType

object KppValidator {
    fun isValid(
        kpp: String,
        companyType: CompanyType,
    ): Boolean {
        val clean = kpp.trim()
        if (companyType == CompanyType.Individual && clean.isEmpty()) return true
        if (clean.isEmpty()) return companyType == CompanyType.Individual
        return clean.all { it.isDigit() } && clean.length == 9
    }

    fun getValidationError(
        kpp: String,
        companyType: CompanyType,
    ): String? {
        val clean = kpp.trim()
        if (clean.isEmpty()) {
            return if (companyType == CompanyType.LegalEntity) "КПП обязателен для юридического лица" else null
        }
        if (!clean.all { it.isDigit() }) return "КПП должен содержать только цифры"
        if (clean.length != 9) return "КПП должен содержать ровно 9 цифр"
        return null
    }
}
