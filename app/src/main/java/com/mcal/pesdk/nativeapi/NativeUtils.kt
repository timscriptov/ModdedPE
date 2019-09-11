/*
 * Copyright (C) 2018-2019 Тимашков Иван
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
package com.mcal.pesdk.nativeapi

import android.content.Context

import com.mcal.pesdk.utils.LauncherOptions

import java.io.File

object NativeUtils {
    init {
        nativeRegisterNatives(NativeUtils::class.java)
    }

    external fun nativeIsGameStarted(): Boolean

    external fun nativeSetDataDirectory(directory: String)

    external fun nativeDemangle(symbol_name: String): String

    external fun nativeRegisterNatives(cls: Class<*>)

    fun setValues(context: Context, options: LauncherOptions) {
        if (options.dataSavedPath == LauncherOptions.STRING_VALUE_DEFAULT) {
            nativeSetDataDirectory(context.filesDir.absolutePath + File.separator)
        } else {
            var pathStr = options.dataSavedPath
            if (!pathStr.endsWith(File.separator))
                pathStr += File.separator
            nativeSetDataDirectory(pathStr)
        }
    }
}
