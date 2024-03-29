package com.mcal.moddedpe.googleplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller


class ApkXDownloaderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ApkXDownloaderAlarmReceiver", "Alarm received")
        try {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(
                context,
                intent,
                ApkXDownloaderService::class.java
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("ApkXDownloaderAlarmReceiver", "Exception: " + e.javaClass.name + ":" + e.message)
        }
    }
}
