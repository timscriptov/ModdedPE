package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Applications;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Intents.DataBuilder;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.Uris;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.BackupService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.ui.WebFlowActivity;
import com.microsoft.onlineid.internal.ui.WebFlowTelemetryData;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import java.util.Map.Entry;

public class AddAccountActivity extends Activity {
    protected static final int AccountAddedRequest = 2;
    public static final String ActionAddAccount = "com.microsoft.onlineid.internal.ADD_ACCOUNT";
    public static final String ActionSignUpAccount = "com.microsoft.onlineid.internal.SIGN_UP_ACCOUNT";
    protected static final int AddPendingRequest = 1;
    protected static final int NoRequest = -1;
    public static final String PlatformLabel = "platform";
    public static final String PlatformName = "android";
    protected static final int SignInWebFlowRequest = 0;
    private static final String WReplyLabel = "wreply";
    protected String _accountPuid;
    protected Handler _handler;
    protected int _pendingChildRequest = NoRequest;
    private ActivityResultSender _resultSender;
    protected TypedStorage _typedStorage;

    protected void onCreate(Bundle savedInstanceState) {
        Uri startUri;
        super.onCreate(savedInstanceState);
        ServerConfig serverConfig = new ServerConfig(getApplicationContext());
        String clientPackageName = getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey);
        boolean isCallerMsa = PackageInfoHelper.isAuthenticatorApp(clientPackageName);
        this._resultSender = new ActivityResultSender(this, ResultType.Account);
        String action = getIntent().getAction();
        AppProperties appProperties = BundleMarshaller.appPropertiesFromBundle(getIntent().getExtras());
        boolean precachingEnabled = appProperties.is(AppProperties.ClientWebTelemetryPrecachingEnabledKey);
        boolean webTelemetryRequested = appProperties.is(AppProperties.ClientWebTelemetryRequestedKey);
        if (ActionSignUpAccount.equals(action)) {
            startUri = getSignupUri(serverConfig, isCallerMsa);
        } else {
            startUri = getLoginUri(serverConfig, isCallerMsa, false);
        }
        Intent intent = WebFlowActivity.getFlowRequest(getApplicationContext(), startUri, ActionSignUpAccount.equals(action) ? WebFlowActivity.ActionSignUp : WebFlowActivity.ActionSignIn, appProperties, new WebFlowTelemetryData().setIsWebTelemetryRequested(webTelemetryRequested).setCallingAppPackageName(clientPackageName).setCallingAppVersionName(PackageInfoHelper.getAppVersionName(getApplicationContext(), clientPackageName)).setWasPrecachingEnabled(precachingEnabled)).asIntent();
        intent.addFlags(65536);
        this._pendingChildRequest = SignInWebFlowRequest;
        if (NetworkConnectivity.hasInternetConnectivity(getApplicationContext())) {
            startActivityForResult(intent, SignInWebFlowRequest);
            this._handler = new Handler();
            return;
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.NoNetworkConnectivity, ClientAnalytics.AtStartOfWebFlow);
        sendFailureResult(new NetworkException());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this._pendingChildRequest) {
            this._pendingChildRequest = NoRequest;
        }
        switch (requestCode) {
            case SignInWebFlowRequest /*0*/:
                addTelemetryToResult(data);
                switch (resultCode) {
                    case NoRequest /*-1*/:
                        if (data == null || data.getExtras() == null) {
                            sendFailureResult("Sign in flow finished successfully with no extras set.");
                            return;
                        } else {
                            onSetupSuccessful(new ApiResult(data.getExtras()).getAccountPuid());
                            return;
                        }
                    case SignInWebFlowRequest /*0*/:
                        sendCancelledResult();
                        return;
                    case AddPendingRequest /*1*/:
                        sendFailureResult(new ApiResult(data.getExtras()).getException());
                        return;
                    default:
                        sendFailureResult("Sign in activity finished with unexpected result code: " + resultCode);
                        return;
                }
            case AddPendingRequest /*1*/:
                return;
            case AccountAddedRequest /*2*/:
                switch (resultCode) {
                    case NoRequest /*-1*/:
                    case SignInWebFlowRequest /*0*/:
                        sendSuccessResult(this._accountPuid);
                        return;
                    default:
                        sendFailureResult("Account added activity finished with unexpected result code: " + resultCode);
                        return;
                }
            default:
                Logger.error("Received activity result for unknown request code: " + requestCode);
                sendFailureResult("Received activity result for unknown request code: " + requestCode);
                return;
        }
    }

    protected Uri getLoginUri(ServerConfig serverConfig, boolean isCallerMsa, boolean isWreply) {
        Endpoint endpoint = isCallerMsa ? isWreply ? Endpoint.SignupWReplyMsa : Endpoint.ConnectMsa : isWreply ? Endpoint.SignupWReplyPartner : Endpoint.ConnectPartner;
        Builder uriBuilder = Uri.parse(serverConfig.getUrl(endpoint).toExternalForm()).buildUpon();
        addCommonQueryStringParams(uriBuilder);
        for (Entry<String, String> property : BundleMarshaller.appPropertiesFromBundle(getIntent().getExtras()).getServerQueryStringValues().entrySet()) {
            uriBuilder.appendQueryParameter((String) property.getKey(), (String) property.getValue());
        }
        if (isWreply) {
            return Uris.appendMarketQueryString(getApplicationContext(), uriBuilder.build());
        }
        return uriBuilder.build();
    }

    protected Uri getSignupUri(ServerConfig serverConfig, boolean isCallerMsa) {
        Builder uriBuilder = Uri.parse(serverConfig.getUrl(isCallerMsa ? Endpoint.SignupMsa : Endpoint.SignupPartner).toExternalForm()).buildUpon();
        addCommonQueryStringParams(uriBuilder);
        for (Entry<String, String> property : BundleMarshaller.appPropertiesFromBundle(getIntent().getExtras()).getServerQueryStringValues().entrySet()) {
            uriBuilder.appendQueryParameter((String) property.getKey(), (String) property.getValue());
        }
        uriBuilder.appendQueryParameter(WReplyLabel, getLoginUri(serverConfig, isCallerMsa, true).toString());
        return uriBuilder.build();
    }

    protected void addCommonQueryStringParams(Builder uriBuilder) {
        uriBuilder.appendQueryParameter(PlatformLabel, PlatformName + Resources.getSdkVersion(getApplicationContext()));
        uriBuilder.appendQueryParameter(AppProperties.ClientIdKey, Applications.buildClientAppUri(getApplicationContext(), getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey)));
    }

    protected void addTelemetryToResult(Intent data) {
        if (data != null && data.getExtras() != null) {
            ApiResult result = new ApiResult(data.getExtras());
            if (result.hasWebFlowTelemetryEvents()) {
                this._resultSender.putWebFlowTelemetryFields(result).set();
            }
        }
    }

    protected void sendSuccessResult(String accountPuid) {
        Assertion.check(accountPuid != null);
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendSuccess(new ApiResult().setAccountPuid(accountPuid));
        } else {
            AuthenticatorUserAccount account = new TypedStorage(getApplicationContext()).readAccount(accountPuid);
            if (account == null) {
                sendFailureResult(new InternalException("AddAccountActivity could not acquire newly added account."));
                return;
            }
            this._resultSender.putLimitedUserAccount(account).set();
        }
        finish();
    }

    protected void sendFailureResult(Exception exception) {
        Assertion.check(exception != null);
        Logger.error("Failed to add account.", exception);
        ClientAnalytics.get().logException(exception);
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendFailure(exception);
        } else {
            this._resultSender.putException(exception).set();
        }
        finish();
    }

    protected void sendFailureResult(String message) {
        sendFailureResult(new InternalException(message));
    }

    protected void sendCancelledResult() {
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendUserCanceled();
        }
        finish();
    }

    public void finish() {
        if (this._pendingChildRequest != NoRequest) {
            finishActivity(this._pendingChildRequest);
            this._pendingChildRequest = NoRequest;
        }
        super.finish();
    }

    protected void onSetupSuccessful(String accountPuid) {
        BackupService.pushBackup(getApplicationContext());
        if (!isFinishing()) {
            finishActivity(AddPendingRequest);
            sendSuccessResult(accountPuid);
        }
    }

    protected void onSetupFailure(Exception exception) {
        sendFailureResult(exception);
    }

    public static Intent getSignUpIntent(Context applicationContext, Bundle appProperties, String clientPackageName, Bundle clientState) {
        return new Intent(applicationContext, AddAccountActivity.class).setAction(ActionSignUpAccount).putExtra(BundleMarshaller.AppPropertiesKey, appProperties).putExtra(BundleMarshaller.ClientPackageNameKey, clientPackageName).putExtra(BundleMarshaller.ClientStateBundleKey, clientState).setData(new DataBuilder().add(appProperties).add(clientPackageName).build());
    }

    public static Intent getSignInIntent(Context applicationContext, Bundle appProperties, String clientPackageName, Bundle clientState) {
        return new Intent(applicationContext, AddAccountActivity.class).setAction(ActionAddAccount).putExtra(BundleMarshaller.AppPropertiesKey, appProperties).putExtra(BundleMarshaller.ClientPackageNameKey, clientPackageName).putExtra(BundleMarshaller.ClientStateBundleKey, clientState).setData(new DataBuilder().add(appProperties).add(clientPackageName).build());
    }
}
