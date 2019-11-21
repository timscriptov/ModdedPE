package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.Collection;

public class GetAccountOperation extends ServiceOperation {
    public GetAccountOperation(Context applicationContext, Bundle params, AuthenticatorAccountManager accountManager, TicketManager ticketManager) {
        super(applicationContext, params, accountManager, ticketManager);
    }

    public Bundle call() {
        if (!getAccountManager().hasAccounts()) {
            return new GetSignInIntentOperation(getContext(), getParameters(), getAccountManager(), getTicketManager()).call();
        }
        Collection<AuthenticatorUserAccount> accounts = getAccountManager().getAccounts();
        if (accounts.size() == 1) {
            return BundleMarshaller.limitedUserAccountToBundle((AuthenticatorUserAccount) accounts.iterator().next());
        }
        return new GetAccountPickerOperation(getContext(), getParameters(), getAccountManager(), getTicketManager()).call();
    }
}
