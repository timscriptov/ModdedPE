package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.Bundles;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignUpIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final SignUpOptions _signUpOptions;

    public GetSignUpIntentRequest(Context applicationContext, Bundle state, SignUpOptions signUpOptions, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        if (signUpOptions == null) {
            signUpOptions = new SignUpOptions();
        }
        this._signUpOptions = signUpOptions;
        if (onlineIdConfiguration == null) {
            onlineIdConfiguration = new OnlineIdConfiguration();
        }
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        params.putAll(BundleMarshaller.appPropertiesToBundle(Bundles.merge(this._onlineIdConfiguration.asBundle(), this._signUpOptions.asBundle())));
        Bundle bundle = this._msaSsoService.getSignUpIntent(params);
        SingleSsoRequest.checkForErrors(bundle);
        return BundleMarshaller.pendingIntentFromBundle(bundle);
    }
}
