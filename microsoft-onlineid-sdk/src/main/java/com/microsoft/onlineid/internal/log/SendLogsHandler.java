package com.microsoft.onlineid.internal.log;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.microsoft.onlineid.internal.Assertion;

public class SendLogsHandler {
    protected static final long SendKeyEventIntervalMillis = 5000;
    protected static final String ToastMsg = "Press the 'volume down' button %d more time(s) to send logs.";
    private Context _activityContext;
    private Context _applicationContext;
    private ErrorReportManager _errorReport;
    private byte _sendLogsKeyCounter;
    private long _startTime;
    private Toast _toast;

    public SendLogsHandler(Activity activityContext) {
        this(activityContext, null);
    }

    protected SendLogsHandler(Context context, ErrorReportManager errorReporter) {
        this._applicationContext = null;
        this._activityContext = null;
        this._startTime = 0;
        this._sendLogsKeyCounter = (byte) -1;
        this._activityContext = context;
        this._errorReport = errorReporter;
    }

    protected SendLogsHandler(Activity activityContext, ErrorReportManager errorReporter) {
        boolean z;
        boolean z2 = true;
        this._applicationContext = null;
        this._activityContext = null;
        this._startTime = 0;
        this._sendLogsKeyCounter = (byte) -1;
        this._activityContext = activityContext;
        if (activityContext != null) {
            z = true;
        } else {
            z = false;
        }
        Assertion.check(z);
        this._applicationContext = activityContext.getApplicationContext();
        if (this._applicationContext == null) {
            z2 = false;
        }
        Assertion.check(z2);
        if (errorReporter == null) {
            errorReporter = new ErrorReportManager(this._applicationContext);
        }
        this._errorReport = errorReporter;
    }

    public void setSendScreenshot(boolean sendScreenshotNewValue) {
        this._errorReport.setSendScreenshot(sendScreenshotNewValue);
    }

    public void setSendLogs(boolean sendLogsNewValue) {
        this._errorReport.setSendLogs(sendLogsNewValue);
    }

    public void trySendLogsOnKeyEvent(int keyCode) {
        switch (keyCode) {
            case 24:
                this._sendLogsKeyCounter = (byte) 2;
                showToast(String.format(ToastMsg, new Object[]{Byte.valueOf(this._sendLogsKeyCounter)}));
                this._startTime = getTimeMillis();
                return;
            case 25:
                long elapsed = getTimeMillis() - this._startTime;
                if (this._sendLogsKeyCounter < (byte) 0 || elapsed >= SendKeyEventIntervalMillis) {
                    this._sendLogsKeyCounter = (byte) -1;
                    return;
                }
                this._sendLogsKeyCounter = (byte) (this._sendLogsKeyCounter - 1);
                if (this._sendLogsKeyCounter > (byte) 0) {
                    showToast(String.format(ToastMsg, new Object[]{Byte.valueOf(this._sendLogsKeyCounter)}));
                    return;
                }
                sendLogs();
                this._sendLogsKeyCounter = (byte) -1;
                return;
            default:
                return;
        }
    }

    public void sendLogs() {
        this._errorReport.generateAndSendReportWithUserPermission(this._activityContext);
    }

    public void sendLogs(String userFeedback) {
        this._errorReport.generateAndSendReportWithUserPermission(this._activityContext, userFeedback);
    }

    protected void showToast(String msg) {
        if (this._toast == null) {
            this._toast = Toast.makeText(this._applicationContext, msg, 1);
        } else {
            this._toast.setText(msg);
        }
        this._toast.show();
    }

    protected long getTimeMillis() {
        return System.currentTimeMillis();
    }
}
