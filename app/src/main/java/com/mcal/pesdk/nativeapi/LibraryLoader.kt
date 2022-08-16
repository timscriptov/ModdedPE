/*
 * Copyright (C) 2018-2021 Тимашков Иван
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

import android.annotation.SuppressLint
import java.io.File

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object LibraryLoader {
    private external fun nativeOnLauncherLoaded(libPath: String)
    private external fun nativeOnNModAPILoaded(libPath: String)

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadSubstrate() {
        System.loadLibrary("substrate")
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadXHook() {
        System.loadLibrary("xhook")
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadLauncher(mcLibsPath: String) {
        System.loadLibrary("launcher-core")
        nativeOnLauncherLoaded("$mcLibsPath/libminecraftpe.so")
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadFMod(mcLibsPath: String?) {
        try {
            System.load(File(mcLibsPath, "libfmod.so").absolutePath)
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadMediaDecoders(mcLibsPath: String?) {
        try {
            System.load(File(mcLibsPath, "libMediaDecoders_Android.so").absolutePath)
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadMinecraftPE(mcLibsPath: String?) {
        try {
            System.load(File(mcLibsPath, "libminecraftpe.so").absolutePath)
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadCppShared(mcLibsPath: String?) {
        try {
            System.load(File(mcLibsPath, "libc++_shared.so").absolutePath)
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadNModAPI(mcLibsPath: String) {
        System.loadLibrary("nmod-core")
        nativeOnNModAPILoaded("$mcLibsPath/libminecraftpe.so")
    }
}