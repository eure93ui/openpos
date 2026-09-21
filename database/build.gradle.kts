plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.bundles.exposed)
    implementation(projects.data)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

spotless {
    kotlin {
        ktlint()
    }
}

tasks.test {
    useJUnitPlatform()
}
