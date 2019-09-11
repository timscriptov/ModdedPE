/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.minecraftpe.platforms;

import android.os.Build.VERSION;
import android.view.View;

public abstract class Platform {
    public static Platform createPlatform(boolean initEventHandler) {
        if (VERSION.SDK_INT >= 19) {
            return new Platform19(initEventHandler);
        }
        if (VERSION.SDK_INT >= 21) {
            return new Platform21(initEventHandler);
        }
        return new Platform9();
    }

    public abstract String getABIS();

    public abstract void onAppStart(View view);

    public abstract void onViewFocusChanged(boolean z);

    public abstract void onVolumePressed();
}
