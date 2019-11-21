package com.microsoft.onlineid.internal.sso.client;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.MasterRedirectException;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountByIdRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountPickerIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAllAccountsRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignInIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignOutIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignUpIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetTicketRequest;
import com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest;
import com.microsoft.onlineid.internal.sso.exception.ClientNotAuthorizedException;
import com.microsoft.onlineid.internal.sso.exception.UnsupportedClientVersionException;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ConfigManager;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class MsaSsoClient {
    private final Context _applicationContext;
    private final ServerConfig _config;
    private final ConfigManager _configManager;
    private final MigrationManager _migrationManager;
    private final ServiceFinder _serviceFinder;

    public MsaSsoClient(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._config = new ServerConfig(applicationContext);
        this._serviceFinder = new ServiceFinder(applicationContext);
        this._configManager = new ConfigManager(applicationContext);
        this._migrationManager = new MigrationManager(applicationContext);
    }

    public SsoResponse<AuthenticatorUserAccount> getAccount(OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (SsoResponse) performRequestWithFallback(new GetAccountRequest(this._applicationContext, state, onlineIdConfiguration));
    }

    public AuthenticatorUserAccount getAccountById(String cid, Bundle state) throws AuthenticationException {
        return (AuthenticatorUserAccount) performRequestWithFallback(new GetAccountByIdRequest(this._applicationContext, state, cid));
    }

    public Set<AuthenticatorUserAccount> getAllAccounts(Bundle state) throws AuthenticationException {
        return (Set) performRequestWithFallback(new GetAllAccountsRequest(this._applicationContext, state));
    }

    public PendingIntent getSignInIntent(SignInOptions signInOptions, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignInIntentRequest(this._applicationContext, state, signInOptions, onlineIdConfiguration));
    }

    public PendingIntent getSignUpIntent(SignUpOptions signUpOptions, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignUpIntentRequest(this._applicationContext, state, signUpOptions, onlineIdConfiguration));
    }

    public PendingIntent getSignOutIntent(String cid, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignOutIntentRequest(this._applicationContext, state, cid));
    }

    public PendingIntent getAccountPickerIntent(ArrayList<String> cidExclusionList, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetAccountPickerIntentRequest(this._applicationContext, state, cidExclusionList, onlineIdConfiguration));
    }

    public SsoResponse<Ticket> getTicket(String cid, ISecurityScope securityScope, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (SsoResponse) performRequestWithFallback(new GetTicketRequest(this._applicationContext, state, cid, securityScope, onlineIdConfiguration));
    }

    protected <T> T performRequestWithFallback(SingleSsoRequest<T> request) throws AuthenticationException {
        this._configManager.updateIfFirstDownloadNeeded();
        this._migrationManager.migrateAndUpgradeStorageIfNeeded();
        int maxTries = this._config.getInt(Int.MaxTriesForSsoRequestWithFallback);
        if (maxTries < 1) {
            String error = "Invalid MaxTriesForSsoRequestWithFallback: " + maxTries;
            Logger.error(error);
            ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoFallback, error);
            maxTries = 1;
        }
        int tries = 0;
        boolean isConfigUpToDate = false;
        Iterator<SsoService> iterator = this._serviceFinder.getOrderedSsoServices().iterator();
        SsoService ssoService = iterator.hasNext() ? (SsoService) iterator.next() : null;
        while (tries < maxTries && ssoService != null) {
            try {
                return request.performRequest(ssoService);
            } catch (MasterRedirectException e) {
                String redirectedPackage = e.getRedirectRequestTo();
                String redirectMessage = "Redirect to: " + redirectedPackage;
                Logger.info(redirectMessage, e);
                ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoFallback, redirectMessage);
                ssoService = this._serviceFinder.getSsoService(redirectedPackage);
                if (ssoService == null) {
                    String errorMessage = "Cannot find redirected master";
                    Logger.error("Cannot find redirected master", e);
                    ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoFallback, "Cannot find redirected master");
                    if (iterator.hasNext()) {
                        ssoService = (SsoService) iterator.next();
                    } else {
                        ssoService = null;
                    }
                }
            } catch (ServiceBindingException e2) {
                if (iterator.hasNext()) {
                    ssoService = (SsoService) iterator.next();
                } else {
                    ssoService = null;
                }
            } catch (ClientNotAuthorizedException e3) {
                return performRequestWithSelf(request);
            } catch (UnsupportedClientVersionException e4) {
                return performRequestWithSelf(request);
            } catch (ClientConfigUpdateNeededException e5) {
                Logger.info("Client needs config update: " + e5.getMessage());
                if (this._configManager.update()) {
                    iterator = this._serviceFinder.getOrderedSsoServices().iterator();
                    ssoService = iterator.hasNext() ? (SsoService) iterator.next() : null;
                    if (!isConfigUpToDate) {
                        tries--;
                    }
                    isConfigUpToDate = true;
                }
            }
        }
        String errorString = String.format(Locale.US, "SSO request failed after %d tries", new Object[]{Integer.valueOf(tries)});
        Logger.error(errorString);
        ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoFallback, errorString);
        return performRequestWithSelf(request);
        tries++;
    }

    private <T> T performRequestWithSelf(SingleSsoRequest<T> request) throws AuthenticationException {
        Logger.info("Attempting to self-service request.");
        return request.performRequest(this._serviceFinder.getSelfSsoService());
    }
}
