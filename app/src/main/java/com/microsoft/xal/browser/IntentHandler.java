package com.microsoft.xal.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class IntentHandler extends AppCompatActivity {
    public static final String TAG = "IntentHandler";

    @SuppressLint("WrongConstant")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate() New intent received.");
        Intent intent = new Intent(this, WebView.class);
        intent.setData(getIntent().getData());
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(603979776);
        startActivity(intent);
        finish();
    }
}
