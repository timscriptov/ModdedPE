package com.microsoft.onlineid;

import android.app.PendingIntent;
import android.os.Bundle;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.IFailureCallback;
import com.microsoft.onlineid.internal.IUserInteractionCallback;

public interface ITicketCallback extends IFailureCallback, IUserInteractionCallback {
    void onFailure(AuthenticationException authenticationException, Bundle bundle);

    void onTicketAcquired(Ticket ticket, UserAccount userAccount, Bundle bundle);

    void onUINeeded(PendingIntent pendingIntent, Bundle bundle);

    void onUserCancel(Bundle bundle);
}
