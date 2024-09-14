plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.microsoft.xboxtcui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.firebase.messaging)
    implementation(libs.firebase.iid)

    implementation(libs.pkix)

    implementation(libs.gson)
    implementation(libs.httpclient)
    implementation(libs.simple.xml)
}
