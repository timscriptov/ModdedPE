rootProject.name = "ModdedPE"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
//            mavenContent {
//                includeGroupAndSubgroups("androidx")
//                includeGroupAndSubgroups("com.android")
//                includeGroupAndSubgroups("com.google")
//            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.wortise.com/artifactory/public")
        maven("https://android-sdk.is.com/")
        maven("https://artifact.bytedance.com/repository/pangle")
        maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
    }
}

include(":app")
include(":substrate")
include(":minecraft")
include(":minecraft-app")
include(":httpclient")
include(":microsoft:xal")
include(":microsoft:xbox")
include(":xhook")
include(":fmod")
include(":downloader")
include(":licensing")
include(":assets-pack")
