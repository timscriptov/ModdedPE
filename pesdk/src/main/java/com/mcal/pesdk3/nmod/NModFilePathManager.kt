/*
 * Copyright (C) 2018-2025 Тимашков Иван
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
package com.mcal.pesdk3.nmod

import android.content.Context
import java.io.File

class NModFilePathManager(
    private val context: Context
) {
    companion object {
        private const val FILEPATH_DIR_NAME_NMOD_PACKS = "nmod_packs"
        private const val FILEPATH_DIR_NAME_NMOD_LIBS = "nmod_libs"
        private const val FILEPATH_DIR_NAME_NMOD_DEXES = "nmod_dexes"
        private const val FILEPATH_DIR_NAME_NMOD_ICON = "nmod_icon"
        private const val FILEPATH_FILE_NAME_NMOD_CAHCHE = "nmod_cached"
        private const val FILEPATH_DIR_NAME_NMOD_JSON_PACKS = "nmod_json_packs"
        private const val FILEPATH_DIR_NAME_NMOD_TEXT_PACKS = "nmod_text_packs"
    }

    fun getNModsDir(): File {
        return File(context.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_PACKS)
    }

    fun getNModJsonDir(): File {
        return File(context.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_JSON_PACKS)
    }

    fun getNModJsonPath(nMod: NMod): File {
        return File(getNModJsonDir(), nMod.getPackageName())
    }

    fun getNModTextDir(): File {
        return File(context.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_TEXT_PACKS)
    }

    fun getNModTextPath(nMod: NMod): File {
        return File(getNModTextDir(), nMod.getPackageName())
    }

    fun getNModLibsDir(): File {
        val dir = File(context.filesDir.absolutePath, FILEPATH_DIR_NAME_NMOD_LIBS)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getNModDexesDir(): File {
        val dir = File(context.filesDir.absolutePath, FILEPATH_DIR_NAME_NMOD_DEXES)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getNModCacheDir(): File {
        val dir = File(context.cacheDir.absolutePath)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getNModCachePath(): File {
        return File(context.cacheDir.absolutePath + File.separator + FILEPATH_FILE_NAME_NMOD_CAHCHE)
    }

    fun getNModIconDir(): File {
        val dir = File(context.filesDir.absolutePath, FILEPATH_DIR_NAME_NMOD_ICON)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getNModIconPath(nMod: NMod): File {
        return File(getNModIconDir().absolutePath + File.separator + nMod.getPackageName() + "_icon.png")
    }

    fun getNModBannerIconPath(nMod: NMod): File {
        return File(getNModIconDir().absolutePath + File.separator + nMod.getPackageName() + "_banner.png")
    }
}
