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
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import com.mcal.pesdk3.data.ExtractFailedException
import com.mcal.pesdk3.data.NModInfo
import kotlinx.serialization.json.Json
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import androidx.core.graphics.createBitmap

class NModExtractor(
    private val context: Context
) {
    @Throws(ExtractFailedException::class)
    fun archiveFromInstalledPackage(packageName: String): PackagedNMod {
        return try {
            val contextPackage = context.createPackageContext(
                packageName,
                Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
            )
            contextPackage.assets.open(NMod.MANIFEST_NAME).close()
            PackagedNMod(packageName, context, contextPackage)
        } catch (e: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_NO_MANIFEST, e)
        } catch (notFoundE: PackageManager.NameNotFoundException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_PACKAGE_NOT_FOUND, notFoundE)
        }
    }

    @Throws(ExtractFailedException::class)
    fun archiveFromZipped(path: String): ZippedNMod {
        val packageManager = context.packageManager

        var packageInfo: PackageInfo? = null
        runCatching {
            packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_CONFIGURATIONS)
        }.onFailure {
            it.printStackTrace()
        }


        val nModInfo = archiveInfoFromZipped(File(path))

        val nNodDir = NModFilePathManager(context).getNModCacheDir()
        nNodDir.mkdirs()

        return if (packageInfo != null) {
            if (nModInfo.packageName != null && nModInfo.packageName != packageInfo.packageName) {
                throw ExtractFailedException(
                    ExtractFailedException.TYPE_INEQUAL_PACKAGE_NAME,
                    RuntimeException("Package name defined in AndroidManifest.xml and nmod_manifest.json must equal!")
                )
            }

            nModInfo.packageName = packageInfo.packageName

            try {
                val packageName = packageInfo.packageName
                packageInfo.applicationInfo?.let { applicationInfo ->
                    applicationInfo.sourceDir = path
                    applicationInfo.publicSourceDir = path
                }

                val nModFile = getFile(path, nModInfo, packageManager, packageInfo, packageName)
                ZippedNMod(packageName, context, copyCachedNModToData(nModFile, packageName))
            } catch (ioe: IOException) {
                throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
            }
        } else {
            if (nModInfo.packageName == null) {
                throw ExtractFailedException(
                    ExtractFailedException.TYPE_UNDEFINED_PACKAGE_NAME,
                    RuntimeException("Undefined package name in manifest.")
                )
            }
            if (!PackageNameChecker.isValidPackageName(nModInfo.packageName)) {
                throw ExtractFailedException(
                    ExtractFailedException.TYPE_INVAILD_PACKAGE_NAME,
                    RuntimeException("The provided package name is not a valid java-styled package name.")
                )
            }

            try {
                val nModFile = getFile(path)
                ZippedNMod(nModInfo.packageName!!, context, copyCachedNModToData(nModFile, nModInfo.packageName!!))
            } catch (ioe: IOException) {
                throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
            }
        }
    }

    @Throws(IOException::class)
    private fun getFile(
        path: String,
        nModInfo: NModInfo,
        packageManager: PackageManager,
        packageInfo: PackageInfo,
        packageName: String
    ): File {
        val nModFile = createNModCacheFile()

        processZipFile(path, nModFile) { zipFile, zipInput, zipOutputStream ->
            copyNonManifestEntries(zipInput, zipFile, zipOutputStream)
            addManifestEntry(zipOutputStream, nModInfo, packageName, packageInfo)
            addIconEntry(zipOutputStream, packageManager, packageInfo)
        }

        return nModFile
    }

    @Throws(IOException::class)
    private fun getFile(path: String): File {
        val nModFile = createNModCacheFile()

        processZipFile(path, nModFile) { zipFile, zipInput, zipOutputStream ->
            copyAllEntries(zipInput, zipFile, zipOutputStream)
            addEmptyAndroidManifest(zipOutputStream)
        }

        return nModFile
    }

    @Throws(IOException::class)
    private fun createNModCacheFile(): File {
        val nModFile = NModFilePathManager(context).getNModCachePath()
        nModFile.createNewFile()
        return nModFile
    }

    @Throws(IOException::class)
    private fun processZipFile(
        sourcePath: String,
        targetFile: File,
        block: (ZipFile, ZipInputStream, ZipOutputStream) -> Unit
    ) {
        ZipFile(sourcePath).use { zipFile ->
            ZipInputStream(BufferedInputStream(FileInputStream(sourcePath))).use { zipInput ->
                ZipOutputStream(FileOutputStream(targetFile)).use { zipOutputStream ->
                    block(zipFile, zipInput, zipOutputStream)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyNonManifestEntries(
        zipInput: ZipInputStream,
        zipFile: ZipFile,
        zipOutputStream: ZipOutputStream
    ) {
        var entry: ZipEntry?
        while (zipInput.nextEntry.also { entry = it } != null) {
            if (entry != null && shouldCopyEntry(entry)) {
                copyZipEntry(entry, zipFile, zipOutputStream)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyAllEntries(
        zipInput: ZipInputStream,
        zipFile: ZipFile,
        zipOutputStream: ZipOutputStream
    ) {
        var entry: ZipEntry?
        while (zipInput.nextEntry.also { entry = it } != null) {
            if (entry != null && !entry.isDirectory) {
                copyZipEntry(entry, zipFile, zipOutputStream)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyZipEntry(
        entry: ZipEntry,
        zipFile: ZipFile,
        zipOutputStream: ZipOutputStream
    ) {
        zipOutputStream.putNextEntry(entry)
        zipFile.getInputStream(entry).use { from ->
            from.copyTo(zipOutputStream)
        }
        zipOutputStream.closeEntry()
    }

    private fun shouldCopyEntry(entry: ZipEntry): Boolean {
        return !entry.isDirectory && !isManifestFile(entry.name)
    }

    private fun isManifestFile(entryName: String): Boolean {
        return entryName == NMod.MANIFEST_NAME ||
                entryName.endsWith("${File.separator}${NMod.MANIFEST_NAME}")
    }

    @Throws(IOException::class)
    private fun addManifestEntry(
        zipOutputStream: ZipOutputStream,
        nModInfo: NModInfo,
        packageName: String,
        packageInfo: PackageInfo
    ) {
        nModInfo.packageName = packageName
        nModInfo.versionCode = getVersionCode(packageInfo)
        nModInfo.versionName = packageInfo.versionName

        zipOutputStream.putNextEntry(ZipEntry(NMod.MANIFEST_NAME))
        zipOutputStream.write(Json.encodeToString(NModInfo.serializer(), nModInfo).toByteArray())
        zipOutputStream.closeEntry()
    }

    @Throws(IOException::class)
    private fun addIconEntry(
        zipOutputStream: ZipOutputStream,
        packageManager: PackageManager,
        packageInfo: PackageInfo
    ) {
        packageInfo.applicationInfo?.let { applicationInfo ->
            val icon = packageManager.getApplicationIcon(applicationInfo)
            val bitmap = createBitmapFromIcon(icon)

            zipOutputStream.putNextEntry(ZipEntry("icon.png"))
            ByteArrayOutputStream().use { baos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                zipOutputStream.write(baos.toByteArray())
            }
            zipOutputStream.closeEntry()
            zipOutputStream.flush()
        }
    }

    @Suppress("DEPRECATION")
    private fun createBitmapFromIcon(icon: Drawable): Bitmap {
        val bitmap = createBitmap(
            icon.intrinsicWidth,
            icon.intrinsicHeight,
            if (icon.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        icon.draw(canvas)
        return bitmap
    }

    @Throws(IOException::class)
    private fun addEmptyAndroidManifest(zipOutputStream: ZipOutputStream) {
        val entryManifest = ZipEntry("AndroidManifest.xml")
        zipOutputStream.putNextEntry(entryManifest)
        zipOutputStream.closeEntry()
        zipOutputStream.flush()
    }

    private fun getVersionCode(packageInfo: PackageInfo): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val longVersionCode = packageInfo.longVersionCode
            when {
                longVersionCode > Int.MAX_VALUE -> Int.MAX_VALUE
                longVersionCode < Int.MIN_VALUE -> Int.MIN_VALUE
                else -> longVersionCode.toInt()
            }
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode
        }
    }

    @Throws(ExtractFailedException::class)
    private fun copyCachedNModToData(cachedNModFile: File, packageName: String): File {
        return try {
            val finalFileDir = NModFilePathManager(context).getNModsDir()
            finalFileDir.mkdirs()
            val finalFile = File(NModFilePathManager(context).getNModsDir().toString() + File.separator + packageName)
            finalFile.createNewFile()

            FileOutputStream(finalFile).use { finalFileOutput ->
                FileInputStream(cachedNModFile).use { fileInput ->
                    fileInput.copyTo(finalFileOutput)
                }
            }

            cachedNModFile.delete()
            finalFile
        } catch (ioe: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_IO_EXCEPTION, ioe)
        }
    }

    fun archiveAllFromInstalled(): ArrayList<NMod> {
        val packageManager = context.packageManager
        val infos = packageManager.getInstalledPackages(0)
        val list = ArrayList<NMod>()
        for (info in infos) {
            try {
                val packagedNMod = archiveFromInstalledPackage(info.packageName)
                list.add(packagedNMod)
            } catch (e: ExtractFailedException) {
                e.printStackTrace()
            }
        }
        return list
    }

    @Throws(ExtractFailedException::class)
    private fun archiveInfoFromZipped(filePath: File): NModInfo {
        try {
            ZipFile(filePath).use { zipFile ->
                val manifest1 = zipFile.getEntry(NMod.MANIFEST_NAME)
                val manifest2 = zipFile.getEntry("assets${File.separator}${NMod.MANIFEST_NAME}")
                if (manifest1 != null && manifest2 != null) {
                    throw ExtractFailedException(
                        ExtractFailedException.TYPE_REDUNDANT_MANIFEST,
                        RuntimeException("NModAPI found two nmod_manifest.json in this file but didn't know which one to read.Please delete one.(/nmod_manifest.json or /assets/nmod_manifest.json)")
                    )
                }
                if (manifest1 == null && manifest2 == null) {
                    throw ExtractFailedException(
                        ExtractFailedException.TYPE_NO_MANIFEST,
                        RuntimeException("There is no nmod_manifest.json found in this file.")
                    )
                }
                val manifest = manifest1 ?: manifest2
                zipFile.getInputStream(manifest).use { input ->
                    return try {
                        val content = input.reader().readText()
                        Json.decodeFromString(NModInfo.serializer(), content)
                    } catch (e: Exception) {
                        throw ExtractFailedException(ExtractFailedException.TYPE_JSON_SYNTAX_EXCEPTION, e)
                    }
                }
            }
        } catch (e: IOException) {
            throw ExtractFailedException(ExtractFailedException.TYPE_DECODE_FAILED, e)
        }
    }
}
