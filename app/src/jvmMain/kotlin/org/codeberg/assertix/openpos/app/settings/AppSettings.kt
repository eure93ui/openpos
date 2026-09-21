package org.codeberg.assertix.openpos.app.settings

import androidx.compose.runtime.Stable
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.file.Path
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

    private object Keys {
        const val SCREEN_ZOOM = "screen_zoom"
        const val REMEMBERED_DATABASE = "loaded_database"
    }
}
