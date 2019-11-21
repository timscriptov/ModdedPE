package com.microsoft.onlineid.authenticator;

import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.ui.ProgressView;
import com.microsoft.onlineid.sdk.R;
import com.microsoft.onlineid.ui.MsaSdkActivity;

public class AccountAddPendingActivity extends MsaSdkActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.static_page);
        super.onCreate(savedInstanceState);
        ProgressView progress = (ProgressView) findViewById(R.id.progressView);
        progress.setVisibility(0);
        progress.startAnimation();
        findViewById(R.id.static_page_header).setVisibility(8);
        findViewById(R.id.static_page_body_first).setVisibility(8);
        findViewById(R.id.static_page_body_second).setVisibility(8);
    }

    protected void onStart() {
        super.onStart();
        ClientAnalytics.get().logScreenView(ClientAnalytics.AccountAddPendingScreen);
    }

    public void onBackPressed() {
    }
}
