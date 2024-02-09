package com.mcal.moddedpe.googleplay;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

public class ApkXDownloaderAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ApkXDownloaderAlarmReceiver", "Alarm received");
        try {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, ApkXDownloaderService.class);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("ApkXDownloaderAlarmReceiver", "Exception: " + e.getClass().getName() + ":" + e.getMessage());
        }
    }
}
