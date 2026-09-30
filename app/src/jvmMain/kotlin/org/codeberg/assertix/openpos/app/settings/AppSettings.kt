package org.codeberg.assertix.openpos.app.settings

import androidx.compose.runtime.Stable
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.file.Path
import java.time.LocalDate
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

@Stable
class AppSettings(private val settings: Settings) {
    val rememberedDatabase: StateFlow<Path?>
        field = run {
            val path = settings.getStringOrNull(Keys.REMEMBERED_DATABASE)
                ?.let { Path(it) }
                ?.takeIf { it.exists() }
            MutableStateFlow(path)
        }

    fun rememberDatabase(path: Path) {
        settings.putString(Keys.REMEMBERED_DATABASE, path.absolutePathString())
        rememberedDatabase.value = path
    }

    fun clearRememberedDatabase() {
        settings.remove(Keys.REMEMBERED_DATABASE)
        rememberedDatabase.value = null
    }

    val screenZoom: StateFlow<Float>
        field = MutableStateFlow(
            settings.getFloat(
                Keys.SCREEN_ZOOM,
                ScreenZoomValue.Zero.value
            )
        )

    fun updateScreenZoom(screenZoomValue: ScreenZoomValue) {
        settings.putFloat(Keys.SCREEN_ZOOM, screenZoomValue.value)
        screenZoom.value = screenZoomValue.value
    }

    val historyDateFilterOption: StateFlow<String>
        field = MutableStateFlow(
            settings.getString(Keys.HISTORY_DATE_FILTER, "YEAR")
        )

    fun updateHistoryDateFilterOption(optionName: String) {
        settings.putString(Keys.HISTORY_DATE_FILTER, optionName)
        historyDateFilterOption.value = optionName
    }

    val historyCustomStartDate: StateFlow<String?>
        field = MutableStateFlow(
            settings.getStringOrNull(Keys.HISTORY_CUSTOM_START_DATE)
        )

    val historyCustomEndDate: StateFlow<String?>
        field = MutableStateFlow(
            settings.getStringOrNull(Keys.HISTORY_CUSTOM_END_DATE)
        )

    fun updateHistoryCustomRange(startDate: LocalDate?, endDate: LocalDate?) {
        if (startDate != null) {
            settings.putString(Keys.HISTORY_CUSTOM_START_DATE, startDate.toString())
        } else {
            settings.remove(Keys.HISTORY_CUSTOM_START_DATE)
        }
        if (endDate != null) {
            settings.putString(Keys.HISTORY_CUSTOM_END_DATE, endDate.toString())
        } else {
            settings.remove(Keys.HISTORY_CUSTOM_END_DATE)
        }
        historyCustomStartDate.value = startDate?.toString()
        historyCustomEndDate.value = endDate?.toString()
    }

    private object Keys {
        const val SCREEN_ZOOM = "screen_zoom"
        const val REMEMBERED_DATABASE = "loaded_database"
        const val HISTORY_DATE_FILTER = "history_date_filter"
        const val HISTORY_CUSTOM_START_DATE = "history_custom_start_date"
        const val HISTORY_CUSTOM_END_DATE = "history_custom_end_date"
    }
}
