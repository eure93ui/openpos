plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.pebble)
    implementation(libs.bundles.openhtmltopdf)
    implementation(libs.moneytostr)
    implementation(projects.data)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktlint()
    }
}
