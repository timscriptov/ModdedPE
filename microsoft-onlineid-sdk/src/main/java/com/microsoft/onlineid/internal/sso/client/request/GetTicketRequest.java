package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Bundles;
import com.microsoft.onlineid.internal.Scopes;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;

public class GetTicketRequest extends SingleSsoRequest<SsoResponse<Ticket>> {
    private final String _cid;
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final ISecurityScope _securityScope;

    public GetTicketRequest(Context applicationContext, Bundle state, String cid, ISecurityScope securityScope, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        this._cid = cid;
        this._securityScope = securityScope;
        if (onlineIdConfiguration == null) {
            onlineIdConfiguration = new OnlineIdConfiguration();
        }
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public SsoResponse<Ticket> performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        params.putString(BundleMarshaller.UserCidKey, this._cid);
        Bundle globalParameters = this._onlineIdConfiguration.asBundle();
        Bundle scopeParameters = Bundles.fromStringMap(Scopes.extractParametersFromScope(this._securityScope));
        AppProperties appProperties = new AppProperties(Bundles.merge(globalParameters, scopeParameters));
        appProperties.remove(AppProperties.PreferredMembernameTypeKey);
        params.putAll(BundleMarshaller.scopeToBundle(Scopes.applyDefaultParametersToScope(this._securityScope, appProperties.getServerValues())));
        params.putAll(BundleMarshaller.appPropertiesToBundle(appProperties));
        Bundle bundle = this._msaSsoService.getTicket(params);
        SingleSsoRequest.checkForErrors(bundle);
        if (BundleMarshaller.hasPendingIntent(bundle)) {
            return new SsoResponse().setPendingIntent(BundleMarshaller.pendingIntentFromBundle(bundle));
        }
        return new SsoResponse().setData(BundleMarshaller.ticketFromBundle(bundle));
    }
}
