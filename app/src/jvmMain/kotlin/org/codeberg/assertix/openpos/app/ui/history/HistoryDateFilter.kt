package org.codeberg.assertix.openpos.app.ui.history

import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.filter_all
import org.codeberg.assertix.openpos.resources.filter_custom
import org.codeberg.assertix.openpos.resources.filter_half_year
import org.codeberg.assertix.openpos.resources.filter_month
import org.codeberg.assertix.openpos.resources.filter_week
import org.codeberg.assertix.openpos.resources.filter_year
import org.jetbrains.compose.resources.StringResource
import java.time.LocalDate

enum class HistoryDateFilterOption(val labelRes: StringResource) {
    WEEK(Res.string.filter_week),
    MONTH(Res.string.filter_month),
    HALF_YEAR(Res.string.filter_half_year),
    YEAR(Res.string.filter_year),
    ALL(Res.string.filter_all),
    CUSTOM(Res.string.filter_custom)
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
