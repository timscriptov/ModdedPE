package com.mojang.minecraftpe.store;

import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.store.amazonappstore.AmazonAppStore;
import com.mojang.minecraftpe.store.googleplay.GooglePlayStore;

public class StoreFactory {
    static Store createGooglePlayStore(String googlePlayLicenseKey, StoreListener storeListener) {
        return new GooglePlayStore(MainActivity.mInstance, googlePlayLicenseKey, storeListener);
    }

    static Store createAmazonAppStore(StoreListener storeListener, boolean forFireTV) {
        return new AmazonAppStore(MainActivity.mInstance, storeListener, forFireTV);
    }
}
