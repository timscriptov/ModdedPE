package com.microsoft.onlineid.internal.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import com.microsoft.onlineid.SecurityScope;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequest.Extras;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.exception.PromptNeededException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.internal.ui.PropertyBag.Key;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.KnownEnvironment;
import com.microsoft.onlineid.sts.exception.InlineFlowException;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;
import com.microsoft.onlineid.userdata.SignUpData;

public class JavaScriptBridge {
    private static final String PPCRL_REQUEST_E_USER_CANCELED = "80048842";
    private IWebPropertyProvider _assetBundlePropertyProvider;
    private boolean _isOutOfBandInterrupt;
    private final PropertyBag _propertyBag;
    private final ServerConfig _serverConfig;
    private final WebTelemetryRecorder _telemetryRecorder;
    private final TicketManager _ticketManager;
    private final TypedStorage _typedStorage;
    private final WebFlowActivity _webFlowActivity;

    @Deprecated
    public JavaScriptBridge() {
        this._webFlowActivity = null;
        this._telemetryRecorder = null;
        this._propertyBag = null;
        this._serverConfig = null;
        this._typedStorage = null;
        this._ticketManager = null;
    }

    public JavaScriptBridge(WebFlowActivity webFlowActivity, WebTelemetryRecorder telemetryRecorder, WebFlowTelemetryData telemetryData) {
        this._webFlowActivity = webFlowActivity;
        this._telemetryRecorder = telemetryRecorder;
        this._propertyBag = new PropertyBag();
        Context applicationContext = this._webFlowActivity.getApplicationContext();
        this._serverConfig = new ServerConfig(applicationContext);
        this._typedStorage = new TypedStorage(applicationContext);
        this._ticketManager = new TicketManager(applicationContext);
        populatePropertyBag();
        populateTelemetryData(telemetryData);
    }

    public void setAssetBundlePropertyProvider(IWebPropertyProvider provider) {
        this._assetBundlePropertyProvider = provider;
    }

    protected void populatePropertyBag() {
        SignUpData signUpData = new SignUpData(this._webFlowActivity.getApplicationContext());
        this._propertyBag.set(Key.PfFirstName, signUpData.getFirstName());
        this._propertyBag.set(Key.PfLastName, signUpData.getLastName());
        this._propertyBag.set(Key.PfPhone, signUpData.getPhone());
        this._propertyBag.set(Key.PfCountryCode, signUpData.getCountryCode());
    }

    private void populateTelemetryData(WebFlowTelemetryData telemetryData) {
        try {
            Context applicationContext = this._webFlowActivity.getApplicationContext();
            boolean isRequestorMaster = PackageInfoHelper.isCurrentApp(telemetryData.getCallingAppPackageName(), applicationContext);
            this._propertyBag.set(Key.TelemetryAppVersion, telemetryData.getCallingAppVersionName());
            this._propertyBag.set(Key.TelemetryIsRequestorMaster, Boolean.toString(isRequestorMaster));
            this._propertyBag.set(Key.TelemetryNetworkType, NetworkConnectivity.getNetworkTypeForServerTelemetry(applicationContext));
            this._propertyBag.set(Key.TelemetryPrecaching, Boolean.toString(telemetryData.getWasPrecachingEnabled()));
        } catch (Exception e) {
            Logger.error("Encountered error setting telemetry items in property bag.", e);
        }
    }

    @JavascriptInterface
    public void FinalBack() {
        this._webFlowActivity.cancel();
    }

    @JavascriptInterface
    public void FinalNext() {
        String action = this._webFlowActivity.getIntent().getAction();
        String errorCode = this._propertyBag.get(Key.ErrorCode);
        try {
            if (TextUtils.isEmpty(errorCode)) {
                if (WebFlowActivity.ActionSignIn.equals(action) || WebFlowActivity.ActionSignUp.equals(action)) {
                    handleSignInResult();
                } else if (WebFlowActivity.ActionResolveInterrupt.equals(action)) {
                    handleInterruptResult();
                } else {
                    throw new InternalException("Unknown Action: " + action);
                }
            } else if (this._isOutOfBandInterrupt) {
                this._webFlowActivity.cancel();
            } else {
                String extendedErrorString = this._propertyBag.get(Key.ExtendedErrorString);
                if (extendedErrorString == null || !extendedErrorString.contains(PPCRL_REQUEST_E_USER_CANCELED)) {
                    throw new InlineFlowException(this._propertyBag.get(Key.ErrorString), this._propertyBag.get(Key.ErrorURL), errorCode, extendedErrorString);
                }
                FinalBack();
            }
        } catch (Exception ex) {
            ClientAnalytics.get().logException(ex);
            Logger.error("Web flow with action " + action + " failed.", ex);
            this._webFlowActivity.sendResult(1, new ApiResult().setException(ex).asBundle());
        }
    }

    protected void handleSignInResult() throws InternalException, NetworkException, InvalidResponseException, StsException {
        AuthenticatorUserAccount account = createAccountFromProperties(this._propertyBag);
        if (account.isNewAndInOutOfBandInterrupt()) {
            try {
                this._ticketManager.getTicketNoCache(account, new SecurityScope(KnownEnvironment.Production.getEnvironment().equals(this._serverConfig.getEnvironment()) ? "ssl.live.com" : "ssl.live-int.com", "mbi_ssl"), null);
                return;
            } catch (PromptNeededException ex) {
                final Intent intent = ex.getRequest().asIntent();
                intent.removeExtra(Extras.Continuation.getKey());
                intent.fillIn(this._webFlowActivity.getIntent(), 0);
                intent.setAction(WebFlowActivity.ActionResolveInterrupt);
                intent.putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, this._telemetryRecorder.isRequested());
                this._webFlowActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        JavaScriptBridge.this._webFlowActivity.setIntent(intent);
                        JavaScriptBridge.this._webFlowActivity.recreate();
                    }
                });
                return;
            }
        }
        this._typedStorage.writeAccount(account);
        this._webFlowActivity.sendResult(-1, new ApiResult().setAccountPuid(account.getPuid()).asBundle());
    }

    protected void handleInterruptResult() throws AccountNotFoundException, InternalException {
        AuthenticatorUserAccount account = this._typedStorage.readAccount(new ApiRequest(null, this._webFlowActivity.getIntent()).getAccountPuid());
        if (account == null) {
            throw new AccountNotFoundException("Account was deleted before interrupt could be resolved.");
        }
        String tokenValue = this._propertyBag.get(Key.DAToken);
        String encodedSessionKey = this._propertyBag.get(Key.DASessionKey);
        if (TextUtils.isEmpty(tokenValue) || TextUtils.isEmpty(encodedSessionKey)) {
            Logger.warning("WebWizard property bag did not have DAToken/SessionKey");
        } else {
            try {
                account.setDAToken(new DAToken(tokenValue, Base64.decode(encodedSessionKey, 2)));
                this._typedStorage.writeAccount(account);
            } catch (IllegalArgumentException e) {
                Logger.error("Could not decode Base64: " + encodedSessionKey);
                throw new InternalException("Session Key from interrupt resolution was invalid.");
            }
        }
        String flowToken = this._propertyBag.get(Key.STSInlineFlowToken);
        if (TextUtils.isEmpty(flowToken)) {
            String message = "Interrupt resolution did not return a flow token.";
            Logger.error("Interrupt resolution did not return a flow token.");
            Assertion.check(false, "Interrupt resolution did not return a flow token.");
        }
        this._webFlowActivity.sendResult(-1, new ApiResult().setFlowToken(flowToken).asBundle());
    }

    @JavascriptInterface
    public void Property(String propertyName, String newPropertyValue) {
        Key key = getKeyForName(propertyName);
        if (key == null) {
            return;
        }
        if (this._assetBundlePropertyProvider == null || !this._assetBundlePropertyProvider.handlesProperty(key)) {
            this._propertyBag.set(key, newPropertyValue);
            if (key.equals(Key.IsSignUp)) {
                Logger.info(Key.IsSignUp + "=" + newPropertyValue);
                ClientAnalytics.get().logEvent(ClientAnalytics.AppAccountsCategory, ClientAnalytics.SignUp);
                return;
            }
            return;
        }
        this._assetBundlePropertyProvider.setProperty(key, newPropertyValue);
    }

    @JavascriptInterface
    public String Property(String propertyName) {
        Key key = getKeyForName(propertyName);
        if (key == null) {
            return null;
        }
        if (this._assetBundlePropertyProvider == null || !this._assetBundlePropertyProvider.handlesProperty(key)) {
            return this._propertyBag.get(key);
        }
        return this._assetBundlePropertyProvider.getProperty(key);
    }

    void setIsOutOfBandInterrupt() {
        this._isOutOfBandInterrupt = true;
    }

    protected void validateProperty(Key key, String value) throws InternalException {
        if (TextUtils.isEmpty(value)) {
            String message = "PropertyBag was missing required property: " + key.name();
            Logger.error(message);
            throw new InternalException(message);
        }
    }

    protected AuthenticatorUserAccount createAccountFromProperties(PropertyBag properties) throws InternalException {
        String tokenValue = properties.get(Key.DAToken);
        String encodedSessionKey = properties.get(Key.DASessionKey);
        String username = properties.get(Key.SigninName);
        String cid = properties.get(Key.CID);
        String puid = properties.get(Key.PUID);
        validateProperty(Key.DAToken, tokenValue);
        validateProperty(Key.DASessionKey, encodedSessionKey);
        validateProperty(Key.SigninName, username);
        return new AuthenticatorUserAccount(puid, cid, username, new DAToken(tokenValue, Base64.decode(encodedSessionKey, 2)));
    }

    private static Key getKeyForName(String propertyName) {
        Key key = null;
        if (propertyName == null) {
            Assertion.check(false);
        } else {
            try {
                key = Key.valueOf(propertyName);
            } catch (IllegalArgumentException e) {
            }
        }
        return key;
    }

    @JavascriptInterface
    public void ReportTelemetry(String jsonEvent) {
        if (this._telemetryRecorder != null) {
            this._telemetryRecorder.recordEvent(jsonEvent);
        }
    }
}
