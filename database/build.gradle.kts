plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

dependencies {
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.kotlinx.coroutines)
    implementation(projects.data)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.register<JavaExec>("generateSchema") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.codeberg.assertix.openpos.database.SchemaGenerator")
    val outputFile = rootProject.file("app/src/commonMain/composeResources/files/schema.sqlite")
    args(outputFile.absolutePath)
    outputs.file(outputFile)
}

spotless {
    kotlin {
        ktlint()
    }
}
