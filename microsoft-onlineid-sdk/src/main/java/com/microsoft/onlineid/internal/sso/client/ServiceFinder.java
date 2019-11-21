package com.microsoft.onlineid.internal.sso.client;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SignatureVerifier;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.sdk.BuildConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ServiceFinder {
    public static final Comparator<SsoService> MasterPrecedenceComparator = new Comparator<SsoService>() {
        public int compare(SsoService service, SsoService otherService) {
            if (service.getPackageName().equals(PackageInfoHelper.AuthenticatorPackageName)) {
                return -1;
            }
            if (otherService.getPackageName().equals(PackageInfoHelper.AuthenticatorPackageName)) {
                return 1;
            }
            int diff = otherService.getSsoVersion() - service.getSsoVersion();
            if (diff == 0) {
                return (int) (service.getFirstInstallTime() - otherService.getFirstInstallTime());
            }
            return diff;
        }
    };
    public static final String SdkVersionMetaDataName = "com.microsoft.msa.service.sdk_version";
    public static final String SsoVersionMetaDataName = "com.microsoft.msa.service.sso_version";
    private final Context _applicationContext;
    private final SignatureVerifier _signatureVerifier;

    public ServiceFinder(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._signatureVerifier = new SignatureVerifier(applicationContext);
    }

    protected List<SsoService> getPotentialSsoServices() {
        List<ResolveInfo> services = this._applicationContext.getPackageManager().queryIntentServices(new Intent(SsoService.SsoServiceIntent), 128);
        List<SsoService> ssoServices = new ArrayList();
        for (ResolveInfo info : services) {
            String packageName = info.serviceInfo.applicationInfo.packageName;
            Bundle metadata = info.serviceInfo.metaData;
            int ssoVersion = metadata.getInt(SsoVersionMetaDataName);
            if (ssoVersion <= 1) {
                Logger.warning("Ignoring SSO service from " + packageName + " because its SSO version is " + ssoVersion + ".");
            } else {
                try {
                    ssoServices.add(new SsoService(packageName, ssoVersion, metadata.getString(SdkVersionMetaDataName), getFirstInstallTime(packageName)));
                } catch (NameNotFoundException e) {
                    Logger.error("Could not find package when querying for first install time: " + packageName, e);
                }
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.TotalPotentialSsoServices, String.valueOf(ssoServices.size()));
        return ssoServices;
    }

    protected List<SsoService> getUntrustedOrderedSsoServices() {
        List<SsoService> orderedSsoServices = getPotentialSsoServices();
        Collections.sort(orderedSsoServices, MasterPrecedenceComparator);
        Logger.info("Available potential/ordered SSO services: " + Arrays.toString(orderedSsoServices.toArray()));
        return orderedSsoServices;
    }

    public List<SsoService> getOrderedSsoServices() {
        List<SsoService> ssoServices = getUntrustedOrderedSsoServices();
        Iterator<SsoService> it = ssoServices.iterator();
        while (it.hasNext()) {
            SsoService service = (SsoService) it.next();
            if (!this._signatureVerifier.isTrusted(service.getPackageName())) {
                Logger.warning("Disallowing SSO with " + service.getPackageName() + " because it is not trusted.");
                it.remove();
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.TotalTrustedSsoServices, String.valueOf(ssoServices.size()));
        Logger.info("Available trusted/ordered SSO services: " + Arrays.toString(ssoServices.toArray()));
        return ssoServices;
    }

    public boolean doesUntrustedPotentialMasterExist() {
        List<SsoService> ssoServices = getUntrustedOrderedSsoServices();
        if (ssoServices.size() < 1) {
            return false;
        }
        String masterPackageName = ((SsoService) ssoServices.get(0)).getPackageName();
        if (this._applicationContext.getPackageName().equals(masterPackageName)) {
            return false;
        }
        return !this._signatureVerifier.isTrusted(masterPackageName);
    }

    protected SsoService getSelfSsoService() {
        return new SsoService(this._applicationContext.getPackageName(), 0, BuildConfig.VERSION_NAME, 0);
    }

    public SsoService getSsoService(String packageName) {
        if (packageName != null) {
            for (SsoService ssoService : getOrderedSsoServices()) {
                if (ssoService.getPackageName().equalsIgnoreCase(packageName)) {
                    return ssoService;
                }
            }
        }
        return null;
    }

    protected long getFirstInstallTime(String packageName) throws NameNotFoundException {
        return this._applicationContext.getPackageManager().getPackageInfo(packageName, 0).firstInstallTime;
    }
}
