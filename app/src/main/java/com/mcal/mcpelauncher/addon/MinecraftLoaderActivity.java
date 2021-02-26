package com.mcal.mcpelauncher.addon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.mcal.mcpelauncher.addon.utils.ASplitParser;
import com.mcal.pesdk.nativeapi.LibraryLoader;
import com.mcal.pesdk.utils.AssetOverrideManager;
import com.mcal.pesdk.utils.MinecraftInfo;
import com.mcal.pesdk.utils.SplitParser;
import com.mojang.minecraftpe.MainActivity;

public class MinecraftLoaderActivity extends MainActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetOverrideManager.addAssetOverride(this.getAssets(), MinecraftInfo.getMinecraftPackageContext().getPackageResourcePath());

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
