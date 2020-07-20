package com.mojang.minecraftpe.packagesource;

import com.mojang.minecraftpe.MainActivity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class PackageSourceFactory {
    @Nullable
    @Contract(pure = true)
    static PackageSource createGooglePlayPackageSource(String googlePlayLicenseKey, PackageSourceListener packageSourceListener) {
        return null;
    }
}