package com.microsoft.xal.browser;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.xal.logging.XalLogger;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class IntentHandler extends AppCompatActivity {
    private final XalLogger m_logger = new XalLogger("IntentHandler");

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_logger.Important("onCreate() New intent received.");
        m_logger.Flush();
        Intent intent = new Intent(this, WebView.class);
        intent.setData(getIntent().getData());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
