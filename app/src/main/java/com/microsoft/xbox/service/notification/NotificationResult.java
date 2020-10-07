package com.microsoft.xbox.service.notification;

import android.content.Context;
import android.util.Log;

import com.appboy.models.InAppMessageBase;
import com.google.firebase.messaging.RemoteMessage;
import com.mcal.mcpelauncher.R;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NotificationResult {
    public String body;
    public String data;
    public NotificationType notificationType;
    public String title;

    public NotificationResult(@NotNull RemoteMessage remoteMessage, Context ctx) {
        Map<String, String> messagedata = remoteMessage.getData();
        String type = messagedata.get(InAppMessageBase.TYPE);
        if (type == null) {
            notificationType = NotificationType.Unknown;
        } else if (type.equals("xbox_live_game_invite")) {
            notificationType = NotificationType.Invite;
            title = ctx.getString(R.string.xbox_live_game_invite_title);
            String invitePartialBody = ctx.getString(R.string.xbox_live_game_invite_body);
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            if (notification != null) {
                Log.i("XSAPI.Android", "parsing notification");
                String[] bodyLocArgs = notification.getBodyLocalizationArgs();
                if (bodyLocArgs != null) {
                    body = String.format(invitePartialBody, new Object[]{bodyLocArgs[0], bodyLocArgs[1]});
                }
            } else {
                Log.i("XSAPI.Android", "could not parse notification");
            }
        } else if (type.equals("xbox_live_achievement_unlock")) {
            notificationType = NotificationType.Achievement;
            if (remoteMessage.getNotification() != null) {
                title = remoteMessage.getNotification().getTitle();
                body = remoteMessage.getNotification().getBody();
            }
        } else {
            notificationType = NotificationType.Unknown;
        }
        data = messagedata.get("xbl");
    }

    public enum NotificationType {
        Achievement,
        Invite,
        Unknown
    }
}
