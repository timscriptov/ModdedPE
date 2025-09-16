plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)

    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinxParcelize)
}

android {
    namespace = "com.mcal.moddedpe"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = "26.1.10909125"

    defaultConfig {
        applicationId = "com.mcal.moddedpe"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 972110101
        versionName = "1.21.101.1"

        ndk {
            abiFilters.addAll(
                setOf(
                    "armeabi-v7a",
                    "arm64-v8a",
                    "x86",
                    "x86_64"
                )
            )
        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    sourceSets {
        getByName("debug").assets.srcDirs("../assets-pack/src/main/assets")
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/INDEX.LIST",
                "META-INF/*.kotlin_module"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    buildFeatures {
//        viewBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    configurations {
        all {
            exclude(group = "xpp3", module = "xpp3")
        }
    }
    assetPacks += ":assets-pack"
}

dependencies {
    implementation(project(":minecraft"))
    implementation(project(":httpclient"))
    implementation(project(":microsoft:xal"))
    implementation(project(":microsoft:xbox"))
    implementation(project(":fmod"))

    implementation(libs.androidx.games.activity)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

//    implementation(libs.exposed.jdbc)
//    implementation(libs.xerial.jdbc)

//    implementation(libs.ktor.client.okhttp)
//    implementation(libs.ktor.client.core)
//    implementation(libs.ktor.client.cio)

    implementation(libs.apkparser)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.material)

    // Wortise AD
//    implementation(libs.wortise.sdk)

    // IronSource AD
//    implementation(libs.mediationsdk)

    // Admob AD
//    implementation(libs.play.services.ads)

    implementation(libs.bundles.koin)
    implementation(libs.bundles.voyager)
}

configurations.all {
    exclude(group = "net.sf.kxml", module = "kxml2")
    exclude(group = "xpp3", module = "xpp3")
}
