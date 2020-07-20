/*
 * Copyright (C) 2018-2020 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.pesdk.nativeapi;

import android.content.Context;

import com.mcal.pesdk.utils.LauncherOptions;

import java.io.File;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NativeUtils {
    static {
        nativeRegisterNatives(NativeUtils.class);
    }

    public static native void nativeSetDataDirectory(String directory);

    public static native String nativeDemangle(String symbol_name);

    public static native void nativeRegisterNatives(Class cls);

    public static void setValues(Context context, LauncherOptions options) {
        if (options.getDataSavedPath().equals(LauncherOptions.STRING_VALUE_DEFAULT)) {
            NativeUtils.nativeSetDataDirectory(context.getFilesDir().getAbsolutePath() + File.separator);
        } else {
            String pathStr = options.getDataSavedPath();
            if (!pathStr.endsWith(File.separator))
                pathStr += File.separator;
            NativeUtils.nativeSetDataDirectory(pathStr);
        }
    }
}
