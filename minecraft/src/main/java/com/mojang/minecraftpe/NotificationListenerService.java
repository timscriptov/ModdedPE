package com.mojang.minecraftpe;

import android.os.Looper;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.microsoft.xbox.service.notification.NotificationHelper;
import com.microsoft.xbox.service.notification.NotificationResult;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NotificationListenerService extends FirebaseMessagingService {
    private static String sDeviceRegistrationToken = "";

    native void nativePushNotificationReceived(final int type, String title, String description, String data);

    public NotificationListenerService() {
        retrieveDeviceToken();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationResult tryParseXboxLiveNotification = NotificationHelper.tryParseXboxLiveNotification(remoteMessage, this);
        nativePushNotificationReceived(tryParseXboxLiveNotification.notificationType.ordinal(), tryParseXboxLiveNotification.title, tryParseXboxLiveNotification.body, tryParseXboxLiveNotification.data);
    }

    public static String getDeviceRegistrationToken() {
        retrieveDeviceToken();
        return sDeviceRegistrationToken;
    }

    private static void retrieveDeviceToken() {
        if (Thread.currentThread().equals(Looper.getMainLooper().getThread())) {
            Log.e("Minecraft", "NotificationListenerService.retrieveDeviceToken() should not run on main thread.");
        }
        if (sDeviceRegistrationToken.isEmpty()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                String result = task.isSuccessful() ? task.getResult() : "";
                if (result != null && !result.isEmpty()) {
                    sDeviceRegistrationToken = result;
                } else {
                    Log.e("Minecraft", "Unable to get Firebase Messaging token, trying again...");
                }
            });
        }
    }
}