package com.microsoft.onlineid.internal.ui;

import android.content.Context;
import android.graphics.Typeface;

public enum Fonts {
    SegoeUI("segoeui", null, 0),
    SegoeUILight("segoeuil", "sans-serif-thin", 0),
    SegoeUISemiBold("seguisb", null, 1);
    
    private String _fallbackFamilyName;
    private int _fallbackStyle;
    private String _filename;
    private boolean _loadFailed;
    private Typeface _typeface;

    private Fonts(String filename, String fallbackFamilyName, int fallbackStyle) {
        this._typeface = null;
        this._loadFailed = false;
        this._filename = filename;
        this._fallbackFamilyName = fallbackFamilyName;
        this._fallbackStyle = fallbackStyle;
    }

    public Typeface getTypeface(Context context) {
        Typeface typeface;
        boolean z = true;
        synchronized (this) {
            if (this._typeface == null && !this._loadFailed) {
                try {
                    this._typeface = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s.ttf", new Object[]{this._filename}));
                } catch (RuntimeException e) {
                    this._typeface = Typeface.create(this._fallbackFamilyName, this._fallbackStyle);
                }
                if (this._typeface != null) {
                    z = false;
                }
                this._loadFailed = z;
            }
            typeface = this._typeface;
        }
        return typeface;
    }
}
