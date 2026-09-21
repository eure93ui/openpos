package org.codeberg.assertix.openpos.database.utils

import org.jetbrains.exposed.v1.jdbc.Database
import java.nio.file.Path
import kotlin.io.path.absolutePathString

fun connect(path: Path) = Database.connect("jdbc:sqlite:${path.absolutePathString()}", "org.sqlite.JDBC")
