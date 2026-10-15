plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}

kotlin {
    jvmToolchain(25)
}

spotless {
    kotlin {
        ktlint()
    }
}
