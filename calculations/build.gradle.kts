plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(projects.data)
}

spotless {
    kotlin {
        ktlint()
    }
}

kotlin {
    jvmToolchain(25)
}
