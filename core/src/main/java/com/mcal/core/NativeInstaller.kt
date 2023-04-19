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
import java.util.zip.ZipFile

class NativeInstaller(private val context: Context) {

    fun install() {
        val nativeDir = nativeDir(context)
        val abi = getNameAppABI(context)
        if (abi.contains("arm64-v8a")) {
            extractNative(nativeDir)
        } else if (abi.contains("armeabi-v7a")) {
            extractNative(nativeDir)
        } else if (abi.contains("x86")) {
            extractNative(nativeDir)
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
    private fun extractNative(libDir: File) {
        val nativeLibrariesFile = getNativeLibrariesFile(context)
        arrayListOf(
            "libc++_shared.so",
            "libfmod.so",
            "libminecraftpe.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
            if (nativeLibrariesFile.exists()) {
                val nativeLibrariesZipFile = ZipFile(nativeLibrariesFile)
                nativeLibrariesZipFile.getInputStream(nativeLibrariesZipFile.getEntry(libName))
                    .use { input ->
                        val libFilePath = File(libDir, libName)
                        FileHelper.writeToFile(libFilePath, FileHelper.readAllBytes(input))
                        if (libFilePath.exists()) {
                            System.load(libFilePath.path)
                        }
                    }
            }
        }
    }
}