package com.microsoft.onlineid.internal.sso.client.request;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.SsoServiceError;
import com.microsoft.onlineid.internal.sso.client.ServiceBindingException;
import com.microsoft.onlineid.internal.sso.client.ServiceFinder;
import com.microsoft.onlineid.internal.sso.service.IMsaSsoService;
import com.microsoft.onlineid.internal.sso.service.IMsaSsoService.Stub;
import com.microsoft.onlineid.internal.sso.service.MsaSsoService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import java.util.Locale;

public abstract class SingleSsoRequest<T> implements ServiceConnection {
    static final String MaxTriesErrorMessage = "Max SSO tries exceeded.";
    static final int MaxWaitTimeForServiceBindingInMillis = 3000;
    protected final Context _applicationContext;
    protected final Bundle _clientState;
    protected final ServerConfig _config;
    private final Object _lock = new Object();
    protected IMsaSsoService _msaSsoService;
    protected boolean _serviceConnected;
    protected final TypedStorage _storage;

    protected abstract T performRequestTask() throws AuthenticationException, RemoteException;

    public void onServiceConnected(ComponentName className, IBinder service) {
        this._msaSsoService = Stub.asInterface(service);
        synchronized (this._lock) {
            this._serviceConnected = true;
            this._lock.notify();
        }
    }

    public void onServiceDisconnected(ComponentName className) {
        this._msaSsoService = null;
        this._serviceConnected = false;
    }

    public SingleSsoRequest(Context applicationContext, Bundle clientState) {
        this._applicationContext = applicationContext;
        this._clientState = clientState;
        this._config = new ServerConfig(applicationContext);
        this._storage = new TypedStorage(applicationContext);
        this._msaSsoService = null;
    }

    public Bundle getDefaultCallingParams() {
        Bundle params = new Bundle();
        try {
            Bundle metadata = this._applicationContext.getPackageManager().getServiceInfo(new ComponentName(this._applicationContext, MsaSsoService.class.getName()), 128).metaData;
            params.putString(BundleMarshaller.ClientPackageNameKey, this._applicationContext.getPackageName());
            params.putInt(BundleMarshaller.ClientSsoVersionKey, metadata.getInt(ServiceFinder.SsoVersionMetaDataName));
            params.putString(BundleMarshaller.ClientSdkVersionKey, metadata.getString(ServiceFinder.SdkVersionMetaDataName));
            params.putString(BundleMarshaller.ClientConfigVersionKey, this._config.getString(ServerConfig.Version));
            params.putLong(BundleMarshaller.ClientConfigLastDownloadedTimeKey, this._storage.readConfigLastDownloadedTime());
            params.putBundle(BundleMarshaller.ClientStateBundleKey, this._clientState);
        } catch (NameNotFoundException e) {
            Logger.error("Could not find calling SSO service meta-data.", e);
        }
        return params;
    }

    public T performRequest(SsoService ssoService) throws AuthenticationException {
        int maxTries = this._config.getInt(Int.MaxTriesForSsoRequestToSingleService);
        if (maxTries < 1) {
            String error = "Invalid MaxTriesForSsoRequestToSingleService: " + maxTries;
            Logger.error(error);
            ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoFallback, error);
            maxTries = 1;
        }
        int tries = 1;
        ServiceBindingException lastException = null;
        while (tries <= maxTries) {
            try {
                return tryPerformRequest(ssoService);
            } catch (ServiceBindingException e) {
                lastException = e;
                tries++;
            }
        }
        throw new ServiceBindingException(MaxTriesErrorMessage, lastException);
    }

    public T tryPerformRequest(SsoService ssoService) throws AuthenticationException {
        String requestType = getClass().getSimpleName();
        boolean bound = false;
        try {
            if (bind(ssoService)) {
                bound = true;
                synchronized (this._lock) {
                    if (!this._serviceConnected) {
                        this._lock.wait(3000);
                    }
                }
                if (this._serviceConnected) {
                    Logger.info("Bound to: " + ssoService.getPackageName() + " [" + requestType + "]");
                    T performRequestTask = performRequestTask();
                    if (1 != null) {
                        unbind();
                    }
                    return performRequestTask;
                }
                String error = "Timed out after " + String.valueOf(MaxWaitTimeForServiceBindingInMillis) + " milliseconds when trying to bind to: " + ssoService.getPackageName() + " [" + requestType + "]";
                Logger.warning(error);
                throw new ServiceBindingException(error);
            }
            String errorMessage = "Failed to bind to " + ssoService.getPackageName() + " [" + requestType + "]";
            Logger.error(errorMessage);
            throw new ServiceBindingException(errorMessage);
        } catch (AuthenticationException e) {
            try {
                throw e;
            } catch (Throwable th) {
                if (bound) {
                    unbind();
                }
            }
        } catch (Throwable e2) {
            Logger.error("Caught a SecurityException while trying to bind to " + ssoService.getPackageName() + ", service may not be exported correctly. [" + requestType + "]", e2);
            throw new ServiceBindingException(e2);
        } catch (Throwable e22) {
            Logger.error("SSO service request threw an unhandled exception.", e22);
            throw new InternalException(e22);
        }
    }

    boolean getIsServiceConnected() {
        return this._serviceConnected;
    }

    protected boolean bind(SsoService ssoService) {
        Intent intent = new Intent(SsoService.SsoServiceIntent).setPackage(ssoService.getPackageName());
        Logger.info(this._applicationContext.getPackageName() + " attempting to bind to: " + ssoService.getPackageName() + " [" + getClass().getSimpleName() + "]");
        return this._applicationContext.bindService(intent, this, 1);
    }

    protected void unbind() {
        this._serviceConnected = false;
        this._msaSsoService = null;
        this._applicationContext.unbindService(this);
    }

    protected static void checkForErrors(Bundle bundle) throws AuthenticationException {
        checkForErrors(bundle, true);
    }

    static void checkForErrors(Bundle bundle, boolean logError) throws AuthenticationException {
        if (BundleMarshaller.hasError(bundle)) {
            if (logError) {
                SsoServiceError error = SsoServiceError.get(bundle.getInt(BundleMarshaller.ErrorCodeKey));
                Logger.error(String.format(Locale.US, "%s: %s, %s", new Object[]{ClientAnalytics.SsoError, error.name(), bundle.getString(BundleMarshaller.ErrorMessageKey)}));
                ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.SsoError, error.name() + ": " + message);
            }
            throw BundleMarshaller.exceptionFromBundle(bundle);
        }
    }
}
