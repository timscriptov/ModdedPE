package com.mcal.moddedpe.task

import android.annotation.SuppressLint
import android.content.Context
import com.mcal.moddedpe.utils.ABIHelper
import com.mcal.moddedpe.utils.FileHelper
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class NativeInstaller(
    private val context: Context
) {
    fun install() {
        val abi = getDeviceABI()
        extractLibArchive(abi)
        extractLibraries(abi)
    }

    private fun getDeviceABI(): String {
        val deviceABI = ABIHelper.getABI()
        return if (deviceABI == "x86_64") "x86" else deviceABI
    }

    private fun extractLibArchive(abi: String) {
        runCatching {
            val nativeDir = File("${context.filesDir}/native").apply {
                if (!exists()) mkdirs()
            }
            val archive = File(nativeDir, "libgame.zip")
            if (isAppBundle()) {
                extractFromAppBundle(abi, archive)
            } else {
                extractFromAPK(abi, archive)
            }
        }
    }

    private fun extractFromAppBundle(abi: String, archive: File) {
        context.applicationInfo.splitPublicSourceDirs?.forEach { path ->
            val name = File(path).name
            if (name.contains("arm") || name.contains("x86")) {
                ZipFile(path).getInputStream(ZipEntry("lib/$abi/libgame.so"))?.use {
                    FileHelper.writeToFile(archive, it)
                }
            }
        }
    }

    private fun extractFromAPK(abi: String, archive: File) {
        ZipFile(context.applicationInfo.sourceDir).getInputStream(
            ZipEntry("lib/$abi/libgame.so")
        )?.use {
            FileHelper.writeToFile(archive, it)
        }
    }

    private fun isAppBundle(): Boolean {
        return !context.applicationInfo.splitPublicSourceDirs.isNullOrEmpty()
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun extractLibraries(abi: String) {
        val archive = File("${context.filesDir}/native/libgame.zip")
        val outDir = File("${context.filesDir}/native/$abi/").apply {
            if (!exists()) mkdirs()
        }
        listOf(
            "libc++_shared.so",
            "libminecraftpe.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
            extractLibrary(archive, outDir, libName)
        }
    }

    private fun extractLibrary(archive: File, outDir: File, libName: String) {
        val libFile = File(outDir, libName)
        ZipFile(archive).getInputStream(ZipEntry(libName))?.use {
            FileHelper.writeToFile(libFile, it)
        }
        libFile.apply {
            setExecutable(true)
            setReadable(true)
            setWritable(true)
        }
    }
}