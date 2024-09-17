import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(compose.desktop.currentOs)
            implementation(compose.components.resources)

            // Common dependencies
            implementation(libs.coil.compose)
            implementation(libs.coil.compose.core)
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)

            // Desktop-specific dependency
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.koin.core.jvm)
            implementation(libs.bundles.voyager)
        }
    }
}

compose.desktop {
    application {
        mainClass = "ru.mcal.mclibpatcher.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.mcal.mclibpatcher"
            packageVersion = "1.0.0"
        }
    }
}
