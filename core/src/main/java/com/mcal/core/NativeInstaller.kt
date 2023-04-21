package com.mcal.core

import android.annotation.SuppressLint
import android.content.Context
import com.mcal.core.data.StorageHelper.getNativeLibrariesFile
import com.mcal.core.data.StorageHelper.nativeDir
import com.mcal.core.utils.ABIHelper.getNameAppABI
import com.mcal.core.utils.FileHelper
import com.mcal.core.utils.Patcher
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class NativeInstaller(private val context: Context) {
    fun install() {
        val nativeDir = nativeDir(context)
        val abi = getNameAppABI(context)
        if (abi.contains("arm64-v8a") || (abi.contains("armeabi-v7a")) ||
            (abi.contains("x86_64")) || (abi.contains("x86"))
        ) {
            extractLibraries(nativeDir)
            patchingMinecraftLibrary()
            loadLibraries()
        }
        try {
            Patcher.patchNativeLibraryDir(
                context.classLoader,
                nativeDir
            )
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun extractLibraries(nativeDir: File) {
        val tmpLibLokiCraftFile = getNativeLibrariesFile(context)
        arrayListOf(
            "libc++_shared.so",
            "libfmod.so",
            "liblokicraft.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
            val libFilePath = if (libName.contains("liblokicraft.so")) {
                File(nativeDir, "libminecraftpe.so")
            } else {
                File(nativeDir, libName)
            }
            ZipFile(tmpLibLokiCraftFile).getInputStream(ZipEntry(libName))
                ?.let {
                    FileHelper.writeToFile(libFilePath, it)
                }
        }
    }

    private fun patchingMinecraftLibrary() {
        val libraryFile = getNativeLibrariesFile(context)

        val libraryBytes = libraryFile.readBytes()
        val origImgBytes = context.assets.open("resources/title_original.png").readBytes()
        val newImgBytes = context.assets.open("resources/title_replace.png").readBytes()

        val indexOfOrigImg = hexIndexOf(libraryBytes, origImgBytes)
        if (indexOfOrigImg == -1) {
            println("title.png not found in libminecraftpe.so")
            return
        }

        val libraryPatchedBytes = libraryBytes.copyOf()
        System.arraycopy(newImgBytes, 0, libraryPatchedBytes, indexOfOrigImg, newImgBytes.size)

        FileHelper.writeToFile(libraryFile, libraryPatchedBytes)
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun loadLibraries() {
        nativeDir(context).walk().maxDepth(1).filter { it.isFile }.forEach {
            if (it.name.endsWith(".so")) {
                System.load(it.path)
            }
        }
    }

    private fun hexIndexOf(source: ByteArray, target: ByteArray): Int {
        outer@ for (i in 0..source.size - target.size) {
            for (j in target.indices) {
                if (source[i + j] != target[j]) {
                    continue@outer
                }
            }
            return i
        }
        return -1
    }
}