/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.minecraftpe.platforms;

import android.annotation.TargetApi;
import android.os.Handler;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

@TargetApi(19)
public class Platform19 extends Platform9 {
    private Runnable decorViewSettings;
    private View decoreView;
    private Handler eventHandler;

    public Platform19(boolean initEventHandler) {
        if (initEventHandler) {
            eventHandler = new Handler();
        }
    }

    public void onVolumePressed() {
    }

    public void onAppStart(View view) {
        if (eventHandler != null) {
            decoreView = view;
            decoreView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
                public void onSystemUiVisibilityChange(int visibility) {
                    eventHandler.postDelayed(decorViewSettings, 500);
                }
            });
            decorViewSettings = new Runnable() {
                public void run() {
                    decoreView.setSystemUiVisibility(5894);
                }
            };
            eventHandler.post(decorViewSettings);
        }
    }

    public void onViewFocusChanged(boolean hasFocus) {
        if (eventHandler != null && hasFocus) {
            eventHandler.postDelayed(decorViewSettings, 500);
        }
    }
}
