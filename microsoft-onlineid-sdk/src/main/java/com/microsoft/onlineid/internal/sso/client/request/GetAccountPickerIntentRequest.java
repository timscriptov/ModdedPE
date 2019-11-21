package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import java.util.ArrayList;

public class GetAccountPickerIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final ArrayList<String> _cidExclusionList;
    private final OnlineIdConfiguration _onlineIdConfiguration;

    public GetAccountPickerIntentRequest(Context applicationContext, Bundle state, ArrayList<String> cidExclusionList, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        this._cidExclusionList = cidExclusionList;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        params.putStringArrayList(BundleMarshaller.CidExclusionListKey, this._cidExclusionList);
        if (this._onlineIdConfiguration != null) {
            params.putAll(BundleMarshaller.appPropertiesToBundle(this._onlineIdConfiguration.asBundle()));
        }
        Bundle bundle = this._msaSsoService.getAccountPickerIntent(params);
        SingleSsoRequest.checkForErrors(bundle);
        return BundleMarshaller.pendingIntentFromBundle(bundle);
    }
}
