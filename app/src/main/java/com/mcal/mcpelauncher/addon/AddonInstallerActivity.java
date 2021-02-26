package com.mcal.mcpelauncher.addon;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.addon.utils.ASplitParser;
import com.mcal.mcpelauncher.utils.ExceptionHandler;
import com.mcal.pesdk.nativeapi.LibraryLoader;

public class AddonInstallerActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.moddedpe_addon_installer);

        ASplitParser.parse(this);
        LibraryLoader.loadCppShared(ASplitParser.getMinecraftPackageNativeLibraryDir(this));
        LibraryLoader.loadFMod(ASplitParser.getMinecraftPackageNativeLibraryDir(this));
        LibraryLoader.loadMinecraftPE(ASplitParser.getMinecraftPackageNativeLibraryDir(this));
        LibraryLoader.loadLauncher(ASplitParser.getMinecraftPackageNativeLibraryDir(this));

        Intent intent = new Intent(AddonInstallerActivity.this, MinecraftLoaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
