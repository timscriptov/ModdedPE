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

import java.io.File
import java.io.IOException

object LibraryLoader {
    private val FMOD_LIB_NAME = "libfmod.so"
    private val MINECRAFTPE_LIB_NAME = "libminecraftpe.so"

    private val API_NAME = "nmod-core"
    private val SUBSTRATE_NAME = "substrate"
    private val LAUNCHER_NAME = "launcher-core"

    fun loadSubstrate() {
        System.loadLibrary(SUBSTRATE_NAME)
    }

    fun loadLauncher(mcLibsPath: String) {
        System.loadLibrary(LAUNCHER_NAME)
        nativeOnLauncherLoaded(mcLibsPath + File.separator + MINECRAFTPE_LIB_NAME)
    }

    @Throws(IOException::class)
    fun loadFMod(mcLibsPath: String) {
        System.load(File(mcLibsPath, FMOD_LIB_NAME).absolutePath)
    }

    @Throws(IOException::class)
    fun loadMinecraftPE(mcLibsPath: String) {
        System.load(File(mcLibsPath, MINECRAFTPE_LIB_NAME).absolutePath)
    }

    fun loadNModAPI(mcLibsPath: String) {
        System.loadLibrary(API_NAME)
        nativeOnNModAPILoaded(mcLibsPath + File.separator + MINECRAFTPE_LIB_NAME)
    }

    private external fun nativeOnLauncherLoaded(libPath: String)

    private external fun nativeOnNModAPILoaded(libPath: String)
}
