package org.codeberg.assertix.openpos.app.ui.history

import java.time.LocalDate

enum class HistoryDateFilterOption(val labelKey: String) {
    WEEK("Неделя"),
    MONTH("Месяц"),
    HALF_YEAR("Полугодие"),
    YEAR("Год"),
    ALL("За все время"),
    CUSTOM("Произвольный период")
}

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {
        fun calculate(option: HistoryDateFilterOption, today: LocalDate = LocalDate.now()): DateRange? {
            return when (option) {
                HistoryDateFilterOption.WEEK -> DateRange(today.minusWeeks(1), today)
                HistoryDateFilterOption.MONTH -> DateRange(today.minusMonths(1), today)
                HistoryDateFilterOption.HALF_YEAR -> DateRange(today.minusMonths(6), today)
                HistoryDateFilterOption.YEAR -> DateRange(today.withDayOfYear(1), today) // or today.minusYears(1)
                HistoryDateFilterOption.ALL -> null
                HistoryDateFilterOption.CUSTOM -> null
            }
        }
    }
}
