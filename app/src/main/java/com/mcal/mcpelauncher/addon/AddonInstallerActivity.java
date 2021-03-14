package com.mcal.mcpelauncher.addon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.mcal.pesdk.utils.ABIInfo;
import com.mcal.pesdk.utils.AssetOverrideManager;
import com.mcal.pesdk.utils.SplitParser;
import com.mojang.minecraftpe.MainActivity;

public class AddonInstallerActivity extends MainActivity {
    public static final String PACKAGE_NAME = "com.mojang.minecraftpe";

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Context mc = this.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            AssetOverrideManager.addAssetOverride(this.getAssets(), mc.getPackageResourcePath());
            SplitParser.parse(this);
        } catch (Error | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.load(this.getCacheDir().getPath() + "/lib/" + ABIInfo.getABI() + "/libc++_shared.so");
            System.load(this.getCacheDir().getPath() + "/lib/" + ABIInfo.getABI() + "/libfmod.so");
            System.load(this.getCacheDir().getPath() + "/lib/" + ABIInfo.getABI() + "/libminecraftpe.so");
            System.loadLibrary("launcher-core");
        }
    }
}
