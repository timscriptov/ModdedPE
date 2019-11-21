package com.microsoft.onlineid;

import android.app.PendingIntent;
import android.os.Bundle;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.IFailureCallback;
import com.microsoft.onlineid.internal.IUserInteractionCallback;

public interface IAccountCallback extends IFailureCallback, IUserInteractionCallback {
    void onAccountAcquired(UserAccount userAccount, Bundle bundle);

    void onAccountSignedOut(String str, boolean z, Bundle bundle);

    void onFailure(AuthenticationException authenticationException, Bundle bundle);

    void onUINeeded(PendingIntent pendingIntent, Bundle bundle);

    void onUserCancel(Bundle bundle);
}
