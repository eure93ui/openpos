plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
}

kotlin {
    jvmToolchain(25)
}
