package com.mojang.minecraftpe.platforms;

import android.os.Build;
import android.view.View;

public class Platform9 extends Platform {
    public void onVolumePressed() {
    }

    public void onAppStart(View view) {
    }

    public void onViewFocusChanged(boolean hasFocus) {
    }

    public String getABIS() {
        return Build.CPU_ABI;
    }
}