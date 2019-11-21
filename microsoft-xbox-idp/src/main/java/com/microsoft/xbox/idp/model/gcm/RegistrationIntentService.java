package com.microsoft.xbox.idp.model.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.xbox.idp.interop.Interop;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    private static final String REGISTRATION_MODE = "com.microsoft.xbox.idp.model.gcm";
    private static final String REGISTRATION_TOKEN_FIELD = "registrationToken";
    private static final String SENDER_ID = "86584527366";
    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    public void onHandleIntent(Intent intent) {
        boolean isCached = true;
        SharedPreferences sharedPreferences = getSharedPreferences(REGISTRATION_MODE, 0);
        String registrationToken = null;
        if (sharedPreferences != null) {
            registrationToken = sharedPreferences.getString(REGISTRATION_TOKEN_FIELD, "");
        }
        boolean isRefresh = false;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                isRefresh = extras.getBoolean(NotificationInstanceIDListenerService.REFRESH_FLAG, false);
            }
        }
        if (registrationToken.isEmpty() || isRefresh) {
            isCached = false;
            try {
                registrationToken = InstanceID.getInstance(this).getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            } catch (IOException e) {
                Log.d("XSAPI.Android", e.getMessage());
            } catch (SecurityException e2) {
                Log.d("XSAPI.Android", e2.getMessage());
            }
            sharedPreferences.edit().putString(REGISTRATION_TOKEN_FIELD, registrationToken).commit();
        }
        Interop.NotificationRegisterCallback(registrationToken, isCached);
    }
}
