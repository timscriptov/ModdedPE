package com.microsoft.xal.androidjava;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.microsoft.applications.events.HttpClient;

public class XalInitTelemetry extends AppCompatActivity {
    static void initOneDS() {
        System.loadLibrary("maesdk");
    }

    static void startHttpClient(Context context) {
        new HttpClient(context);
    }
}
