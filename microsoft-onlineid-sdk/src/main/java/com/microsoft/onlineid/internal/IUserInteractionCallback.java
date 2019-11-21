package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.os.Bundle;

public interface IUserInteractionCallback {
    void onUINeeded(PendingIntent pendingIntent, Bundle bundle);

    void onUserCancel(Bundle bundle);
}
