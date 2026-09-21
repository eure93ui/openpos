package org.codeberg.assertix.openpos.app.ui.startup

import java.nio.file.Path

sealed interface StartupState {
    data object Selecting : StartupState
    data class Verifying(val path: Path) : StartupState
    data class Failed(val path: Path) : StartupState
    data class Creation(val path: Path) : StartupState
    data class Ready(val path: Path) : StartupState
}