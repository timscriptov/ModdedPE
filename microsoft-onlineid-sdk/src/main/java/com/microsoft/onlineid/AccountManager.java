package com.microsoft.onlineid;

import android.content.Context;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Bundles;
import com.microsoft.onlineid.internal.IFailureCallback;
import com.microsoft.onlineid.internal.IUserInteractionCallback;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.MsaSsoClient;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import com.microsoft.onlineid.internal.sso.client.SsoRunnable;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AccountManager {
    private IAccountCallback _accountCallback;
    private IAccountCollectionCallback _accountCollectionCallback;
    private final Context _applicationContext;
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final MsaSsoClient _ssoClient;
    private ITelemetryCallback _telemetryCallback;
    private ITicketCallback _ticketCallback;

    public AccountManager(Context applicationContext) {
        this(applicationContext, new OnlineIdConfiguration());
    }

    public AccountManager(Context context, OnlineIdConfiguration onlineIdConfiguration) {
        if (context.getApplicationContext() != null) {
            context = context.getApplicationContext();
        }
        this._applicationContext = context;
        if (onlineIdConfiguration == null) {
            onlineIdConfiguration = new OnlineIdConfiguration();
        }
        this._onlineIdConfiguration = onlineIdConfiguration;
        this._ssoClient = new MsaSsoClient(this._applicationContext);
        ClientAnalytics.initialize(this._applicationContext);
        Logger.initialize(this._applicationContext);
    }

    public void getAccount(Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getAccountRunnable(state)).start();
    }

    protected SsoRunnable getAccountRunnable(final Bundle state) {
        return new SsoRunnable(this._accountCallback, state) {
            public void performRequest() throws AuthenticationException {
                SsoResponse<AuthenticatorUserAccount> ssoResponse = AccountManager.this._ssoClient.getAccount(AccountManager.this._onlineIdConfiguration, state);
                if (ssoResponse.hasData()) {
                    AccountManager.this._accountCallback.onAccountAcquired(new UserAccount(AccountManager.this.getAccountManager(), (AuthenticatorUserAccount) ssoResponse.getData()), state);
                } else {
                    AccountManager.this._accountCallback.onUINeeded(ssoResponse.getPendingIntent(), state);
                }
            }
        };
    }

    public void getSignInIntent(SignInOptions options, Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getSignInIntentRunnable(options, state)).start();
    }

    protected SsoRunnable getSignInIntentRunnable(SignInOptions options, Bundle state) {
        final SignInOptions signInOptions = options;
        final Bundle bundle = state;
        return new SsoRunnable(this._accountCallback, state) {
            public void performRequest() throws AuthenticationException {
                AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getSignInIntent(signInOptions, AccountManager.this._onlineIdConfiguration, bundle), bundle);
            }
        };
    }

    public void getSignUpIntent(Bundle state) {
        getSignUpIntent(null, state);
    }

    public void getSignUpIntent(SignUpOptions options, Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getSignUpIntentRunnable(options, state)).start();
    }

    protected SsoRunnable getSignUpIntentRunnable(SignUpOptions options, Bundle state) {
        final SignUpOptions signUpOptions = options;
        final Bundle bundle = state;
        return new SsoRunnable(this._accountCallback, state) {
            public void performRequest() throws AuthenticationException {
                AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getSignUpIntent(signUpOptions, AccountManager.this._onlineIdConfiguration, bundle), bundle);
            }
        };
    }

    public void getAccountById(String cid, Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getAccountByIdRunnable(cid, state)).start();
    }

    protected SsoRunnable getAccountByIdRunnable(String cid, Bundle state) {
        final String str = cid;
        final Bundle bundle = state;
        return new SsoRunnable(this._accountCallback, state) {
            public void performRequest() throws AuthenticationException {
                try {
                    Strings.verifyArgumentNotNullOrEmpty(str, "cid");
                    AccountManager.this._accountCallback.onAccountAcquired(new UserAccount(AccountManager.this.getAccountManager(), AccountManager.this._ssoClient.getAccountById(str, bundle)), bundle);
                } catch (AccountNotFoundException e) {
                    AccountManager.this._accountCallback.onAccountSignedOut(str, false, bundle);
                }
            }
        };
    }

    public void getAllAccounts(Bundle state) {
        verifyCallback(this._accountCollectionCallback, IAccountCollectionCallback.class.getSimpleName());
        new Thread(getAllAccountsRunnable(state)).start();
    }

    protected SsoRunnable getAllAccountsRunnable(final Bundle state) {
        return new SsoRunnable(this._accountCollectionCallback, state) {
            public void performRequest() throws AuthenticationException {
                Set<AuthenticatorUserAccount> fullAccounts = AccountManager.this._ssoClient.getAllAccounts(state);
                Set<UserAccount> result = new HashSet();
                for (AuthenticatorUserAccount account : fullAccounts) {
                    result.add(new UserAccount(AccountManager.this.getAccountManager(), account));
                }
                AccountManager.this._accountCollectionCallback.onAccountCollectionAcquired(result, state);
            }
        };
    }

    public void getAccountPickerIntent(Iterable<String> cidExclusionList, Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getAccountPickerIntentRunnable(cidExclusionList, state)).start();
    }

    protected SsoRunnable getAccountPickerIntentRunnable(Iterable<String> cidExclusionList, Bundle state) {
        final Iterable<String> iterable = cidExclusionList;
        final Bundle bundle = state;
        return new SsoRunnable(this._accountCallback, state) {
            public void performRequest() throws AuthenticationException {
                ArrayList<String> excludedCids = new ArrayList();
                if (iterable != null) {
                    for (String cid : iterable) {
                        excludedCids.add(cid);
                    }
                }
                AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getAccountPickerIntent(excludedCids, AccountManager.this._onlineIdConfiguration, bundle), bundle);
            }
        };
    }

    public void getSignOutIntent(UserAccount account, Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getSignOutIntentRunnable(account, state)).start();
    }

    protected SsoRunnable getSignOutIntentRunnable(UserAccount account, Bundle state) {
        final UserAccount userAccount = account;
        final Bundle bundle = state;
        return new SsoRunnable(this._accountCallback, state) {
            public void performRequest() throws AuthenticationException {
                try {
                    AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getSignOutIntent(userAccount.getCid(), bundle), bundle);
                } catch (AccountNotFoundException e) {
                    AccountManager.this._accountCallback.onAccountSignedOut(userAccount.getCid(), false, bundle);
                }
            }
        };
    }

    void getTicket(UserAccount account, ISecurityScope scope, Bundle state) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        verifyCallback(this._ticketCallback, ITicketCallback.class.getSimpleName());
        new Thread(getTicketRunnable(account, scope, state)).start();
    }

    protected SsoRunnable getTicketRunnable(UserAccount account, ISecurityScope scope, Bundle state) {
        final UserAccount userAccount = account;
        final ISecurityScope iSecurityScope = scope;
        final Bundle bundle = state;
        return new SsoRunnable(this._ticketCallback, state) {
            public void performRequest() throws AuthenticationException {
                try {
                    SsoResponse<Ticket> ssoResponse = AccountManager.this._ssoClient.getTicket(userAccount.getCid(), iSecurityScope, AccountManager.this._onlineIdConfiguration, bundle);
                    if (ssoResponse.hasData()) {
                        AccountManager.this._ticketCallback.onTicketAcquired((Ticket) ssoResponse.getData(), userAccount, bundle);
                    } else {
                        AccountManager.this._ticketCallback.onUINeeded(ssoResponse.getPendingIntent(), bundle);
                    }
                } catch (AccountNotFoundException e) {
                    AccountManager.this._accountCallback.onAccountSignedOut(userAccount.getCid(), false, bundle);
                }
            }
        };
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        IFailureCallback failureCallback;
        String resultTypeString = null;
        ResultType resultType = null;
        Bundle extras = null;
        Bundle state = null;
        if (data != null) {
            try {
                extras = data.getExtras();
                if (extras != null) {
                    state = extras.getBundle(BundleMarshaller.ClientStateBundleKey);
                    resultTypeString = extras.getString(BundleMarshaller.ActivityResultTypeKey);
                    resultType = ResultType.fromString(resultTypeString);
                }
            } catch (BadParcelableException e) {
                Logger.info("Caught BadParcelableException when checking extras, ignoring: " + e);
                return false;
            } catch (RuntimeException e2) {
                if (e2.getCause() == null || !(e2.getCause() instanceof ClassNotFoundException)) {
                    throw e2;
                }
                Logger.info("Caught RuntimeException when checking extras, ignoring: " + e2);
                return false;
            }
        }
        if (Settings.isDebugBuild()) {
            Logger.info("Activity result: request: " + requestCode + ", result: " + resultCode);
            Bundles.log("With extras:", extras);
        }
        if (resultType == null) {
            Logger.info("Unknown result type (" + resultTypeString + ") encountered, ignoring.");
            return false;
        }
        IUserInteractionCallback uiCallback = resultType == ResultType.Ticket ? this._ticketCallback : this._accountCallback;
        if (resultType == ResultType.Ticket) {
            failureCallback = this._ticketCallback;
        } else {
            failureCallback = this._accountCallback;
        }
        if (!(extras == null || this._telemetryCallback == null)) {
            ArrayList<String> webTelemetryEvents = extras.getStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey);
            boolean wereAllEventsCaptured = extras.getBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, false);
            if (!(webTelemetryEvents == null || webTelemetryEvents.isEmpty())) {
                this._telemetryCallback.webTelemetryEventsReceived(webTelemetryEvents, wereAllEventsCaptured);
            }
        }
        if (resultCode == 0) {
            uiCallback.onUserCancel(state);
        } else if (resultCode == -1) {
            try {
                if (BundleMarshaller.hasError(extras)) {
                    AuthenticationException exception = BundleMarshaller.exceptionFromBundle(extras);
                    if (exception instanceof AccountNotFoundException) {
                        String cid = extras.getString(BundleMarshaller.UserCidKey);
                        Assertion.check(cid != null, "Expect to find a CID for sign-out notification.");
                        this._accountCallback.onAccountSignedOut(cid, extras.getBoolean(BundleMarshaller.IsSignedOutOfThisAppOnlyKey), state);
                    } else {
                        failureCallback.onFailure(exception, state);
                    }
                } else if (BundleMarshaller.hasPendingIntent(extras)) {
                    uiCallback.onUINeeded(BundleMarshaller.pendingIntentFromBundle(extras), state);
                } else if (resultType == ResultType.Ticket && BundleMarshaller.hasTicket(extras)) {
                    this._ticketCallback.onTicketAcquired(BundleMarshaller.ticketFromBundle(extras), new UserAccount(this, BundleMarshaller.limitedUserAccountFromBundle(extras)), state);
                } else if (resultType == ResultType.Account && BundleMarshaller.hasLimitedUserAccount(extras)) {
                    this._accountCallback.onAccountAcquired(new UserAccount(this, BundleMarshaller.limitedUserAccountFromBundle(extras)), state);
                } else {
                    failureCallback.onFailure(new InternalException("Unexpected onActivityResult found."), state);
                }
            } catch (Throwable e3) {
                failureCallback.onFailure(new InternalException(e3), state);
            }
        }
        return true;
    }

    public AccountManager setAccountCallback(IAccountCallback callback) {
        this._accountCallback = callback;
        return this;
    }

    public AccountManager setTicketCallback(ITicketCallback callback) {
        this._ticketCallback = callback;
        return this;
    }

    public AccountManager setAccountCollectionCallback(IAccountCollectionCallback callback) {
        this._accountCollectionCallback = callback;
        return this;
    }

    public AccountManager setTelemetryCallback(ITelemetryCallback callback) {
        this._telemetryCallback = callback;
        return this;
    }

    private void verifyCallback(Object callback, String callbackName) {
        if (callback == null) {
            throw new IllegalStateException("You must specify an " + callbackName + " before invoking this method.");
        }
    }

    private AccountManager getAccountManager() {
        return this;
    }
}
