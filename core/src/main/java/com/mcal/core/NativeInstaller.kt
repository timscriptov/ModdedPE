package com.mcal.core

import android.annotation.SuppressLint
import android.content.Context
import com.mcal.core.data.StorageHelper.getTmpLibLokiCraftFile
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
            extractNative(abi, nativeDir)
        } else if (abi.contains("armeabi-v7a")) {
            extractNative(abi, nativeDir)
        } else if (abi.contains("x86")) {
            extractNative(abi, nativeDir)
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
    private fun extractNative(abi: String, libDir: File) {
        ZipFile(context.applicationInfo.publicSourceDir).let { zipApkFile ->
            val inputStreamLibLokiCraftSO =
                zipApkFile.getInputStream(zipApkFile.getEntry("lib/$abi/liblokicraft.so"))
            val tmpLibLokiCraftFile = getTmpLibLokiCraftFile(context)
            FileHelper.writeToFile(
                tmpLibLokiCraftFile,
                FileHelper.readAllBytes(inputStreamLibLokiCraftSO)
            )
            arrayListOf(
                "libc++_shared.so",
                "libfmod.so",
                "liblokicraft.so",
                "libMediaDecoders_Android.so"
            ).forEach { libName ->
                if (tmpLibLokiCraftFile.exists()) {
                    val zipLibsFile = ZipFile(tmpLibLokiCraftFile)
                    zipLibsFile.getInputStream(zipLibsFile.getEntry(libName))
                        .use { input ->
                            val libFilePath = if (libName.contains("liblokicraft.so")) {
                                File(libDir, "libminecraftpe.so")
                            } else {
                                File(libDir, libName)
                            }
                            FileHelper.writeToFile(libFilePath, FileHelper.readAllBytes(input))
                            if (libFilePath.exists()) {
                                System.load(libFilePath.path)
                            }
                        }
                }
            }
            tmpLibLokiCraftFile.delete()
        }
    }
}