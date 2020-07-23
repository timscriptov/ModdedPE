package com.mojang.minecraftpe;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.microsoft.xbox.services.NotificationHelper;
import com.microsoft.xbox.services.NotificationResult;

public class NotificationListenerService extends GcmListenerService {
    public native void nativePushNotificationReceived(int i, String str, String str2, String str3);

    public void onMessageReceived(String from, Bundle data) {
        NotificationResult result = NotificationHelper.tryParseXboxLiveNotification(data, this);
        nativePushNotificationReceived(result.notificationType.ordinal(), result.title, result.body, result.data);
    }
}