package com.microsoft.xal.androidjava;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Storage {
    @NotNull
    public static String getStoragePath(@NotNull Context context) {
        return context.getFilesDir().getPath();
    }
}
