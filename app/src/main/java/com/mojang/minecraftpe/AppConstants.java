package com.mojang.minecraftpe;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class AppConstants {
    public static String ANDROID_BUILD = null;
    public static String ANDROID_VERSION = null;
    public static String APP_PACKAGE = null;
    public static int APP_VERSION = 0;
    public static String APP_VERSION_NAME = null;
    public static String PHONE_MANUFACTURER = null;
    public static String PHONE_MODEL = null;
    private static AsyncTask<Void, Object, String> loadIdentifiersTask;

    public static void loadFromContext(Context context) {
        Log.i("MinecraftPlatform", "CrashManager: AppConstants loadFromContext started");
        ANDROID_VERSION = Build.VERSION.RELEASE;
        ANDROID_BUILD = Build.DISPLAY;
        PHONE_MODEL = Build.MODEL;
        PHONE_MANUFACTURER = Build.MANUFACTURER;
        loadPackageData(context);
    }

    private static void loadPackageData(Context context) {
        if (context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                APP_PACKAGE = packageInfo.packageName;
                APP_VERSION = packageInfo.versionCode;
                APP_VERSION_NAME = packageInfo.versionName;
                Log.i("MinecraftPlatform", "CrashManager: AppConstants loadFromContext finished succesfully");
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("MinecraftPlatform", "CrashManager: Exception thrown when accessing the package info", e);
            }
        }
    }
}