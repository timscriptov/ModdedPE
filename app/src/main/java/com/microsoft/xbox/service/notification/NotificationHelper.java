package com.microsoft.xbox.service.notification;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NotificationHelper {
    @Contract("_, _ -> new")
    public static @NotNull NotificationResult tryParseXboxLiveNotification(RemoteMessage remoteMessage, Context context) {
        return new NotificationResult(remoteMessage, context);
    }
}
