plugins {
    id("com.android.library")
}

android {
    namespace = "com.android.vending.expansion.downloader"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        aidl = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":licensing"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //noinspection DuplicatePlatformClasses
    implementation(libs.httpclient)
    implementation(libs.httpmime)
}