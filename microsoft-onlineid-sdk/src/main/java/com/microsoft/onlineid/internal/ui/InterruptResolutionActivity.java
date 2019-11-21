package com.microsoft.onlineid.internal.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.ActivityResultHandler;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequestResultReceiver;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.BundleMarshallerException;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class InterruptResolutionActivity extends Activity {
    private static final int PendingActivityRequestCode = 1;
    private static final int WebFlowRequestCode = 2;
    private String _accountCid;
    private String _accountPuid;
    private String _clientPackageName;
    private Bundle _clientState;
    private String _cobrandingId;
    private ISecurityScope _requestedScope;
    private ActivityResultSender _resultSender;
    private TypedStorage _storage;
    private TicketResultReceiver _ticketReceiver;

    private static abstract class DelegatedResultReceiver extends ApiRequestResultReceiver {
        protected InterruptResolutionActivity _activity;

        public DelegatedResultReceiver() {
            super(new Handler());
        }

        public void setActivity(InterruptResolutionActivity activity) {
            this._activity = activity;
        }

        protected void onUserCancel() {
            if (this._activity != null) {
                this._activity.onUserCancel();
            }
        }

        protected void onFailure(Exception e) {
            if (this._activity != null) {
                this._activity.onFailure(e);
            }
        }

        protected void onUINeeded(PendingIntent intent) {
            if (this._activity != null) {
                this._activity.onUiNeeded(intent);
            }
        }
    }

    private static class TicketResultReceiver extends DelegatedResultReceiver {
        private TicketResultReceiver() {
        }

        protected void onSuccess(ApiResult result) {
            if (this._activity != null) {
                this._activity.onTicketAcquired(result.getTicket());
            }
        }
    }

    private class WebFlowResultHandler extends ActivityResultHandler {
        private WebFlowResultHandler() {
        }

        protected void onSuccess(ApiResult result) {
            InterruptResolutionActivity.this.onWebFlowSucceeded(result.getFlowToken());
        }

        protected void onUserCancel() {
            InterruptResolutionActivity.this.onUserCancel();
        }

        protected void onFailure(Exception e) {
            InterruptResolutionActivity.this.onFailure(e);
        }

        protected void onUINeeded(PendingIntent intent) {
            InterruptResolutionActivity.this.onUiNeeded(intent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._storage = new TypedStorage(getApplicationContext());
        this._resultSender = new ActivityResultSender(this, ResultType.Ticket);
        this._ticketReceiver = new TicketResultReceiver();
        this._ticketReceiver.setActivity(this);
        if (BundleMarshaller.hasScope(getIntent().getExtras())) {
            try {
                this._requestedScope = BundleMarshaller.scopeFromBundle(getIntent().getExtras());
            } catch (BundleMarshallerException e) {
                onFailure(e);
            }
        }
        this._accountPuid = getIntent().getExtras().getString(BundleMarshaller.UserPuidKey);
        this._accountCid = getIntent().getExtras().getString(BundleMarshaller.UserCidKey);
        this._clientPackageName = getIntent().getExtras().getString(BundleMarshaller.ClientPackageNameKey);
        this._cobrandingId = getIntent().getStringExtra(BundleMarshaller.CobrandingIdKey);
        this._clientState = getIntent().getBundleExtra(BundleMarshaller.ClientStateBundleKey);
        WebFlowTelemetryData telemetryData = new WebFlowTelemetryData().setIsWebTelemetryRequested(getIntent().getBooleanExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, false)).setCallingAppPackageName(this._clientPackageName).setCallingAppVersionName(PackageInfoHelper.getAppVersionName(getApplicationContext(), this._clientPackageName)).setWasPrecachingEnabled(false);
        if (savedInstanceState == null) {
            startActivityForResult(WebFlowActivity.getFlowRequest(getApplicationContext(), getIntent().getData(), WebFlowActivity.ActionResolveInterrupt, null, telemetryData).setAccountPuid(this._accountPuid).asIntent().addFlags(65536), WebFlowRequestCode);
        }
    }

    protected void onDestroy() {
        this._ticketReceiver.setActivity(null);
        super.onDestroy();
    }

    protected void onWebFlowSucceeded(String flowToken) {
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.isSdkRequest()) {
            if (this._requestedScope == null) {
                this._ticketReceiver.onFailure(new IllegalArgumentException("Scope must not be null for SSO ticket request."));
            }
            startService(new TicketManager(getApplicationContext()).createTicketRequest(this._accountPuid, this._requestedScope, this._clientPackageName, this._cobrandingId, this._clientState).setFlowToken(flowToken).setResultReceiver(this._ticketReceiver).asIntent());
            showPendingActivity();
            return;
        }
        request.sendSuccess(new ApiResult().setAccountPuid(this._accountPuid).setFlowToken(flowToken));
        finish();
    }

    protected void showPendingActivity() {
        startActivityForResult(new Intent().setClassName(getApplicationContext(), "com.microsoft.onlineid.authenticator.AccountAddPendingActivity").addFlags(65536), PendingActivityRequestCode);
    }

    protected void onFailure(Exception e) {
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (!request.isSdkRequest()) {
            request.sendFailure(e);
        } else if (e instanceof AccountNotFoundException) {
            this._resultSender.putSignedOutCid(this._accountCid, false).set();
        } else {
            this._resultSender.putException(e).set();
        }
        finishActivity(PendingActivityRequestCode);
        finish();
    }

    protected void onUserCancel() {
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendUserCanceled();
        }
        finishActivity(PendingActivityRequestCode);
        finish();
    }

    protected void onUiNeeded(PendingIntent intent) {
        try {
            startIntentSenderForResult(intent.getIntentSender(), 0, null, 0, 0, 0);
            this._resultSender.putWereAllWebFlowTelemetryEventsCaptured(false).set();
        } catch (SendIntentException e) {
            onFailure(e);
        }
    }

    protected void onTicketAcquired(Ticket ticket) {
        AuthenticatorUserAccount account = this._storage.readAccount(this._accountPuid);
        if (account == null) {
            onFailure(new AccountNotFoundException());
            return;
        }
        this._resultSender.putTicket(ticket).putLimitedUserAccount(account).set();
        finishActivity(PendingActivityRequestCode);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PendingActivityRequestCode /*1*/:
                if (resultCode == 0) {
                    onUserCancel();
                    return;
                }
                return;
            case WebFlowRequestCode /*2*/:
                addTelemetryToResult(data);
                new WebFlowResultHandler().onActivityResult(resultCode, data);
                return;
            default:
                return;
        }
    }

    protected void addTelemetryToResult(Intent data) {
        if (data != null) {
            ApiResult result = new ApiResult(data.getExtras());
            if (result.hasWebFlowTelemetryEvents()) {
                this._resultSender.putWebFlowTelemetryFields(result).set();
            }
        }
    }

    protected String getAccountPuid() {
        return this._accountPuid;
    }

    protected String getAccountCid() {
        return this._accountCid;
    }

    protected String getClientPackageName() {
        return this._clientPackageName;
    }

    protected ISecurityScope getRequestedScope() {
        return this._requestedScope;
    }

    public static Intent getResolutionIntent(Context applicationContext, Uri authUri, AuthenticatorUserAccount account, ISecurityScope scope, String cobrandingId, boolean webTelemetryRequested, String clientPackageName, Bundle clientState) {
        Intent intent = new Intent().setClass(applicationContext, InterruptResolutionActivity.class).setData(authUri).putExtra(BundleMarshaller.UserPuidKey, account.getPuid()).putExtra(BundleMarshaller.UserCidKey, account.getCid()).putExtra(BundleMarshaller.CobrandingIdKey, cobrandingId).putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, webTelemetryRequested).putExtra(BundleMarshaller.ClientPackageNameKey, clientPackageName).putExtra(BundleMarshaller.ClientStateBundleKey, clientState);
        if (scope != null && clientPackageName != null) {
            intent.putExtras(BundleMarshaller.scopeToBundle(scope));
        } else if (scope != null && clientPackageName == null) {
            throw new IllegalArgumentException("A ticket scope requires a client package name to make a request.");
        }
        return intent;
    }
}
