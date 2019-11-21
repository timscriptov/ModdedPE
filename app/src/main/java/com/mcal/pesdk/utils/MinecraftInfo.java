/*
 * Copyright (C) 2018-2019 Тимашков Иван
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

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MinecraftInfo {
    private static String MC_PACKAGE_NAME = "com.mojang.minecraftpe";

    private Context mContext;
    private Context mMCContext;

    public MinecraftInfo(Context context, LauncherOptions options) {
        this.mContext = context;

        String mMinecraftPackageName = MC_PACKAGE_NAME;
        if (!options.getMinecraftPEPackageName().equals(LauncherOptions.STRING_VALUE_DEFAULT))
            mMinecraftPackageName = options.getMinecraftPEPackageName();

        try {
            mMCContext = context.createPackageContext(mMinecraftPackageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
        } catch (PackageManager.NameNotFoundException e) {
        }

        AssetOverrideManager.newInstance();
        if (mMCContext != null)
            AssetOverrideManager.getInstance().addAssetOverride(mMCContext.getPackageResourcePath());
        AssetOverrideManager.getInstance().addAssetOverride(mContext.getPackageResourcePath());
    }

    public boolean isSupportedMinecraftVersion(String[] versions) {
        String mcpeVersionName = getMinecraftVersionName();
        if (mcpeVersionName == null)
            return false;
        for (String nameItem : versions) {
            Pattern pattern = Pattern.compile(nameItem);
            Matcher matcher = pattern.matcher(mcpeVersionName);
            if (matcher.find())
                return true;
        }
        return false;
    }

    public String getMinecraftVersionName() {
        if (getMinecraftPackageContext() == null)
            return null;
        try {
            return mContext.getPackageManager().getPackageInfo(getMinecraftPackageContext().getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public String getMinecraftPackageNativeLibraryDir() {
        return mMCContext.getApplicationInfo().nativeLibraryDir;
    }

    public Context getMinecraftPackageContext() {
        return mMCContext;
    }

    public boolean isMinecraftInstalled() {
        return getMinecraftPackageContext() != null;
    }

    public AssetOverrideManager getAssetOverrideManager() {
        return AssetOverrideManager.getInstance();
    }

    public AssetManager getAssets() {
        return getAssetOverrideManager().getAssetManager();
    }
}
