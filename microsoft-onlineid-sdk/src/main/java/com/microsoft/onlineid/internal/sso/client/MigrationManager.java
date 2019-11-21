package com.microsoft.onlineid.internal.sso.client;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.client.request.RetrieveBackupRequest;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.sts.ConfigManager;
import java.util.Iterator;
import java.util.List;

public class MigrationManager {
    public static final String InitialSdkVersion = "0";
    private final String _appSdkVersion;
    private final Context _applicationContext;
    private final ConfigManager _configManager;
    private final ServiceFinder _serviceFinder;
    private List<SsoService> _ssoServices;
    private final TypedStorage _typedStorage;

    public MigrationManager(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._configManager = new ConfigManager(applicationContext);
        this._typedStorage = new TypedStorage(applicationContext);
        this._serviceFinder = new ServiceFinder(applicationContext);
        this._appSdkVersion = Resources.getSdkVersion(applicationContext);
    }

    public void migrateAndUpgradeStorageIfNeeded() {
        String storageSdkVersion = this._typedStorage.readSdkVersion();
        if (storageSdkVersion == null) {
            this._typedStorage.writeSdkVersion(InitialSdkVersion);
            this._ssoServices = this._serviceFinder.getOrderedSsoServices();
            if (!this._ssoServices.isEmpty()) {
                migrateStorage();
            }
        }
        if (storageSdkVersion == null || !storageSdkVersion.equals(this._appSdkVersion)) {
            upgradeStorage(storageSdkVersion, this._appSdkVersion);
            this._typedStorage.writeSdkVersion(this._appSdkVersion);
        }
    }

    protected void migrateStorage() {
        SsoService ssoService;
        String thisAppPackageName = this._applicationContext.getPackageName();
        int migrationAttempts = 0;
        boolean isConfigUpToDate = false;
        Iterator<SsoService> iterator = this._ssoServices.iterator();
        if (iterator.hasNext()) {
            ssoService = (SsoService) iterator.next();
        } else {
            ssoService = null;
        }
        while (ssoService != null) {
            String ssoServicePackageName = ssoService.getPackageName();
            if (!ssoServicePackageName.equals(thisAppPackageName)) {
                migrationAttempts++;
                try {
                    Bundle backup = (Bundle) createRetrieveBackupRequest(this._applicationContext).performRequest(ssoService);
                    if (!backup.isEmpty()) {
                        this._typedStorage.storeBackup(backup);
                        Logger.info(thisAppPackageName + " migrated backup data from " + ssoServicePackageName);
                        break;
                    }
                } catch (ClientConfigUpdateNeededException e) {
                    Logger.info("Migration attempt requires config update: " + e.getMessage());
                    if (isConfigUpToDate) {
                        Logger.info("Config update already ran during this migration attempt, ignoring service: " + ssoService);
                    } else {
                        if (this._configManager.update()) {
                            this._ssoServices = this._serviceFinder.getOrderedSsoServices();
                            iterator = this._ssoServices.iterator();
                        } else {
                            Logger.warning("Attempt to update config failed.");
                        }
                        isConfigUpToDate = true;
                    }
                } catch (Exception e2) {
                    Logger.error(thisAppPackageName + " encountered an error attempting to migrate storage from " + ssoServicePackageName, e2);
                    ClientAnalytics.get().logException(e2);
                }
            }
            if (iterator.hasNext()) {
                ssoService = (SsoService) iterator.next();
            } else {
                ssoService = null;
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.MigrationCategory, ClientAnalytics.MigrationAttempts, String.valueOf(migrationAttempts));
    }

    protected void upgradeStorage(String oldVersion, String newVersion) {
    }

    protected RetrieveBackupRequest createRetrieveBackupRequest(Context applicationContext) {
        return new RetrieveBackupRequest(applicationContext);
    }
}
