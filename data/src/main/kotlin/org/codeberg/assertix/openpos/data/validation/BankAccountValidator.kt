package org.codeberg.assertix.openpos.data.validation

object BankAccountValidator {
    fun isValid(account: String): Boolean {
        val clean = account.trim()
        return clean.all { it.isDigit() } && clean.length == 20
    }

    fun getValidationError(
        account: String,
        fieldName: String,
    ): String? {
        val clean = account.trim()
        if (clean.isBlank()) return "$fieldName не может быть пустым"
        if (!clean.all { it.isDigit() }) return "$fieldName должен содержать только цифры"
        if (clean.length != 20) return "$fieldName должен содержать ровно 20 цифр"
        return null
    }
}
