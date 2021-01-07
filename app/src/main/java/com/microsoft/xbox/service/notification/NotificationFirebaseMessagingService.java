package com.microsoft.xbox.service.notification;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.microsoft.xbox.idp.interop.Interop;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {
    public void onNewToken(String str) {
        Interop.NotificationRegisterCallback(str);
    }
}
