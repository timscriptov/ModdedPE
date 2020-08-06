package com.mcal.pesdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SplitParser2 {
    private static ApplicationInfo mcpe;
    private static String[] minecraftLibs = new String[]{"libminecraftpe.so", "libc++_shared.so", "libfmod.so"};

    public static void parse(@NotNull Context context) {
        File lib = new File(context.getCacheDir().getPath() + "/lib");
        if (!lib.exists()) {
            lib.mkdir();
        }

        File arm64 = new File(lib + "/" + Build.CPU_ABI);
        if (!arm64.exists()) {
            arm64.mkdir();
        }

        try {
            for (ApplicationInfo applicationInfo : context.getPackageManager().getInstalledApplications(0)) {
                if (applicationInfo.packageName.equals("com.mojang.minecraftpe")) {
                    mcpe = applicationInfo;
                    break;
                }
            }
            if (mcpe == null) {
                return;
            }
            List<String> list = new ArrayList<>();
            for (int i = 0; i < mcpe.splitPublicSourceDirs.length; i++) {
                list = Arrays.asList(mcpe.splitPublicSourceDirs);
            }
            String splitpath = list.get(0);
            byte[] buffer = new byte[65535];
            for (String so : minecraftLibs) {
                InputStream inputStream = new ZipFile(splitpath).getInputStream(new ZipEntry("lib" + "/" + Build.CPU_ABI + "/" + so));
                FileOutputStream fos = new FileOutputStream(arm64 + "/" + so);
                do {
                    int numread = inputStream.read(buffer);
                    if (numread <= 0) {
                        break;
                    }
                    fos.write(buffer, 0, numread);
                } while (true);
                fos.close();
            }
        } catch (Exception e) {
            Log.i(SplitParser.class.getName(), e.getMessage());
        }
    }

    @Contract(pure = true)
    public static boolean isBundle(@NotNull ApplicationInfo applicationInfo) {
        return applicationInfo.splitPublicSourceDirs.length != 0;
    }
}