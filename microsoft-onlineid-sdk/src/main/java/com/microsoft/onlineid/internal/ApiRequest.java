package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import java.util.Locale;

public class ApiRequest {
    protected final Context _applicationContext;
    protected final Intent _intent;

    public enum Extras {
        AccountName,
        AccountPuid,
        ClientPackageName,
        ClientStateBundle,
        CobrandingId,
        Continuation,
        FlowToken,
        IsSdkRequest,
        ResultReceiver,
        Scope,
        WebFlowTelemetryRequested;

        public String getKey() {
            return "com.microsoft.msa.authenticator." + name();
        }
    }

    public ApiRequest(Context applicationContext, Intent intent) {
        this._applicationContext = applicationContext;
        this._intent = intent;
    }

    public Intent asIntent() {
        return this._intent;
    }

    public Context getContext() {
        return this._applicationContext;
    }

    public String getAccountName() {
        return this._intent.getStringExtra(Extras.AccountName.getKey());
    }

    public ApiRequest setAccountName(String value) {
        this._intent.putExtra(Extras.AccountName.getKey(), value);
        return this;
    }

    public String getAccountPuid() {
        return this._intent.getStringExtra(Extras.AccountPuid.getKey());
    }

    public ApiRequest setAccountPuid(String value) {
        this._intent.putExtra(Extras.AccountPuid.getKey(), value);
        return this;
    }

    public String getClientPackageName() {
        return this._intent.getStringExtra(Extras.ClientPackageName.getKey());
    }

    public ApiRequest setClientPackageName(String value) {
        this._intent.putExtra(Extras.ClientPackageName.getKey(), value);
        return this;
    }

    public String getFlowToken() {
        return this._intent.getStringExtra(Extras.FlowToken.getKey());
    }

    public ApiRequest setFlowToken(String value) {
        this._intent.putExtra(Extras.FlowToken.getKey(), value);
        return this;
    }

    public ISecurityScope getScope() {
        return (ISecurityScope) this._intent.getSerializableExtra(Extras.Scope.getKey());
    }

    public ApiRequest setScope(ISecurityScope value) {
        this._intent.putExtra(Extras.Scope.getKey(), value);
        return this;
    }

    protected String getTicketKey(ISecurityScope scope) {
        return TextUtils.join(".", new Object[]{PackageInfoHelper.AuthenticatorPackageName, "Ticket", scope.getTarget().toLowerCase(Locale.US), scope.getPolicy().toLowerCase(Locale.US)});
    }

    public Ticket getTicket(ISecurityScope scope) {
        return scope == null ? null : (Ticket) this._intent.getSerializableExtra(getTicketKey(scope));
    }

    public ApiRequest addTicket(Ticket value) {
        this._intent.putExtra(getTicketKey(value.getScope()), value);
        return this;
    }

    public Intent getContinuation() {
        return (Intent) this._intent.getParcelableExtra(Extras.Continuation.getKey());
    }

    public ApiRequest setContinuation(ApiRequest request) {
        this._intent.putExtra(Extras.Continuation.getKey(), request.asIntent());
        return this;
    }

    public ResultReceiver getResultReceiver() {
        return (ResultReceiver) this._intent.getParcelableExtra(Extras.ResultReceiver.getKey());
    }

    public boolean hasResultReceiver() {
        return getResultReceiver() != null;
    }

    public ApiRequest setResultReceiver(ResultReceiver value) {
        this._intent.putExtra(Extras.ResultReceiver.getKey(), value);
        return this;
    }

    public String getCobrandingId() {
        return this._intent.getStringExtra(Extras.CobrandingId.getKey());
    }

    public ApiRequest setCobrandingId(String value) {
        this._intent.putExtra(Extras.CobrandingId.getKey(), value);
        return this;
    }

    public Bundle getClientStateBundle() {
        return this._intent.getBundleExtra(Extras.ClientStateBundle.getKey());
    }

    public ApiRequest setClientStateBundle(Bundle state) {
        this._intent.putExtra(Extras.ClientStateBundle.getKey(), state);
        return this;
    }

    public boolean isSdkRequest() {
        return this._intent.getBooleanExtra(Extras.IsSdkRequest.getKey(), false);
    }

    public ApiRequest setIsSdkRequest(boolean isSdkRequest) {
        this._intent.putExtra(Extras.IsSdkRequest.getKey(), isSdkRequest);
        return this;
    }

    public ApiRequest setIsWebFlowTelemetryRequested(boolean requested) {
        this._intent.putExtra(Extras.WebFlowTelemetryRequested.getKey(), requested);
        return this;
    }

    public boolean getIsWebFlowTelemetryRequested() {
        return this._intent.getBooleanExtra(Extras.WebFlowTelemetryRequested.getKey(), false);
    }

    public void sendSuccess(ApiResult result) {
        Intent continuation = getContinuation();
        if (continuation != null) {
            continuation.fillIn(new Intent().putExtras(result.asBundle()), 0);
            getContext().startService(continuation);
            return;
        }
        sendResult(-1, result);
    }

    private void sendResult(int resultCode, ApiResult result) {
        ResultReceiver receiver = getResultReceiver();
        if (receiver != null) {
            receiver.send(resultCode, result.asBundle());
        }
    }

    public void sendUserCanceled() {
        sendResult(0, new ApiResult());
    }

    public void sendFailure(Exception exception) {
        sendResult(1, new ApiResult().setException(exception));
    }

    public void sendUINeeded(PendingIntent intent) {
        sendResult(2, new ApiResult().setUINeededIntent(intent));
    }

    public void executeAsync() {
        getContext().startService(this._intent);
    }
}
