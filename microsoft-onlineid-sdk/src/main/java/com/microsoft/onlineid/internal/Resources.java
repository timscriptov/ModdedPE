package com.microsoft.onlineid.internal;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import java.util.Locale;

public class Resources {
    private final Context _appContext;

    public Resources(Context appContext) {
        this._appContext = appContext;
    }

    public String getString(String name) {
        try {
            return this._appContext.getString(getIdentifierByType(name, "string"));
        } catch (NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "String resource with name %s not found", new Object[]{name}));
            return null;
        }
    }

    public int getDimensionPixelSize(String name) {
        int i = 0;
        try {
            i = this._appContext.getResources().getDimensionPixelSize(getIdentifierByType(name, "dimen"));
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Dimen resource with name %s not found", new Object[]{name}));
        }
        return i;
    }

    public int getLayout(String name) {
        int i = 0;
        try {
            i = getIdentifierByType(name, "layout");
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Layout resource with name %s not found", new Object[]{name}));
        }
        return i;
    }

    public int getId(String name) {
        int i = 0;
        try {
            i = getIdentifierByType(name, "id");
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Id resource with name %s not found", new Object[]{name}));
        }
        return i;
    }

    public int getMenu(String name) {
        int i = 0;
        try {
            i = getIdentifierByType(name, "menu");
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "Menu resource with name %s not found", new Object[]{name}));
        }
        return i;
    }

    public static String getString(Context appContext, String name) {
        return new Resources(appContext).getString(name);
    }

    public String getSdkVersion() {
        return getString("sdk_version_name");
    }

    public static String getSdkVersion(Context appContext) {
        return new Resources(appContext).getSdkVersion();
    }

    private int getIdentifierByType(String name, String type) {
        int i = 0;
        try {
            i = this._appContext.getResources().getIdentifier(name, type, this._appContext.getPackageName());
        } catch (NotFoundException e) {
            Assertion.check(i, String.format(Locale.US, "%s resource with name %s not found", new Object[]{type, name}));
        }
        return i;
    }
}
