plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        ktlint()
    }
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.material3)
            implementation(libs.multiplatform.settings)
            implementation(libs.bundles.filekit)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin)

            implementation(libs.bundles.kotlinx.coroutines)

            implementation(projects.calculations)
            implementation(projects.database)
            implementation(projects.data)
            implementation(projects.reporting)
        }

        commonMain.dependencies {
            implementation(libs.compose.resources)
        }
    }

    jvmToolchain(25)
}

compose.desktop {
    application {
        mainClass = "org.codeberg.assertix.openpos.app.MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

compose.resources {
    packageOfResClass = "org.codeberg.assertix.openpos.resources"
}

tasks.named("compileKotlinJvm") {
    dependsOn(":database:generateSchema")
}

tasks.named("copyNonXmlValueResourcesForCommonMain") {
    dependsOn(":database:generateSchema")
}

tasks.named("prepareComposeResourcesTaskForCommonMain") {
    dependsOn(":database:generateSchema")
}