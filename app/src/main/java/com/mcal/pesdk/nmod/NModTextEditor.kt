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
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class NModTextEditor(context: Context, private val mTargetNMod: NMod, private val mParents: Array<File>) {
    private val mManager: NModFilePathManager

    init {
        mManager = NModFilePathManager(context)
    }

    @Throws(IOException::class)
    fun edit(): File {
        val dir = mManager.nModTextDir
        dir.mkdirs()
        val file = mManager.getNModTextPath(mTargetNMod)
        file.createNewFile()
        val zipOutPut = ZipOutputStream(FileOutputStream(file))
        zipOutPut.putNextEntry(ZipEntry("AndroidManifest.xml"))
        zipOutPut.closeEntry()
        for (textEdit in mTargetNMod.info!!.text_edit!!) {
            val src = readTextFromParents(textEdit.path)
            val srcThis = readTextFromThis(textEdit.path)
            if (textEdit.mode == NMod.NModTextEditBean.MODE_REPLACE) {
                zipOutPut.putNextEntry(ZipEntry("assets" + File.separator + textEdit.path))
                zipOutPut.write(srcThis.toByteArray())
                zipOutPut.closeEntry()
            } else if (textEdit.mode == NMod.NModTextEditBean.MODE_APPEND) {
                val result = src + srcThis
                zipOutPut.putNextEntry(ZipEntry("assets" + File.separator + textEdit.path))
                zipOutPut.write(result.toByteArray())
                zipOutPut.closeEntry()
            } else if (textEdit.mode == NMod.NModTextEditBean.MODE_PREPEND) {
                val result = srcThis + src
                zipOutPut.putNextEntry(ZipEntry("assets" + File.separator + textEdit.path))
                zipOutPut.write(result.toByteArray())
                zipOutPut.closeEntry()
            }
        }
        zipOutPut.flush()
        zipOutPut.close()
        return file
    }

    @Throws(IOException::class)
    private fun readTextFromParents(path: String?): String {
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
    private fun readTextFromThis(path: String?): String {
        val input = mTargetNMod.assets!!.open(path!!)
        var tmp = ""
        input.bufferedReader().use {
            tmp = it.readText()
        }
        return tmp
    }
}
