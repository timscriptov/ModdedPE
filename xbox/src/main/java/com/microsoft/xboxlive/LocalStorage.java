package com.microsoft.xboxlive;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class LocalStorage {
    @NotNull
    public static String getPath(@NotNull Context context) {
        return context.getFilesDir().getPath();
    }
}