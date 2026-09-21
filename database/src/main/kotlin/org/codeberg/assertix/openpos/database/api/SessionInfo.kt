package org.codeberg.assertix.openpos.database.api

import kotlinx.coroutines.flow.StateFlow
import java.nio.file.Path

data class SessionInfo(
    private val sessionHolder: SessionHolder,
) {
    fun uriPath(): String {
        val path: Path? = sessionHolder.currentPath
        return path?.fileName?.toString() ?: "database.db"
    }

    val savingState: StateFlow<DatabaseSavingState>
        get() = sessionHolder.savingState
}
