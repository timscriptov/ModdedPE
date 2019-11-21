package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sdk.R;

public abstract class ApiRequestResultReceiver extends ResultReceiver {
    protected abstract void onFailure(Exception exception);

    protected abstract void onSuccess(ApiResult apiResult);

    protected abstract void onUINeeded(PendingIntent pendingIntent);

    protected abstract void onUserCancel();

    public ApiRequestResultReceiver(Handler handler) {
        super(handler);
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        ApiResult request = new ApiResult(resultData);
        switch (resultCode) {
            case BuildConfig.VERSION_CODE /*-1*/:
                onSuccess(request);
                return;
            case R.styleable.StyledTextView_font /*0*/:
                onUserCancel();
                return;
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                onFailure(request.getException());
                return;
            case ApiResult.ResultUINeeded /*2*/:
                onUINeeded(request.getUINeededIntent());
                return;
            default:
                onUnknownResult(request, resultCode);
                return;
        }
    }

    protected void onUnknownResult(ApiResult result, int resultCode) {
        Assertion.check(false, "Unknown result code: " + resultCode);
        onFailure(new InternalException("Unknown result code: " + resultCode));
    }
}
