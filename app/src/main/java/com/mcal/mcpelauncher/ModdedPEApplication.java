/*
 * Copyright (C) 2018-2020 Тимашков Иван
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
package com.mcal.mcpelauncher;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.PESdk;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class ModdedPEApplication extends Application {
    public static PESdk mPESdk;
    public static SharedPreferences preferences;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        if (context == null) {
            context = new ModdedPEApplication();
        }
        return context;
    }

    public void onCreate() {
        super.onCreate();
        context = this;
        mPESdk = new PESdk(this);
        if (Preferences.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public AssetManager getAssets() {
        return mPESdk.getMinecraftInfo().getAssets();
    }
}
