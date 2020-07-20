package com.mojang.minecraftpe.packagesource;

import android.util.Log;
import java.util.EnumMap;
import java.util.Map;
import java.util.HashMap;

public abstract class PackageSource {
    public static final Map<Integer, String> stringResources = new HashMap<>();

    public abstract void abortDownload();
    public abstract void destructor();
    public abstract void downloadFiles(String str, long j, boolean z, boolean z2);
    public abstract String getDownloadDirectoryPath();
    public abstract String getMountPath(String str);
    public abstract void mountFiles(String str);
    public abstract void pauseDownload();
    public abstract void resumeDownload();
    public abstract void resumeDownloadOnCell();
    public abstract void unmountFiles(String str);

    public static void setStringResource(int i, String str) {
        stringResources.put(i, str);
    }
}