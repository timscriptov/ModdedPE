package com.microsoft.onlineid.internal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.exception.PromptNeededException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.profile.ProfileManager;
import com.microsoft.onlineid.internal.sso.client.BackupService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;

public class MsaService extends IntentService {
    public static final String ActionGetTicket = "com.microsoft.onlineid.internal.GET_TICKET";
    public static final String ActionSignOut = "com.microsoft.onlineid.internal.SIGN_OUT";
    public static final String ActionSignOutAllApps = "com.microsoft.onlineid.internal.SIGN_OUT_ALL_APPS";
    public static final String ActionUpdateProfile = "com.microsoft.onlineid.internal.UPDATE_PROFILE";
    private ProfileManager _profileManager;
    private TicketManager _ticketManager;
    private TypedStorage _typedStorage;

    public MsaService() {
        super(MsaService.class.getName());
    }

    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        this._profileManager = new ProfileManager(context);
        this._ticketManager = new TicketManager(context);
        this._typedStorage = new TypedStorage(context);
    }

    protected void onHandleIntent(Intent intent) {
        ApiRequest request = new ApiRequest(getApplicationContext(), intent);
        String action = intent.getAction();
        try {
            String puid = request.getAccountPuid();
            if (ActionGetTicket.equals(action)) {
                request.sendSuccess(new ApiResult().setAccountPuid(puid).addTicket(this._ticketManager.getTicket(puid, request.getScope(), request.getClientPackageName(), request.getFlowToken(), false, request.getCobrandingId(), request.getIsWebFlowTelemetryRequested(), request.getClientStateBundle())));
            } else if (ActionUpdateProfile.equals(action)) {
                this._profileManager.updateProfile(request.getAccountPuid(), request.getFlowToken());
                request.sendSuccess(new ApiResult().setAccountPuid(puid));
            } else if (ActionSignOut.equals(action)) {
                request.sendSuccess(new ApiResult());
            } else if (ActionSignOutAllApps.equals(action)) {
                this._typedStorage.removeAccount(puid);
                BackupService.pushBackup(getApplicationContext());
                request.sendSuccess(new ApiResult());
            } else {
                throw new InternalException("Unknown action: " + action);
            }
        } catch (PromptNeededException ex) {
            Logger.info("ApiRequest with action " + action + " requires UI to complete.");
            request.sendUINeeded(new PendingIntentBuilder(ex.getRequest().setResultReceiver(request.getResultReceiver()).setIsSdkRequest(request.isSdkRequest()).setContinuation(request)).buildActivity());
        } catch (Exception ex2) {
            ClientAnalytics.get().logException(ex2);
            Logger.error("ApiRequest with action " + action + " failed.", ex2);
            request.sendFailure(ex2);
        }
    }
}
