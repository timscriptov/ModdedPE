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
package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;

import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.mcpelauncher.services.SoundService;
import com.mcal.pesdk.PESdk;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MinecraftActivity extends com.mojang.minecraftpe.MainActivity {
    private ServiceConnection sc;
    private boolean bound, paused;
    private SoundService ss;

    protected PESdk getPESdk() {
        return ModdedPEApplication.mPESdk;
    }

    @Override
    public AssetManager getAssets() {
        return getPESdk().getGameManager().getAssets();
    }

    @Override
    public void onCreate(Bundle p1) {
        getPESdk().getGameManager().onMinecraftActivityCreate(this, p1);
        super.onCreate(p1);
        if (Preferences.isBackgroundMusic()) {
            sc = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName p1, IBinder p2) {
                    bound = true;
                    ss = ((SoundService.SoundBinder) p2).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName p1) {
                    bound = false;
                }
            };
            bindService(new Intent(getApplicationContext(), SoundService.class), sc, BIND_AUTO_CREATE);
        }
    }

    @Override
    public String getExternalStoragePath() {
        if (Preferences.isSafeMode()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return Preferences.getDataSavedPath();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bound && paused) {
            ss.play();
            paused = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound && !paused) {
            ss.pause();
            paused = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(sc);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}