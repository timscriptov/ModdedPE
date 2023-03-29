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
package com.mcal.pesdk.nmod;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class PackagedNMod extends NMod {
    private final Context mPackageContext;

    PackagedNMod(String packageName, Context contextThiz, Context packageContext) {
        super(packageName, contextThiz);
        this.mPackageContext = packageContext;
        preload();
    }

    @Override
    public String getPackageResourcePath() {
        return getPackageContext().getPackageResourcePath();
    }

    @Override
    public NModPreloadBean copyNModFiles() {
        NModPreloadBean ret = new NModPreloadBean();
        ret.assets_path = getPackageResourcePath();
        ArrayList<NModLibInfo> nativeLibs = new ArrayList<>();
        if (mInfo.native_libs_info != null) {
            for (NModLibInfo lib_item : mInfo.native_libs_info) {
                NModLibInfo newInfo = new NModLibInfo();
                newInfo.name = getNativeLibsPath() + File.separator + lib_item.name;
                newInfo.use_api = lib_item.use_api;
                nativeLibs.add(newInfo);
            }
        }
        ret.native_libs = nativeLibs.toArray(new NModLibInfo[0]);
        return ret;
    }

    @Override
    public boolean isSupportedABI() {

        return false;
    }

    @Override
    public int getNModType() {
        return NMOD_TYPE_PACKAGED;
    }

    private String getNativeLibsPath() {
        return getPackageContext().getApplicationInfo().nativeLibraryDir;
    }

    private Context getPackageContext() {
        return mPackageContext;
    }

    public AssetManager getAssets() {
        return mPackageContext.getAssets();
    }

    public Bitmap createIcon() {
        try {
            PackageManager packageManager = getPackageContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageContext().getPackageName(), 0);
            int iconRes = packageInfo.applicationInfo.icon;
            return BitmapFactory.decodeResource(getPackageContext().getResources(), iconRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected InputStream createInfoInputStream() {
        try {
            return getAssets().open(MANIFEST_NAME);
        } catch (IOException e) {
            return null;
        }
    }
}
