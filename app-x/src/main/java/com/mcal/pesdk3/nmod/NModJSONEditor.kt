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
import com.mcal.pesdk3.data.NModJsonEditBean
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class NModJSONEditor(
    private val context: Context,
    private val targetNMod: NMod,
    private val parents: Array<File>
) {
    private val manager = NModFilePathManager(context)

    @Throws(IOException::class, IllegalArgumentException::class)
    fun edit(): File {
        val dir = manager.getNModJsonDir()
        dir.mkdirs()
        val file = manager.getNModJsonPath(targetNMod)
        file.createNewFile()

        ZipOutputStream(FileOutputStream(file)).use { zipOutput ->
            // Add required AndroidManifest.xml entry
            zipOutput.putNextEntry(ZipEntry("AndroidManifest.xml"))
            zipOutput.closeEntry()

            targetNMod.getInfo()?.jsonEdit?.forEach { jsonEdit ->
                jsonEdit.path?.let { path ->
                    when (jsonEdit.mode) {
                        NModJsonEditBean.Companion.MODE_REPLACE -> {
                            val content = readJsonFromThis(path)
                            zipOutput.putNextEntry(ZipEntry("assets${File.separator}${path}"))
                            zipOutput.write(content.toByteArray())
                            zipOutput.closeEntry()
                        }

                        NModJsonEditBean.Companion.MODE_MERGE -> {
                            val parentContent = readJsonFromParents(path)
                            val thisContent = readJsonFromThis(path)
                            val merger = JSONMerger(parentContent, thisContent)
                            val result = merger.merge()
                            zipOutput.putNextEntry(ZipEntry("assets${File.separator}${path}"))
                            zipOutput.write(result.toByteArray())
                            zipOutput.closeEntry()
                        }

                        else -> throw IllegalArgumentException("Unknown JSON edit mode: ${jsonEdit.mode}")
                    }
                }
            }
        }
        return file
    }

    @Throws(IOException::class)
    private fun readJsonFromParents(path: String): String {
        for (index in parents.indices.reversed()) {
            val parentItem = parents[index]
            ZipFile(parentItem).use { zipFile ->
                val entry = zipFile.getEntry("assets${File.separator}$path") ?: continue
                zipFile.getInputStream(entry).use { input ->
                    return input.reader().readText()
                }
            }
        }
        throw FileNotFoundException("JSON file not found in parents: $path")
    }

    @Throws(IOException::class)
    private fun readJsonFromThis(path: String): String {
        targetNMod.getAssets().open(path).use { input ->
            return input.reader().readText()
        }
    }
}