package com.mcal.moddedpe.googleplay

import com.google.android.vending.expansion.downloader.impl.DownloaderService


class ApkXDownloaderService : DownloaderService() {
    override fun getPublicKey(): String {
        return ApkXDownloaderClient.getLicenseKey()
    }

    override fun getSALT(): ByteArray {
        return ApkXDownloaderClient.SALT
    }

    fun getNotificationChannelId(): String {
        return ApkXDownloaderClient.getNotificationChannelId()
    }

    override fun getAlarmReceiverClassName(): String {
        return ApkXDownloaderAlarmReceiver::class.java.name
    }
}
