package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.PendingIntentBuilder;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;

public abstract class ServiceOperation {
    private final AuthenticatorAccountManager _accountManager;
    private final Context _applicationContext;
    private final Bundle _parameters;
    private final TicketManager _ticketManager;

    public abstract Bundle call() throws AccountNotFoundException, InvalidResponseException, NetworkException, StsException, InternalException;

    public ServiceOperation(Context applicationContext, Bundle parameters, AuthenticatorAccountManager accountManager, TicketManager ticketManager) {
        this._applicationContext = applicationContext;
        this._parameters = parameters;
        this._accountManager = accountManager;
        this._ticketManager = ticketManager;
    }

    public void verifyStandardArguments() {
        Objects.verifyArgumentNotNull(getParameters(), "Parameters");
        Strings.verifyArgumentNotNullOrEmpty(getCallingPackage(), "Package name");
        Strings.verifyArgumentNotNullOrEmpty(getCallerSdkVersion(), "SDK version");
        Strings.verifyArgumentNotNullOrEmpty(getCallerConfigVersion(), "Config version");
        if (getCallerSsoVersion() == 0) {
            throw new IllegalArgumentException("SSO version must not be empty.");
        }
    }

    public Bundle getParameters() {
        return this._parameters;
    }

    public String getCallingPackage() {
        return this._parameters.getString(BundleMarshaller.ClientPackageNameKey);
    }

    public String getCallerSdkVersion() {
        return this._parameters.getString(BundleMarshaller.ClientSdkVersionKey);
    }

    public int getCallerSsoVersion() {
        return this._parameters.getInt(BundleMarshaller.ClientSsoVersionKey);
    }

    public Bundle getCallerStateBundle() {
        return this._parameters.getBundle(BundleMarshaller.ClientStateBundleKey);
    }

    public String getCallerConfigVersion() {
        return this._parameters.getString(BundleMarshaller.ClientConfigVersionKey);
    }

    public long getCallerConfigLastDownloadedTime() {
        return this._parameters.getLong(BundleMarshaller.ClientConfigLastDownloadedTimeKey);
    }

    protected Context getContext() {
        return this._applicationContext;
    }

    protected AuthenticatorAccountManager getAccountManager() {
        return this._accountManager;
    }

    protected TicketManager getTicketManager() {
        return this._ticketManager;
    }

    protected PendingIntentBuilder getPendingIntentBuilder(Intent intent) {
        return new PendingIntentBuilder(intent);
    }
}
