package com.mojang.minecraftpe.packagesource;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class PackageSourceFactory {
    @Nullable
    @Contract(pure = true)
    static PackageSource createGooglePlayPackageSource(String googlePlayLicenseKey, PackageSourceListener packageSourceListener) {
        return new StubPackageSource(packageSourceListener);
    }
}