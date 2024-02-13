import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinxSerialization)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    jvm("desktop")
    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.material3)
                implementation(compose.desktop.currentOs)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)

                implementation(libs.compose.shimmer)
                implementation(libs.kamel.image)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Patcher"
            packageVersion = "1.0.3"
            copyright = "© 2023-2024 timscriptov."
            vendor = "timscriptov"
        }
    }
}
