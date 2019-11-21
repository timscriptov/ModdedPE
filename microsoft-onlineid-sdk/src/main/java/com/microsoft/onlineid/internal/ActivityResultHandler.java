package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.content.Intent;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sdk.R;

public abstract class ActivityResultHandler {
    protected abstract void onFailure(Exception exception);

    protected abstract void onSuccess(ApiResult apiResult);

    protected abstract void onUINeeded(PendingIntent pendingIntent);

    protected abstract void onUserCancel();

    public void onActivityResult(int resultCode, Intent data) {
        ApiResult apiResult = new ApiResult(data != null ? data.getExtras() : null);
        switch (resultCode) {
            case BuildConfig.VERSION_CODE /*-1*/:
                onSuccess(apiResult);
                return;
            case R.styleable.StyledTextView_font /*0*/:
                onUserCancel();
                return;
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                onFailure(apiResult.getException());
                return;
            case ApiResult.ResultUINeeded /*2*/:
                onUINeeded(apiResult.getUINeededIntent());
                return;
            default:
                onUnknownResult(apiResult, resultCode);
                return;
        }
    }

    protected void onUnknownResult(ApiResult result, int resultCode) {
        Assertion.check(false, "Unknown result code: " + resultCode);
        onFailure(new InternalException("Unknown result code: " + resultCode));
    }
}
