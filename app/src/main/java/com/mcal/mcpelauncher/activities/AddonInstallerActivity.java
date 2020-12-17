package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;

import com.mcal.pesdk.PESdk;
import com.mcal.pesdk.utils.AssetOverrideManager;

import java.io.File;

public class AddonInstallerActivity extends com.mojang.minecraftpe.MainActivity {
    private PESdk mPESdk;

    AddonInstallerActivity(PESdk pesdk) {
        mPESdk = pesdk;
    }

    public AssetManager getAssets() {
        return mPESdk.getMinecraftInfo().getAssets();
    }

    @SuppressLint({"WrongConstant", "ResourceType", "UnsafeDynamicallyLoadedCode"})
    public void onCreate(Bundle savedInstanceState) {
        //SplitParser.parse(this);
        System.load(new File(this.getCacheDir().getPath() + "/lib/" + Build.CPU_ABI, "libc++_shared.so").getAbsolutePath());
        System.load(new File(this.getCacheDir().getPath() + "/lib/" + Build.CPU_ABI, "libfmod.so").getAbsolutePath());
        System.load(new File(this.getCacheDir().getPath() + "/lib/" + Build.CPU_ABI, "libminecraftpe.so").getAbsolutePath());
        System.loadLibrary("launcher-core");
        System.loadLibrary("nmod-core");
        System.loadLibrary("substrate");

        AssetOverrideManager.addAssetOverride(this.getAssets(), mPESdk.getMinecraftInfo().getMinecraftPackageContext().getPackageResourcePath());
        super.onCreate(savedInstanceState);
    }
}
