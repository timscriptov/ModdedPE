package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.ui.AddAccountActivity;

public class GetSignInIntentOperation extends ServiceOperation {
    public GetSignInIntentOperation(Context applicationContext, Bundle params, AuthenticatorAccountManager accountManager, TicketManager ticketManager) {
        super(applicationContext, params, accountManager, ticketManager);
    }

    public Bundle call() {
        AppProperties appProperties = BundleMarshaller.appPropertiesFromBundle(getParameters());
        appProperties.setLegacyParameters(getParameters());
        return BundleMarshaller.pendingIntentToBundle(getPendingIntentBuilder(AddAccountActivity.getSignInIntent(getContext(), appProperties.toBundle(), getCallingPackage(), getCallerStateBundle())).setContext(getContext()).buildActivity());
    }
}
