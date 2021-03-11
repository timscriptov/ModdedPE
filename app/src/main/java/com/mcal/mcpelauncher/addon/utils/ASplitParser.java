package com.mcal.mcpelauncher.addon.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.utils.ABIInfo;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 * @author Vologhat
 */
public class ASplitParser {
    private static final String[] minecraftLibs = new String[]{"libminecraftpe.so", "libc++_shared.so", "libfmod.so"};

    /**
     * Извлечение C++ библиотек из Minecraft
     *
     * @param context
     */
    public static void parse(@NotNull Context context) {
        File lib = new File(context.getCacheDir().getPath() + "/lib");
        if (!lib.exists()) {
            lib.mkdir();
        }

        File arm64 = new File(lib + "/" + ABIInfo.getABI());
        if (!arm64.exists()) {
            arm64.mkdir();
        }

        try {
            if (isAppBundle(context)) {
                if (getMinecraftApplicationInfo(context) != null) {
                    String split_path = Arrays.asList(getMinecraftApplicationInfo(context).splitPublicSourceDirs).get(0);
                    byte[] buffer = new byte[2048];
                    for (String so : minecraftLibs) {
                        InputStream is = new ZipFile(split_path).getInputStream(new ZipEntry("lib/" + ABIInfo.getABI() + "/" + so));
                        FileOutputStream fos = new FileOutputStream(arm64 + "/" + so);
                        do {
                            int numread = is.read(buffer);
                            if (numread <= 0) {
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        } while (true);
                        fos.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка формата приложения на App Bundle
     *
     * @return
     */
    @Contract(pure = true)
    public static boolean isAppBundle(Context context) {
        return getMinecraftContext(context).getApplicationInfo().splitPublicSourceDirs != null && getMinecraftContext(context).getApplicationInfo().splitPublicSourceDirs.length > 0;
    }

    private static @Nullable ApplicationInfo getMinecraftApplicationInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(Preferences.getMinecraftPEPackageName(), 0).applicationInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Context getMinecraftContext(Context context) {
        try {
            return context.createPackageContext(Preferences.getMinecraftPEPackageName(), Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMinecraftPackageNativeLibraryDir(Context context) {
        if (isAppBundle(context)) {
            return context.getCacheDir().getPath() + "/lib/" + Build.CPU_ABI;
        } else {
            return getMinecraftContext(context).getApplicationInfo().nativeLibraryDir;
        }
    }
}