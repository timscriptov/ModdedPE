package com.microsoft.xboxlive;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

/**
 * 02.10.2020
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */

public class LocalStorage {
    @NotNull
    public static String getPath(@NotNull Context context) {
        return context.getFilesDir().getPath();
    }
}