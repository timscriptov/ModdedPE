package com.mojang.minecraftpe.platforms;

import android.os.Build;
import android.view.View;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class Platform {
    @NotNull
    @Contract("_ -> new")
    public static Platform createPlatform(boolean initEventHandler) {
        if (Build.VERSION.SDK_INT >= 19) {
            return new Platform19(initEventHandler);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return new Platform21(initEventHandler);
        }
        return new Platform9();
    }

    public abstract String getABIS();

    public abstract void onAppStart(View view);

    public abstract void onViewFocusChanged(boolean z);

    public abstract void onVolumePressed();
}