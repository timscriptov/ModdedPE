package com.microsoft.onlineid.internal.sso.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.SignatureVerifier;
import com.microsoft.onlineid.internal.sso.SsoServiceError;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import com.microsoft.onlineid.internal.sso.service.IMsaSsoService.Stub;
import com.microsoft.onlineid.internal.sso.service.operation.GetAccountByIdOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetAccountOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetAccountPickerOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetAllAccountsOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetSignInIntentOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetSignOutIntentOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetSignUpIntentOperation;
import com.microsoft.onlineid.internal.sso.service.operation.GetTicketOperation;
import com.microsoft.onlineid.internal.sso.service.operation.RetrieveBackupOperation;
import com.microsoft.onlineid.internal.sso.service.operation.ServiceOperation;
import com.microsoft.onlineid.internal.sso.service.operation.StoreBackupOperation;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.ConfigManager;

public class MsaSsoService extends Service {
    private AuthenticatorAccountManager _accountManager;
    private final Stub _binder = new Stub() {
        public Bundle getAccount(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetAccountOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getAccountById(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetAccountByIdOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getAllAccounts(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetAllAccountsOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getAccountPickerIntent(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetAccountPickerOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getSignInIntent(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetSignInIntentOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getSignUpIntent(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetSignUpIntentOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getSignOutIntent(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetSignOutIntentOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle getTicket(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new GetTicketOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager));
        }

        public Bundle storeBackup(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new StoreBackupOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager, MsaSsoService.this._typedStorage));
        }

        public Bundle retrieveBackup(Bundle params) throws RemoteException {
            return MsaSsoService.this.handleIncomingRequest(new RetrieveBackupOperation(MsaSsoService.this.getApplicationContext(), params, MsaSsoService.this._accountManager, MsaSsoService.this._ticketManager, MsaSsoService.this._typedStorage));
        }
    };
    private ConfigManager _configManager;
    private MigrationManager _migrationManager;
    private SignatureVerifier _signatureVerifier;
    private TicketManager _ticketManager;
    private TypedStorage _typedStorage;

    public void onCreate() {
        this._signatureVerifier = new SignatureVerifier(getApplicationContext());
        this._accountManager = new AuthenticatorAccountManager(getApplicationContext());
        this._ticketManager = new TicketManager(getApplicationContext());
        this._typedStorage = new TypedStorage(getApplicationContext());
        this._configManager = new ConfigManager(getApplicationContext());
        this._migrationManager = new MigrationManager(getApplicationContext());
    }

    protected Bundle handleIncomingRequest(ServiceOperation operation) {
        try {
            this._configManager.updateIfFirstDownloadNeeded();
            this._migrationManager.migrateAndUpgradeStorageIfNeeded();
            operation.verifyStandardArguments();
            if (operation.getCallerSsoVersion() <= 1) {
                return BundleMarshaller.errorToBundle(SsoServiceError.UnsupportedClientVersion, "Invalid SSO version.");
            }
            String claimedPackageName = operation.getCallingPackage();
            if (!this._signatureVerifier.isPackageInUid(Binder.getCallingUid(), claimedPackageName)) {
                return BundleMarshaller.errorToBundle(SsoServiceError.ClientNotAuthorized, "Invalid caller package name.");
            }
            String callerConfigVersion = operation.getCallerConfigVersion();
            if (!this._configManager.hasConfigBeenUpdatedRecently(operation.getCallerConfigLastDownloadedTime()) && this._configManager.isClientConfigVersionOlder(callerConfigVersion)) {
                return BundleMarshaller.errorToBundle(SsoServiceError.ClientConfigUpdateNeededException, "The caller must update config to version: " + this._configManager.getCurrentConfigVersion());
            }
            this._configManager.updateIfNeeded(callerConfigVersion);
            if (this._signatureVerifier.isTrusted(claimedPackageName)) {
                return operation.call();
            }
            return BundleMarshaller.errorToBundle(SsoServiceError.ClientNotAuthorized, "The caller is not authorized to invoke this service.");
        } catch (Exception e) {
            Logger.warning("SSO Service caught exception", e);
            return BundleMarshaller.exceptionToBundle(e);
        }
    }

    public IBinder onBind(Intent intent) {
        return this._binder;
    }
}
