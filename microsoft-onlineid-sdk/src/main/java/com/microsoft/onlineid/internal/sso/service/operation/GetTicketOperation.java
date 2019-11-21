package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.BlockingApiRequestResultReceiver;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.exception.UserCancelledException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;

public class GetTicketOperation extends ServiceOperation {
    public GetTicketOperation(Context applicationContext, Bundle params, AuthenticatorAccountManager accountManager, TicketManager ticketManager) {
        super(applicationContext, params, accountManager, ticketManager);
    }

    public Bundle call() throws AccountNotFoundException, InvalidResponseException, NetworkException, StsException, InternalException {
        String cid = getParameters().getString(BundleMarshaller.UserCidKey);
        Strings.verifyArgumentNotNullOrEmpty(cid, BundleMarshaller.UserCidKey);
        AuthenticatorUserAccount account = getAccountManager().getAccountByCid(cid);
        if (account == null) {
            throw new AccountNotFoundException();
        }
        ISecurityScope scope = BundleMarshaller.scopeFromBundle(getParameters());
        AppProperties appProperties = BundleMarshaller.appPropertiesFromBundle(getParameters());
        appProperties.setLegacyParameters(getParameters());
        String cobrandingId = appProperties.get(AppProperties.CobrandIdKey);
        boolean webTelemetryRequested = appProperties.is(AppProperties.ClientWebTelemetryRequestedKey);
        BlockingApiRequestResultReceiver<Ticket> receiver = new BlockingApiRequestResultReceiver<Ticket>() {
            protected void onSuccess(ApiResult result) {
                setResult(result.getTicket());
            }
        };
        getContext().startService(new TicketManager(getContext()).createTicketRequest(account.getPuid(), scope, getCallingPackage(), cobrandingId, getCallerStateBundle()).setIsWebFlowTelemetryRequested(webTelemetryRequested).setIsSdkRequest(true).setResultReceiver(receiver).asIntent());
        try {
            SsoResponse<Ticket> response = receiver.blockForResult();
            if (response.hasData()) {
                return BundleMarshaller.ticketToBundle((Ticket) response.getData());
            }
            if (response.hasPendingIntent()) {
                return BundleMarshaller.pendingIntentToBundle(response.getPendingIntent());
            }
            String message = "GetTicketOperation did not receive an expected result from MsaService.";
            Assertion.check(false, message);
            throw new InternalException(message);
        } catch (UserCancelledException e) {
            Assertion.check(false, "Unexpected UserCancelledException caught in GetTicketOperation.");
            return BundleMarshaller.exceptionToBundle(e);
        } catch (Exception e2) {
            return BundleMarshaller.exceptionToBundle(e2);
        }
    }
}
