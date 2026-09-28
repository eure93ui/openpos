package org.codeberg.assertix.openpos.reporting.engine

import com.github.moneytostr.MoneyToStr
import java.math.BigDecimal

fun BigDecimal.stringRepresentation(defaultLanguage: String): String {
    val currencyAndLanguage = currencyAndLanguageFor(defaultLanguage)
    val converter =
        MoneyToStr(
            currencyAndLanguage.first,
            currencyAndLanguage.second,
            MoneyToStr.Pennies.TEXT,
        )
    return converter.convert(this.toDouble())
}

fun currencyAndLanguageFor(language: String) =
    when (language) {
        "ru" -> MoneyToStr.Currency.RUR to MoneyToStr.Language.RUS
        "en" -> MoneyToStr.Currency.USD to MoneyToStr.Language.ENG
        else -> MoneyToStr.Currency.USD to MoneyToStr.Language.ENG
    }
