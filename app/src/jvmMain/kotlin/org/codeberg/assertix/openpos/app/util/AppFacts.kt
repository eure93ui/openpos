package org.codeberg.assertix.openpos.app.util

const val defaultId = "openpos"
const val defaultVersion = "unknown"

val appId: String = System.getProperty("app.id", defaultId)
val appName: String = System.getProperty("app.name", defaultId)
val appVersion: String = System.getProperty("app.version", defaultVersion)