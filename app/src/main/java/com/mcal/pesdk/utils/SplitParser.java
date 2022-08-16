/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.pesdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.mcal.mcpelauncher.data.Preferences;

import org.jetbrains.annotations.Contract;
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
public class SplitParser {
    private static final String[] minecraftLibs = new String[]{"libminecraftpe.so", "libc++_shared.so", "libfmod.so", "libMediaDecoders_Android.so"};
    private static final String[] moddedpeLibs = new String[]{"libnmod-core.so", "libxhook.so", "libsubstrate.so"};

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    /**
     * Извлечение C++ библиотек из Minecraft
     */
    public static void parseMinecraft(Context context) {
        mContext = context;

        File lib = new File(mContext.getCacheDir().getPath() + "/lib");
        if (!lib.exists()) {
            lib.mkdir();
        }

        File abiPath = new File(lib + "/" + ABIInfo.getABI());
        if (!abiPath.exists()) {
            abiPath.mkdir();
        }

        try {
            if (isAppBundle() && MinecraftInfo.getMinecraftPackageContext().getApplicationInfo() != null) {
                String split_path = Arrays.asList(MinecraftInfo.getMinecraftPackageContext().getApplicationInfo().splitPublicSourceDirs).get(0);
                byte[] buffer = new byte[2048];
                for (String so : minecraftLibs) {
                    InputStream is = new ZipFile(split_path).getInputStream(new ZipEntry("lib/" + ABIInfo.getABI() + "/" + so));
                    FileOutputStream fos = new FileOutputStream(abiPath + "/" + so);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Извлечение C++ библиотек из Minecraft
     */
    public static void parseLauncher(Context context) {
        mContext = context;

        File lib = new File(mContext.getCacheDir().getPath() + "/lib");
        if (!lib.exists()) {
            lib.mkdir();
        }

        File abiPath = new File(lib + "/" + ABIInfo.getABI());
        if (!abiPath.exists()) {
            abiPath.mkdir();
        }

        try {
            byte[] buffer = new byte[2048];
            for (String so : moddedpeLibs) {
                InputStream is = new ZipFile(context.getApplicationInfo().publicSourceDir).getInputStream(new ZipEntry("lib/" + ABIInfo.getABI() + "/" + so));
                FileOutputStream fos = new FileOutputStream(abiPath + "/" + so);
                do {
                    int numread = is.read(buffer);
                    if (numread <= 0) {
                        break;
                    }
                    fos.write(buffer, 0, numread);
                } while (true);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка формата приложения на App Bundle
     */
    @Contract(pure = true)
    public static boolean isAppBundle() {
        return MinecraftInfo.getMinecraftPackageContext().getApplicationInfo().splitPublicSourceDirs != null && MinecraftInfo.getMinecraftPackageContext().getApplicationInfo().splitPublicSourceDirs.length > 0;
    }
}