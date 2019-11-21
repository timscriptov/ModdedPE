package com.microsoft.onlineid.analytics;

import android.content.Context;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders.AppViewBuilder;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.HitBuilders.ExceptionBuilder;
import com.google.android.gms.analytics.HitBuilders.TimingBuilder;
import com.google.android.gms.analytics.Tracker;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.sts.Cryptography;
import java.util.Map;
import java.util.Map.Entry;

public class ClientAnalytics implements IClientAnalytics {
    public static final String AboutScreen = "About screen";
    public static final String AccountAddPendingScreen = "Account add pending screen";
    public static final String AccountAddedScreen = "Account add done screen";
    public static final String AccountPickerScreen = "Account picker";
    public static final String AccountsScreen = "Accounts screen";
    public static final String AddAccount = "Add account";
    public static final String AppAccountsCategory = "Authenticator accounts";
    public static final String AppCertificatesCategory = "Application certificates";
    public static final String ApproveSession = "Approve session";
    public static final String AtStartOfWebFlow = "At start of web flow";
    public static final String AvailableCertificates = "Available certificates";
    public static final String CloudRegistrationCategory = "Notification registration";
    public static final String CollectionConsistencyError = "Collection consistency error";
    public static final String CountryCode = "Country code";
    public static final String DenySession = "Deny session";
    public static final String DisableSessionApproval = "Disable session approval";
    public static final String DismissSession = "Dismiss session";
    private static final int DispatchPeriodSeconds = 10;
    public static final String DoesntExistInAccountManager = "Does not exist in Account Manager";
    public static final String DoesntExistInMeContact = "Does not exist in Me Contact";
    public static final String DoesntExistInTelephonyManager = "Does not exist in Telephony Manager";
    public static final String DuringWebFlow = "During web flow";
    public static final String EnableSessionApproval = "Enable session approval";
    public static final String EnableSessionApprovalWithoutRegistrationID = "Enable session approval without GCM registration ID";
    public static final String ExistsInAccountManager = "Exists in Account Manager";
    public static final String ExistsInMeContact = "Exists in Me Contact";
    public static final String ExistsInTelephonyManager = "Exists in Telephony Manager";
    public static final String FailedLabel = "Failed";
    public static final String FailedToCheckUnlockProcedure = "The check for an unlock procedure failed";
    public static final String FailedToGetMarketizedName = "Failed to get marketized name";
    public static final String FirstName = "First name";
    public static final String FirstRunScreen = "First run screen";
    public static final String GcmIDAddedToAccount = "GCM ID added to account on MSA server after registration";
    public static final String GcmIDInGoodState = "with GCM ID in good state";
    public static final String GcmIDMismatch = "with account GCM ID != app GCM ID";
    public static final String GcmRegistrationEvent = "GCM registration";
    private static final String GoogleAnalyticsPropertyID = "UA-50206275-2";
    public static final String GoogleEmail = "Google email";
    public static final String GoogleEmailCount = "Google email count";
    public static final String HasUnlockProcedure = "User has an unlock procedure";
    public static final String HomePhoneNumber = "Home phone number";
    public static final String ImplicitDisable = "implicit (removed account)";
    public static final String InitiateAccountAdd = "Initiate account add";
    private static IClientAnalytics Instance = null;
    public static final String LastName = "Last name";
    public static final String LoadProfileImage = "Load profile image";
    public static final String LoginCloudPinCollectionPendingScreen = "Login cloud pin pending screen";
    public static final String LoginCloudPinCollectionVerificationScreen = "Login cloud pin collection screen";
    public static final String MigrationAttempts = "Migration attempts";
    public static final String MigrationCategory = "Migration and Upgrade";
    public static final String MobilePhoneNumber = "Mobile phone number";
    public static final String NavigationCategory = "Navigation";
    public static final String NgcApproveSession = "Approve NGC session";
    public static final String NgcAttemptingToApproveSession = "Attempting to approve NGC session";
    public static final String NgcCategory = "NGC";
    public static final String NgcRegistrationCloudPinMismatch = "Entered cloud PINs did not match";
    public static final String NgcRegistrationFailed = "Device registration failed";
    public static final String NgcRegistrationSucceeded = "Device registration succeeded";
    public static final String NgcSessionApproved = "Session Approved";
    public static final String NoAccountGcmID = "with no GCM ID for account";
    public static final String NoAppGcmID = "with no GCM ID for current app version";
    public static final String NoNetworkConnectivity = "No network connectivity";
    public static final String NoUnlockProcedure = "User has no unlock procedure";
    public static final String NotificationErrorTap = "Tapped session error notification";
    public static final String NotificationExpired = "Session notification expired";
    public static final String NotificationScreen = "Notification drawer";
    public static final String NotificationTap = "Tapped session notification";
    public static final String PerformanceCategory = "Performance";
    public static final String PickAccount = "Pick an account";
    public static final String QRCodeAuthenticationCategory = "QR code authentication";
    public static final String QRCodeScannerScreen = "QR code scanner screen";
    public static final String RefreshSessionList = "Refresh sessions list";
    public static final String RegistrationCloudPinCollectionPendingScreen = "Registration cloud pin collection pending screen";
    public static final String RegistrationCloudPinCollectionSetupScreen = "Registration cloud pin collection setup screen";
    public static final String RegistrationCloudPinCollectionVerificationScreen = "Registration cloud pin collection verification screen";
    public static final String RemoveAccount = "Remove account";
    public static final String RenderingCategory = "Rendering";
    public static final String ScanMsaQRCode = "Scanned Msa QR code";
    public static final String ScanNonMsaQRCode = "Scanned non-Msa QR code";
    public static final String ScreenNameParam = "&cd";
    public static final String SdkCategory = "SDK";
    public static final String SessionApprovalCategory = "Session approval";
    public static final String SessionSeenWithoutNotification = "Session seen without notification";
    private static final int SessionTimeoutSeconds = 5;
    public static final String SessionsCategory = "Sessions";
    public static final String SessionsScreen = "Sessions screen";
    public static final String SignUp = "Sign up success";
    public static final String SmsVerificationCategory = "SMS verification";
    public static final String SsoError = "SSO error";
    public static final String SsoFallback = "SSO fallback";
    public static final String StorageCategory = "Storage";
    public static final String StsRequestCategory = "STS requests";
    public static final String SucceededLabel = "Succeeded";
    public static final String TapQRCodeImageButton = "Tap QR code image button to open scanner";
    public static final String TapQRCodeTextLink = "Tap QR code text link to open scanner";
    public static final String TotalAccounts = "Total accounts";
    public static final String TotalAppAccountsCategory = "Total authenticator accounts";
    public static final String TotalPotentialSsoServices = "Total potential SSO services";
    public static final String TotalSAAccountsCategory = "Total session approval accounts";
    public static final String TotalTrustedSsoServices = "Total trusted SSO services";
    public static final String UniqueEmailCount = "Unique email count";
    public static final String UnlockProcedureCategory = "Device unlock procedure";
    public static final String UserDataCategory = "User data";
    public static final String Verified = "Successfully verified";
    public static final String ViaAccountPicker = "via account picker";
    public static final String ViaAccountsScreen = "via accounts screen";
    public static final String ViaAddAccountInApp = "via add account in app";
    public static final String ViaAddButton = "via add button";
    public static final String ViaFirstRun = "via first run";
    public static final String ViaFirstTimeUser = "via first time user";
    public static final String ViaMenu = "via menu";
    public static final String ViaNotification = "via notification drawer";
    public static final String ViaReturningUser = "via returning user";
    public static final String ViaSessionsScreen = "via sessions screen";
    public static final String WebWizardScreen = "Web wizard";
    private final Context _applicationContext;
    private boolean _clockSkewLogged;
    private final Tracker _tracker;

    private ClientAnalytics(Context applicationContext) {
        this._clockSkewLogged = false;
        this._applicationContext = applicationContext;
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(applicationContext);
        analytics.setLocalDispatchPeriod(DispatchPeriodSeconds);
        this._tracker = analytics.newTracker(GoogleAnalyticsPropertyID);
        this._tracker.setAppName("Authenticator");
        this._tracker.setSessionTimeout(5);
        ExceptionReporter reporter = new ExceptionReporter(this._tracker, Thread.getDefaultUncaughtExceptionHandler(), applicationContext);
        reporter.setExceptionParser(new MsaExceptionParser(applicationContext, null));
        Thread.setDefaultUncaughtExceptionHandler(reporter);
        this._tracker.set("&cd1", NetworkConnectivity.getNetworkTypeForAnalytics(applicationContext));
    }

    protected ClientAnalytics() {
        this._clockSkewLogged = false;
        this._applicationContext = null;
        this._tracker = null;
    }

    public static synchronized void initialize(Context applicationContext) {
        synchronized (ClientAnalytics.class) {
            if (Instance == null) {
                Instance = PackageInfoHelper.isRunningInAuthenticatorApp(applicationContext) ? new ClientAnalytics(applicationContext) : new NopClientAnalytics();
            }
        }
    }

    public static synchronized IClientAnalytics get() {
        IClientAnalytics nopClientAnalytics;
        synchronized (ClientAnalytics.class) {
            if (Instance == null) {
                nopClientAnalytics = new NopClientAnalytics();
            } else {
                nopClientAnalytics = Instance;
            }
        }
        return nopClientAnalytics;
    }

    public void setTestMode() {
        GoogleAnalytics.getInstance(this._applicationContext).setDryRun(true);
    }

    public IClientAnalytics send(Map<String, String> params) {
        this._tracker.send(params);
        return this;
    }

    public IClientAnalytics logScreenView(String screenName) {
        Assertion.check(screenName != null);
        setScreenName(screenName);
        send(new AppViewBuilder().build());
        return this;
    }

    public IClientAnalytics logEvent(String category, String action) {
        return logEvent(category, action, null, null);
    }

    public IClientAnalytics logEvent(String category, String action, String label) {
        return logEvent(category, action, label, null);
    }

    public IClientAnalytics logEvent(String category, String action, String label, Long value) {
        if (category == null || action == null) {
            Logger.error("At least category and action must be specified to log an event.");
            Assertion.check(false);
        } else {
            EventBuilder builder = new EventBuilder();
            builder.setCategory(category);
            builder.setAction(action);
            if (label != null) {
                builder.setLabel(label);
            }
            if (value != null) {
                builder.setValue(value.longValue());
            }
            send(builder.build());
        }
        return this;
    }

    public IClientAnalytics logTotalAccountsEvent(String category, int oldAccountCount, int newAccountCount) {
        return get().logEvent(category, Strings.pluralize((long) oldAccountCount, "account", "accounts") + " to " + Strings.pluralize((long) newAccountCount, "account", "accounts"));
    }

    public TimedAnalyticsEvent createTimedEvent(String category, String name, String label) {
        return new TimedAnalyticsEvent(this._tracker, category, name, label);
    }

    public TimedAnalyticsEvent createTimedEvent(String category, String name) {
        return createTimedEvent(category, name, null);
    }

    public IClientAnalytics logException(Throwable throwable) {
        boolean z;
        if (throwable != null) {
            z = true;
        } else {
            z = false;
        }
        Assertion.check(z);
        send(new ExceptionBuilder().setDescription(new MsaExceptionParser(this._applicationContext, null).getDescription(Thread.currentThread().getName(), throwable)).setFatal(false).build());
        return this;
    }

    public IClientAnalytics logClockSkew(long skew) {
        if (!this._clockSkewLogged) {
            send(new TimingBuilder().setCategory("Clock skew").setVariable("Clock skew adjusted").setLabel(skew < 0 ? "Server ahead" : "Client ahead").setValue(Math.abs(skew)).build());
            this._clockSkewLogged = true;
        }
        return this;
    }

    public IClientAnalytics logCertificates(Map<String, byte[]> signatures) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, byte[]> signature : signatures.entrySet()) {
            String signatureBase32 = Cryptography.encodeBase32((byte[]) signature.getValue());
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(signatureBase32).append('(').append((String) signature.getKey()).append(')');
        }
        send(new EventBuilder().setCategory(AppCertificatesCategory).setAction(AvailableCertificates).setLabel(stringBuilder.toString()).build());
        return this;
    }

    protected void setScreenName(String screenName) {
        this._tracker.setScreenName(screenName);
    }
}
