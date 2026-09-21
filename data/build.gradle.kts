plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.kotlinx.dataframe)
    implementation(libs.libphonenumber)
}

spotless {
    kotlin {
        ktlint()
    }
}

kotlin {
    jvmToolchain(25)
}
