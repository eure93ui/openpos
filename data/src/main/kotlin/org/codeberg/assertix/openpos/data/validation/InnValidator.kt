package org.codeberg.assertix.openpos.data.validation

import org.codeberg.assertix.openpos.data.model.company.CompanyType

object InnValidator {
    fun isValid(
        inn: String,
        companyType: CompanyType,
    ): Boolean {
        val clean = inn.trim()
        return clean.all { it.isDigit() } &&
            when (companyType) {
                CompanyType.LegalEntity -> clean.length == 10
                CompanyType.Individual -> clean.length == 12
            }
    }

    fun getValidationError(
        inn: String,
        companyType: CompanyType,
    ): String? {
        val clean = inn.trim()
        if (clean.isBlank()) return "ИНН не может быть пустым"
        if (!clean.all { it.isDigit() }) return "ИНН должен содержать только цифры"
        return when (companyType) {
            CompanyType.LegalEntity -> if (clean.length != 10) "ИНН юридического лица должен содержать ровно 10 цифр" else null
            CompanyType.Individual -> if (clean.length != 12) "ИНН индивидуального предпринимателя должен содержать ровно 12 цифр" else null
        }
    }
}
