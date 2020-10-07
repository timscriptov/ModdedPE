package com.microsoft.xbox.service.notification;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NotificationHelper {
    @NotNull
    @Contract("_, _ -> new")
    public static NotificationResult tryParseXboxLiveNotification(RemoteMessage remoteMessage, Context ctx) {
        return new NotificationResult(remoteMessage, ctx);
    }
}
