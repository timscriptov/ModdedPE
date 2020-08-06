package com.mcal.pesdk.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class SplitParser {
    private var appinfo = ApplicationInfo()
    private var splitpath = ""
    private val arch = "arm64-v8a"
    private val solist = ArrayList<String>()
    private lateinit var context: Context

    fun setItems(context: Context) {
        this.context = context
        for (appinfo in context.packageManager.getInstalledApplications(0)) {
            if (appinfo.packageName.equals("com.mojang.minecraftpe")) {
                this.appinfo = appinfo
                break
            }
        }
        solist.add("libminecraftpe.so")
        solist.add("libc++_shared.so")
        solist.add("libfmod.so")
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun parse() {
        for (i in appinfo.splitPublicSourceDirs.indices) {
            splitpath = appinfo.splitPublicSourceDirs.get(i)
            break
        }
        val apk = ZipFile(splitpath)
        var inputStream: InputStream
        var outputStream: OutputStream
        val buffer = ByteArray(1024)
        var count: Int
        for (i in solist.indices) {
            inputStream = apk.getInputStream(ZipEntry("lib/" + arch + "/" + solist.get(i)))
            outputStream = FileOutputStream(File(context.cacheDir.path + "/" + solist.get(i)))
            while (inputStream.read(buffer).also { count = it } != -1) {
                outputStream.write(buffer, 0, count)
            }
        }
    }
}