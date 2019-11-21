package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.exception.AuthenticationException;

public class StoreBackupRequest extends SingleSsoRequest<Void> {
    private final Bundle _backup;

    public StoreBackupRequest(Context applicationContext, Bundle backup) {
        super(applicationContext, null);
        this._backup = backup;
    }

    public Void performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        params.putAll(this._backup);
        SingleSsoRequest.checkForErrors(this._msaSsoService.storeBackup(params));
        return null;
    }
}
