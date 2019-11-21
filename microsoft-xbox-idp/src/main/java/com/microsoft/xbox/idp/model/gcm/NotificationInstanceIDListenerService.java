package com.microsoft.xbox.idp.model.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class NotificationInstanceIDListenerService extends InstanceIDListenerService {
    public static String REFRESH_FLAG = "isRefresh";
    private static final String TAG = "MyInstanceIDLS";

    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(REFRESH_FLAG, true);
        startService(intent);
    }
}
