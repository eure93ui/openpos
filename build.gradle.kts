plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.spotless) apply false
}

//TODO implement convention plugin to remove jvm-library+kotlin+spotless repetition

private val versionFile = layout.projectDirectory.file("version.txt")
private val versionProvider = providers.fileContents(versionFile).asText.map { it.trim() }
version = versionProvider.get()

project.group = "org.codeberg.assertix.openpos"
