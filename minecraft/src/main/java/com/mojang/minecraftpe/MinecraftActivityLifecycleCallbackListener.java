package com.mojang.minecraftpe;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class MinecraftActivityLifecycleCallbackListener implements Application.ActivityLifecycleCallbacks {
    native void nativeDisableBraze();

    native void nativeEnableBraze();

    native boolean nativeNeedsIntegrityCheck();

    native void nativePlayIntegrityComplete(int errorCode, String packageName, String appRecognitionVerdict, String deviceIntegrity, String appLicensingVerdict);

    native void nativeSetBrazeReady(boolean isReady);

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(final Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }
}