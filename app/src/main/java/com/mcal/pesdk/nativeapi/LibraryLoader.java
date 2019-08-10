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
package com.mcal.pesdk.nativeapi;

import java.io.File;
import java.io.IOException;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class LibraryLoader {
    private static final String FMOD_LIB_NAME = "libfmod.so";
    private static final String MINECRAFTPE_LIB_NAME = "libminecraftpe.so";

    private static final String API_NAME = "nmod-core";
    private static final String SUBSTRATE_NAME = "substrate";
    private static final String LAUNCHER_NAME = "launcher-core";

    static public void loadSubstrate() {
        System.loadLibrary(SUBSTRATE_NAME);
    }

    static public void loadLauncher(String mcLibsPath) {
        System.loadLibrary(LAUNCHER_NAME);
        nativeOnLauncherLoaded(mcLibsPath + File.separator + MINECRAFTPE_LIB_NAME);
    }

    static public void loadFMod(String mcLibsPath) {
        System.load(new File(mcLibsPath, FMOD_LIB_NAME).getAbsolutePath());
    }

    static public void loadMinecraftPE(String mcLibsPath) {
        System.load(new File(mcLibsPath, MINECRAFTPE_LIB_NAME).getAbsolutePath());
    }

    static public void loadNModAPI(String mcLibsPath) {
        System.loadLibrary(API_NAME);
        nativeOnNModAPILoaded(mcLibsPath + File.separator + MINECRAFTPE_LIB_NAME);
    }

    private static native void nativeOnLauncherLoaded(String libPath);
    private static native void nativeOnNModAPILoaded(String libPath);
}
