package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class FontManager {
    private static FontManager instance = new FontManager();
    private HashMap<String, Typeface> fonts;

    public static FontManager Instance() {
        return instance;
    }

    public Typeface getTypeface(Context context, String typeface) {
        if (fonts == null) {
            fonts = new HashMap<>();
        }
        if (!fonts.containsKey(typeface)) {
            fonts.put(typeface, Typeface.createFromAsset(context.getAssets(), typeface));
        }
        return fonts.get(typeface);
    }
}
