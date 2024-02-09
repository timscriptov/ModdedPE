package com.mcal.moddedpe.googleplay;


import com.google.android.vending.expansion.downloader.impl.DownloaderService;


public class ApkXDownloaderService extends DownloaderService {
    @Override
    public String getPublicKey() {
        return ApkXDownloaderClient.getLicenseKey();
    }

    @Override
    public byte[] getSALT() {
        return ApkXDownloaderClient.SALT;
    }

    public String getNotificationChannelId() {
        return ApkXDownloaderClient.getNotificationChannelId();
    }

    @Override
    public String getAlarmReceiverClassName() {
        return ApkXDownloaderAlarmReceiver.class.getName();
    }
}
