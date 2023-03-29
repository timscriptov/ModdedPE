package com.mojang.minecraftpe.packagesource;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NativePackageSourceListener implements PackageSourceListener {
    public long mPackageSourceListener;

    public native void nativeOnDownloadProgress(long j, long j2, long j3, float f, long j4);

    public native void nativeOnDownloadStarted(long j);

    public native void nativeOnDownloadStateChanged(long j, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i, int i2);

    public native void nativeOnMountStateChanged(long j, String str, int i);

    public void setListener(long packageSourceListener) {
        this.mPackageSourceListener = packageSourceListener;
    }
}