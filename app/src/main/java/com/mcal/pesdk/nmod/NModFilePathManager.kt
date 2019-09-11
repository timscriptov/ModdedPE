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
package com.mcal.pesdk.nmod

import android.content.Context

import java.io.File

internal class NModFilePathManager(private val mContext: Context) {

    val nModsDir: File
        get() = File(mContext.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_PACKS)

    val nModJsonDir: File
        get() = File(mContext.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_JSON_PACKS)

    val nModTextDir: File
        get() = File(mContext.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_TEXT_PACKS)

    val nModLibsDir: File
        get() = File(mContext.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_LIBS)

    val nModCacheDir: File
        get() = File(mContext.cacheDir.absolutePath)

    val nModCachePath: File
        get() = File(mContext.cacheDir.absolutePath + File.separator + FILEPATH_FILE_NAME_NMOD_CAHCHE)

    val nModIconDir: File
        get() = File(mContext.filesDir.absolutePath + File.separator + FILEPATH_DIR_NAME_NMOD_ICON)

    fun getNModJsonPath(nmod: NMod): File {
        return File(nModJsonDir, nmod.packageName)
    }

    fun getNModTextPath(nmod: NMod): File {
        return File(nModTextDir, nmod.packageName)
    }

    fun getNModIconPath(nmod: NMod): File {
        return File(nModIconDir.absolutePath + File.separator + nmod.packageName)
    }

    companion object {
        private val FILEPATH_DIR_NAME_NMOD_PACKS = "nmod_packs"
        private val FILEPATH_DIR_NAME_NMOD_LIBS = "nmod_libs"
        private val FILEPATH_DIR_NAME_NMOD_ICON = "nmod_icon"
        private val FILEPATH_FILE_NAME_NMOD_CAHCHE = "nmod_cached"
        private val FILEPATH_DIR_NAME_NMOD_JSON_PACKS = "nmod_json_packs"
        private val FILEPATH_DIR_NAME_NMOD_TEXT_PACKS = "nmod_text_packs"
    }
}
