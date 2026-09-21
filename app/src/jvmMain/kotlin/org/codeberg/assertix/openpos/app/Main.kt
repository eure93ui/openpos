package org.codeberg.assertix.openpos.app

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.vinceglb.filekit.FileKit
import org.codeberg.assertix.openpos.app.di.appModule
import org.codeberg.assertix.openpos.app.ui.startup.StartupScreen
import org.codeberg.assertix.openpos.app.ui.theme.OpenPosTheme
import org.codeberg.assertix.openpos.app.util.appId
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

fun main() {
    FileKit.init(appId)

    application {
        val windowState = rememberWindowState(
            size = DpSize(1280.dp, 800.dp)
        )

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "OpenPOS"
        ) {
            KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
                OpenPosTheme {
                    StartupScreen()
                }
            }
        }
    }
}
