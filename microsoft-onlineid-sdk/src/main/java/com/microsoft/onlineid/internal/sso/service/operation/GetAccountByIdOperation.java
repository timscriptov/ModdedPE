package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class GetAccountByIdOperation extends ServiceOperation {
    public GetAccountByIdOperation(Context applicationContext, Bundle params, AuthenticatorAccountManager accountManager, TicketManager ticketManager) {
        super(applicationContext, params, accountManager, ticketManager);
    }

    public Bundle call() throws AccountNotFoundException {
        String cid = getParameters().getString(BundleMarshaller.UserCidKey);
        Strings.verifyArgumentNotNullOrEmpty(cid, BundleMarshaller.UserCidKey);
        AuthenticatorUserAccount account = getAccountManager().getAccountByCid(cid);
        if (account != null) {
            return BundleMarshaller.limitedUserAccountToBundle(account);
        }
        throw new AccountNotFoundException("No account was found with the specified ID.");
    }
}
