package com.microsoft.xal.browser;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.microsoft.xal.logging.XalLogger;

/**
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public class IntentHandler extends AppCompatActivity {
    private final XalLogger m_logger = new XalLogger("IntentHandler");

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m_logger.Important("onCreate() New intent received.");
        this.m_logger.Flush();
        Intent intent = new Intent(this, BrowserLaunchActivity.class);
        intent.setData(getIntent().getData());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}