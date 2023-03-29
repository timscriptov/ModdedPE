package com.mojang.minecraftpe.store;

import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.store.amazonappstore.AmazonAppStore;
import com.mojang.minecraftpe.store.googleplay.GooglePlayStore;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class StoreFactory {
    @NotNull
    @Contract("_, _ -> new")
    static Store createGooglePlayStore(String googlePlayLicenseKey, StoreListener storeListener) {
        return new GooglePlayStore(MainActivity.mInstance, googlePlayLicenseKey, storeListener);
    }

    @NotNull
    @Contract("_, _ -> new")
    static Store createAmazonAppStore(StoreListener storeListener, boolean forFireTV) {
        return new AmazonAppStore(MainActivity.mInstance, storeListener, forFireTV);
    }
}