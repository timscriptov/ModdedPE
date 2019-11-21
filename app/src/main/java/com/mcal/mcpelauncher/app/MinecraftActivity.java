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
package com.mcal.mcpelauncher.app;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.mcal.mcpelauncher.ModdedPEApplication;
import com.mcal.pesdk.PESdk;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MinecraftActivity extends com.mojang.minecraftpe.MainActivity {
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
    }

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
