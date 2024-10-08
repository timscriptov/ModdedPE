plugins {
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mcal.substrate"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = "26.1.10909125"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("consumer-rules.pro")

        ndk {
            abiFilters.addAll(
                setOf(
                    "armeabi-v7a",
                )
            )
        }
    }
    externalNativeBuild {
        ndkBuild {
            path = File("src/main/cpp/Android.mk")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

}
