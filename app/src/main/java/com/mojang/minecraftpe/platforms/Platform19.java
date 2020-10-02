package com.mojang.minecraftpe.platforms;

import android.annotation.TargetApi;
import android.os.Handler;
import android.view.View;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

@TargetApi(19)
public class Platform19 extends Platform9 {
    public Runnable decorViewSettings;
    public View decoreView;
    public Handler eventHandler;

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
            decoreView.setOnSystemUiVisibilityChangeListener(visibility -> eventHandler.postDelayed(decorViewSettings, 500));
            decorViewSettings = () -> decoreView.setSystemUiVisibility(5894);
            eventHandler.post(decorViewSettings);
        }
    }

    public void onViewFocusChanged(boolean hasFocus) {
        if (eventHandler != null && hasFocus) {
            eventHandler.postDelayed(decorViewSettings, 500);
        }
    }
}