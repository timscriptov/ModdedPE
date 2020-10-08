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

import android.annotation.SuppressLint;

import java.io.File;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class LibraryLoader {
    private static native void nativeOnLauncherLoaded(String libPath);

    private static native void nativeOnNModAPILoaded(String libPath);

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    static public void loadSubstrate() {
        System.loadLibrary("substrate");
        //System.load(new File(mcLibsPath, "libsubstrate.so").getAbsolutePath());
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    static public void loadLauncher(String mcLibsPath) {
        System.loadLibrary("launcher-core");
        //System.load(new File(mcLibsPath, "liblauncher-core.so").getAbsolutePath());
        nativeOnLauncherLoaded(mcLibsPath + "/" + "libminecraftpe.so");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    static public void loadFMod(String mcLibsPath) {
        System.load(new File(mcLibsPath, "libfmod.so").getAbsolutePath());
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    static public void loadMinecraftPE(String mcLibsPath) {
        System.load(new File(mcLibsPath, "libminecraftpe.so").getAbsolutePath());
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    static public void loadCppShared(String mcLibsPath) {
        System.load(new File(mcLibsPath, "libc++_shared.so").getAbsolutePath());
        //System.loadLibrary("c++_shared");
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    static public void loadNModAPI(String mcLibsPath) {
        System.loadLibrary("nmod-core");
        //System.load(new File(mcLibsPath, "libnmod-core.so").getAbsolutePath());
        nativeOnNModAPILoaded(mcLibsPath + "/" + "libminecraftpe.so");
    }
}