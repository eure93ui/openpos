package org.codeberg.assertix.openpos.data.validation

object BicValidator {
    fun isValid(bic: String): Boolean {
        val clean = bic.trim()
        return clean.all { it.isDigit() } && clean.length == 9
    }

    fun getValidationError(bic: String): String? {
        val clean = bic.trim()
        if (clean.isBlank()) return "БИК не может быть пустым"
        if (!clean.all { it.isDigit() }) return "БИК должен содержать только цифры"
        if (clean.length != 9) return "БИК должен содержать ровно 9 цифр"
        return null
    }
}
