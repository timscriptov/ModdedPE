// In the asset pack’s build.gradle.kts file:
plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("game")
    dynamicDelivery {
        deliveryType.set("install-time")
    }
}