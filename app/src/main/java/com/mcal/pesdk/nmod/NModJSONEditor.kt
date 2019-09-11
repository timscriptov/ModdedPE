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
import org.json.JSONException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class NModJSONEditor(context: Context, private val mTargetNMod: NMod, private val mParents: Array<File>) {
    private val mManager: NModFilePathManager

    init {
        mManager = NModFilePathManager(context)
    }

    @Throws(IOException::class, JSONException::class)
    fun edit(): File {
        val dir = mManager.nModJsonDir
        dir.mkdirs()
        val file = mManager.getNModJsonPath(mTargetNMod)
        file.createNewFile()
        val zipOutPut = ZipOutputStream(FileOutputStream(file))
        zipOutPut.putNextEntry(ZipEntry("AndroidManifest.xml"))
        zipOutPut.closeEntry()
        for (jsonEdit in mTargetNMod.info!!.json_edit!!) {
            val src = readJsonFromParents(jsonEdit.path)
            val srcThis = readJsonFromThis(jsonEdit.path)
            if (jsonEdit.mode == NMod.NModJsonEditBean.MODE_REPLACE) {
                zipOutPut.putNextEntry(ZipEntry("assets" + File.separator + jsonEdit.path))
                zipOutPut.write(srcThis.toByteArray())
                zipOutPut.closeEntry()
            } else if (jsonEdit.mode == NMod.NModJsonEditBean.MODE_MERGE) {
                val merger = JSONMerger(src, srcThis)
                val result = merger.merge()
                zipOutPut.putNextEntry(ZipEntry("assets" + File.separator + jsonEdit.path))
                zipOutPut.write(result.toByteArray())
                zipOutPut.closeEntry()
            }
        }
        zipOutPut.close()
        return file
    }

    @Throws(IOException::class)
    private fun readJsonFromParents(path: String?): String {
        for (index in mParents.indices.reversed()) {
            val parentItem = mParents[index]
            val zipFile = ZipFile(parentItem)
            val entry = zipFile.getEntry("assets" + File.separator + path) ?: continue
            val input = zipFile.getInputStream(entry)
            var tmp = ""
            input.bufferedReader().use {
                tmp = it.readText()
            }
            return tmp
        }
        throw FileNotFoundException(path)
    }

    @Throws(IOException::class)
    private fun readJsonFromThis(path: String?): String {
        val input = mTargetNMod.assets?.open(path!!)
        var tmp = ""
        input!!.bufferedReader().use {
            tmp = it.readText()
        }
        return tmp
    }
}
