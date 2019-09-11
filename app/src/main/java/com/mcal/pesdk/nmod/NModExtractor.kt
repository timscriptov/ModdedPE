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
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*
import java.util.*
import java.util.zip.*

internal class NModExtractor(private val mContext: Context) {

    @Throws(ExtractFailedException::class)
    fun archiveFromInstalledPackage(packageName: String): PackagedNMod {
        try {
            val contextPackage = mContext.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
            contextPackage.assets.open(NMod.MANIFEST_NAME).close()
            return PackagedNMod(packageName, mContext, contextPackage)
        } catch (e: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_NO_MANIFEST, e)
        } catch (notFoundE: PackageManager.NameNotFoundException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_PACKAGE_NOT_FOUND, notFoundE)
        }

    }

    @Throws(ExtractFailedException::class)
    fun archiveFromZipped(path: String): ZippedNMod {
        try {
            ZipFile(File(path))
        } catch (zipE: ZipException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_DECODE_FAILED, zipE)
        } catch (ioe: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
        }

        val packageManager = mContext.packageManager
        val packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_CONFIGURATIONS)
        val nmodInfo = archiveInfoFromZipped(File(path))

        if (packageInfo != null) {
            if (nmodInfo.package_name != null && nmodInfo.package_name != packageInfo.packageName)
                throw ExtractFailedException(ExtractFailedException.TYPE_INEQUAL_PACKAGE_NAME, RuntimeException("Package name defined in AndroidManifest.xml and nmod_manifest.json must equal!"))

            nmodInfo.package_name = packageInfo.packageName

            try {
                val nmodDir = NModFilePathManager(mContext).nModCacheDir
                nmodDir.mkdirs()
                val toFile = NModFilePathManager(mContext).nModCachePath
                toFile.createNewFile()
                val zipFile = ZipFile(path)
                val packageName = packageInfo.packageName
                val versionName = packageInfo.versionName
                val versionCode = packageInfo.versionCode
                packageInfo.applicationInfo.sourceDir = path
                packageInfo.applicationInfo.publicSourceDir = path
                val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
                val zipOutputStream = ZipOutputStream(FileOutputStream(toFile))
                val zipInput = ZipInputStream(BufferedInputStream(FileInputStream(path)))
                var entry: ZipEntry? = null

                while ({ entry = zipInput.nextEntry; entry }() != null) {
                    if (!entry!!.isDirectory && !(entry?.name == NMod.MANIFEST_NAME || entry?.name!!.endsWith(File.separator + NMod.MANIFEST_NAME))) {
                        zipOutputStream.putNextEntry(entry)
                        val from = zipFile.getInputStream(entry)
                        var byteRead = 0
                        val buffer = ByteArray(1024)
                        while ({ byteRead = from.read(buffer); byteRead }() != -1) {
                            zipOutputStream.write(buffer, 0, byteRead)
                        }
                        from.close()
                        zipOutputStream.closeEntry()
                    }
                }

                //Manifest
                nmodInfo.package_name = packageName
                nmodInfo.version_code = versionCode
                nmodInfo.version_name = versionName
                zipOutputStream.putNextEntry(ZipEntry(NMod.MANIFEST_NAME))
                zipOutputStream.write(Gson().toJson(nmodInfo).toByteArray())
                zipOutputStream.closeEntry()

                //Icon
                val bitmap = Bitmap.createBitmap(icon.intrinsicWidth, icon.intrinsicHeight, if (icon.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
                val canvas = Canvas(bitmap)
                icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
                icon.draw(canvas)
                zipOutputStream.putNextEntry(ZipEntry("icon.png"))
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                zipOutputStream.write(baos.toByteArray())
                zipOutputStream.closeEntry()

                zipOutputStream.flush()
                zipOutputStream.close()
                zipInput.close()

                return ZippedNMod(packageName, mContext, copyCachedNModToData(toFile, packageName))
            } catch (ioe: IOException) {
                throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
            }

        } else {
            if (nmodInfo.package_name == null)
                throw ExtractFailedException(ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME, RuntimeException("Undefined package name in manifest."))
            if (!PackageNameChecker.isValidPackageName(nmodInfo.package_name))
                throw ExtractFailedException(ExtractFailedException.TYPE_INVAILD_PACKAGE_NAME, RuntimeException("The provided package name is not a valid java-styled package name."))

            try {
                val zipFile = ZipFile(path)
                val zipInput = ZipInputStream(BufferedInputStream(FileInputStream(path)))
                val dir = NModFilePathManager(mContext).nModCacheDir
                dir.mkdirs()
                val nmodFile = NModFilePathManager(mContext).nModCachePath
                nmodFile.createNewFile()
                val zipOutputStream = ZipOutputStream(FileOutputStream(nmodFile))
                var entry: ZipEntry? = null
                while ({ entry = zipInput.nextEntry; entry }() != null) {
                    if (!entry!!.isDirectory) {
                        zipOutputStream.putNextEntry(entry)
                        val from = zipFile.getInputStream(entry)
                        var byteRead = -1
                        val buffer = ByteArray(1024)
                        while ({ byteRead = from.read(buffer); byteRead }() != -1) {
                            zipOutputStream.write(buffer, 0, byteRead)
                        }
                        from.close()
                        zipOutputStream.closeEntry()
                    }
                }
                val entryManifest = ZipEntry("AndroidManifest.xml")
                zipOutputStream.putNextEntry(entryManifest)
                zipOutputStream.closeEntry()
                zipOutputStream.flush()
                zipOutputStream.close()
                zipInput.close()

                return ZippedNMod(nmodInfo.package_name!!, mContext, copyCachedNModToData(nmodFile, nmodInfo.package_name))
            } catch (ioe: IOException) {
                throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
            }

        }
    }

    @Throws(ExtractFailedException::class)
    private fun copyCachedNModToData(cachedNModFile: File, packageName: String?): File {
        try {
            val finalFileDir = NModFilePathManager(mContext).nModsDir
            finalFileDir.mkdirs()
            val finalFile = File(NModFilePathManager(mContext).nModsDir.toString() + File.separator + packageName)
            finalFile.createNewFile()
            val finalFileOutput = FileOutputStream(finalFile)
            val fileInput = FileInputStream(cachedNModFile)
            var byteRead = 0
            val buffer = ByteArray(1024)
            while ({ byteRead = fileInput.read(buffer); byteRead }() != -1) {
                finalFileOutput.write(buffer, 0, byteRead)
            }
            finalFileOutput.close()
            fileInput.close()
            cachedNModFile.delete()
            return finalFile
        } catch (ioe: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
        }

    }

    fun archiveAllFromInstalled(): ArrayList<NMod> {
        val packageManager = mContext.packageManager
        val infos = packageManager.getInstalledPackages(0)
        val list = ArrayList<NMod>()
        for (info in infos) {
            try {
                val packagedNMod = archiveFromInstalledPackage(info.packageName)
                list.add(packagedNMod)
            } catch (e: ExtractFailedException) {
            }

        }
        return list
    }

    @Throws(ExtractFailedException::class)
    private fun archiveInfoFromZipped(filePath: File): NMod.NModInfo {
        val zipFile: ZipFile
        try {
            zipFile = ZipFile(filePath)
        } catch (e: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_DECODE_FAILED, e)
        }

        val manifest1 = zipFile.getEntry(NMod.MANIFEST_NAME)
        val manifest2 = zipFile.getEntry("assets" + File.separator + NMod.MANIFEST_NAME)
        if (manifest1 != null && manifest2 != null)
            throw ExtractFailedException(ExtractFailedException.TYPE_REDUNDANT_MANIFEST, RuntimeException("NModAPI found two nmod_manifest.json in this file but didn't know which one to read.Please delete one.(/nmod_manifest.json or /assets/nmod_manifest.json)"))
        if (manifest1 == null && manifest2 == null)
            throw ExtractFailedException(ExtractFailedException.TYPE_NO_MANIFEST, RuntimeException("There is no nmod_manifest.json found in this file."))
        val manifest = manifest1 ?: manifest2
        try {
            val input = zipFile.getInputStream(manifest)
            var tmp = ""
            input.bufferedReader().use {
                tmp = it.readText()
            }
            return Gson().fromJson(tmp, NMod.NModInfo::class.java)
        } catch (ioe: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
        } catch (jsonSyntaxE: JsonSyntaxException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION, jsonSyntaxE)
        }

    }
}
