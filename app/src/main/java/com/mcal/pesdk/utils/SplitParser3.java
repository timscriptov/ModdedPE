package com.mcal.pesdk.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SplitParser3 {
    private static String[] libs = new String[]{"libminecraftpe.so", "libc++_shared.so", "libfmod.so"};

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
            ApplicationInfo mcpe_info = context.getPackageManager().getPackageInfo("com.mojang,minecraftpe", (int) 0).applicationInfo;
            if (mcpe_info != null && isBundle(mcpe_info)) {
                String split_path = Arrays.asList(mcpe_info.splitPublicSourceDirs).get(0);
                ZipFile apk = new ZipFile(split_path);
                byte[] buffer = new byte[2048];
                int read;
                for (String so : libs) {
                    InputStream is = apk.getInputStream(new ZipEntry("lib" + "/" + Build.CPU_ABI + "/" + so));
                    FileOutputStream fos = new FileOutputStream(new File(arm64  + "/" + so));
                    while ((read = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Contract(pure = true)
    private static boolean isBundle(@NotNull ApplicationInfo applicationInfo) {
        if (applicationInfo.splitPublicSourceDirs.length != 0) {
            return true;
        }
        return false;
    }
}