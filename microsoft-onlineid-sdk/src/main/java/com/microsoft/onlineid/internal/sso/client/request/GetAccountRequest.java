package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class GetAccountRequest extends SingleSsoRequest<SsoResponse<AuthenticatorUserAccount>> {
    private final OnlineIdConfiguration _onlineIdConfiguration;

    public GetAccountRequest(Context applicationContext, Bundle state, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public SsoResponse<AuthenticatorUserAccount> performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        if (this._onlineIdConfiguration != null) {
            params.putAll(BundleMarshaller.appPropertiesToBundle(this._onlineIdConfiguration.asBundle()));
        }
        Bundle bundle = this._msaSsoService.getAccount(params);
        SingleSsoRequest.checkForErrors(bundle);
        if (BundleMarshaller.hasPendingIntent(bundle)) {
            return new SsoResponse().setPendingIntent(BundleMarshaller.pendingIntentFromBundle(bundle));
        }
        return new SsoResponse().setData(BundleMarshaller.limitedUserAccountFromBundle(bundle));
    }
}
