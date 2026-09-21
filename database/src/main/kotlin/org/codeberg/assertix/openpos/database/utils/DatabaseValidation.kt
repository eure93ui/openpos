package org.codeberg.assertix.openpos.database.utils

import org.codeberg.assertix.openpos.database.api.SessionHolder
import java.nio.file.Path

suspend fun isValidSqliteDatabase(path: Path): Boolean =
    SessionHolder().use { holder ->
        holder.validateDatabase(path)
    }
