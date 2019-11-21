package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.SendLogsHandler;
import com.microsoft.onlineid.internal.ui.AccountHeaderView;
import com.microsoft.onlineid.sdk.R;

public abstract class MsaSdkActivity extends Activity {
    public static final String AuthenticatorIntentFlagTag = "com.microsoft.msa.authenticator.authenticatorFlags";
    public static final int IntentFlagNoFinishAnimation = 1;
    protected SendLogsHandler _logHandler;

    protected void onCreate(Bundle savedInstanceState) {
        AccountHeaderView.applyStyle(this, getResources().getString(R.string.webflow_header));
        super.onCreate(savedInstanceState);
        this._logHandler = new SendLogsHandler(this);
    }

    protected void onPause() {
        if (isFinishing() && (getIntent().getIntExtra(AuthenticatorIntentFlagTag, 0) & IntentFlagNoFinishAnimation) == IntentFlagNoFinishAnimation) {
            overridePendingTransition(0, 0);
        }
        super.onPause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Settings.isDebugBuild() && this._logHandler != null) {
            this._logHandler.setSendScreenshot(true);
            this._logHandler.trySendLogsOnKeyEvent(keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }
}
