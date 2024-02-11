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
        val deviceABI = ABIHelper.getABI()
        val abi = if (deviceABI == "x86_64") {
            "x86"
        } else {
            deviceABI
        }
        extractLibArchive(abi)
        extractLibraries(abi)
    }

    private fun extractLibArchive(abi: String) {
        runCatching {
            val nativeDir = File("${context.filesDir}/native")
            if (!nativeDir.exists()) {
                nativeDir.mkdirs()
            }
            val archive = File(nativeDir, "libgame.zip")
            if (isAppBundle()) {
                context.applicationInfo.splitPublicSourceDirs?.forEach { path ->
                    val name = File(path).name
                    if (name.contains("arm") || name.contains("x86")) {
                        ZipFile(path).getInputStream(ZipEntry("lib/$abi/libgame.so"))?.use {
                            FileHelper.writeToFile(archive, it)
                        }
                    }
                }
            } else {
                ZipFile(context.applicationInfo.sourceDir).getInputStream(
                    ZipEntry("lib/$abi/libgame.so")
                )?.use {
                    FileHelper.writeToFile(archive, it)
                }
            }
        }
    }

    private fun isAppBundle(): Boolean {
        return !context.applicationInfo.splitPublicSourceDirs.isNullOrEmpty()
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun extractLibraries(abi: String) {
        val archive = File("${context.filesDir}/native/libgame.zip")
        val outDir = File("${context.filesDir}/native/$abi/")
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        arrayListOf(
            "libc++_shared.so",
            "libminecraftpe.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
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
}
