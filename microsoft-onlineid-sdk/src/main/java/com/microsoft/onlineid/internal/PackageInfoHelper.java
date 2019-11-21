package com.microsoft.onlineid.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import com.microsoft.onlineid.sdk.BuildConfig;

public class PackageInfoHelper {
    public static final String AuthenticatorPackageName = "com.microsoft.msa.authenticator";

    public static int getCurrentAppVersionCode(Context applicationContext) {
        int i = 0;
        try {
            return applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Assertion.check(i);
            return i;
        }
    }

    public static String getCurrentAppVersionName(Context applicationContext) {
        try {
            return applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Assertion.check(false);
            return BuildConfig.VERSION_NAME;
        }
    }

    public static String getAppVersionName(Context applicationContext, String packageName) {
        try {
            return applicationContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static boolean isAuthenticatorApp(String packageName) {
        return AuthenticatorPackageName.equalsIgnoreCase(packageName);
    }

    public static boolean isRunningInAuthenticatorApp(Context applicationContext) {
        return isAuthenticatorApp(applicationContext.getPackageName());
    }

    public static boolean isAuthenticatorAppInstalled(Context applicationContext) {
        try {
            applicationContext.getPackageManager().getPackageInfo(AuthenticatorPackageName, 128);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isCurrentApp(String packageName, Context applicationContext) {
        return applicationContext.getPackageName().equalsIgnoreCase(packageName);
    }

    public static Signature[] getCurrentAppSignatures(Context applicationContext) {
        return getAppSignatures(applicationContext, applicationContext.getPackageName());
    }

    public static Signature[] getAppSignatures(Context applicationContext, String packageName) {
        try {
            return applicationContext.getPackageManager().getPackageInfo(packageName, 64).signatures;
        } catch (NameNotFoundException e) {
            Assertion.check(false);
            return new Signature[0];
        }
    }
}
