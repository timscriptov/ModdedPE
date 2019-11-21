package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignOutIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final String _cid;

    public GetSignOutIntentRequest(Context applicationContext, Bundle state, String cid) {
        super(applicationContext, state);
        this._cid = cid;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        params.putString(BundleMarshaller.UserCidKey, this._cid);
        Bundle bundle = this._msaSsoService.getSignOutIntent(params);
        SingleSsoRequest.checkForErrors(bundle);
        return BundleMarshaller.pendingIntentFromBundle(bundle);
    }
}
