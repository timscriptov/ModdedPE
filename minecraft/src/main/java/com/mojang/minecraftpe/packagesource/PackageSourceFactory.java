package com.mojang.minecraftpe.packagesource;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.Contract;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */

public class PackageSourceFactory {
    @NonNull
    @Contract(pure = true)
    static PackageSource createGooglePlayPackageSource(String googlePlayLicenseKey, PackageSourceListener packageSourceListener) {
        return new StubPackageSource(packageSourceListener);
    }
}