package com.microsoft.xbox.idp.jobs;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.microsoft.xbox.authenticate.DelegateRPSTicketResult;
import com.microsoft.xbox.authenticate.IDelegateKeyService;
import com.microsoft.xbox.authenticate.IDelegateKeyService.Stub;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.util.XboxAppLinker;

public class DelegatedAuthJob {
    private static final int RESULT_INVALID_APPURI = 6;
    private static final int RESULT_INVALID_PACKAGE = 4;
    private static final int RESULT_INVALID_SIGNATURE = 5;
    private static final int RESULT_NOCID = 1;
    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_UNEXPECTED = 2;
    private static final int RESULT_UNKNOWN_PACKAGE = 3;
    public static final String TAG = DelegatedAuthJob.class.getSimpleName();
    public static Intent launchIntent = null;
    private final String XBOX_BROKER_SERVICE_NAME = "com.microsoft.xbox.authenticate.DelegateKeyService";
    public final Callbacks callbacks;
    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            String errorString;
            Log.i(DelegatedAuthJob.TAG, "Service connected");
            DelegatedAuthJob.this.keyService = Stub.asInterface(service);
            try {
                DelegateRPSTicketResult result = DelegatedAuthJob.this.keyService.requestDelegateRPSTicketSilently();
                if (XboxAppLinker.xboxAppIsInstalled(DelegatedAuthJob.this.context)) {
                    DelegatedAuthJob.launchIntent = XboxAppLinker.getXboxAppLaunchIntent(DelegatedAuthJob.this.context);
                    DelegatedAuthJob.launchIntent.setAction("com.microsoft.xbox.action.ACTION_SIGNIN");
                }
                int errorCode = result.getErrorCode();
                if (errorCode != 0) {
                    Log.i(DelegatedAuthJob.TAG, "Error getting RPS ticket");
                    if (errorCode == 1 || errorCode == 2) {
                        DelegatedAuthJob.this.callbacks.onUiNeeded(DelegatedAuthJob.this);
                        return;
                    }
                    switch (errorCode) {
                        case 3:
                            errorString = "RESULT_UNKNOWN_PACKAGE";
                            break;
                        case 4:
                            errorString = "RESULT_INVALID_PACKAGE";
                            break;
                        case 5:
                            errorString = "RESULT_INVALID_SIGNATURE";
                            break;
                        case 6:
                            errorString = "RESULT_INVALID_APPURI";
                            break;
                        default:
                            errorString = "UNKNOWN_ERROR";
                            break;
                    }
                    UTCError.trackFailure(DelegatedAuthJob.TAG, true, CallBackSources.Ticket, new Exception(errorString));
                    DelegatedAuthJob.this.callbacks.onFailure(DelegatedAuthJob.this, new Exception(errorString));
                    return;
                }
                DelegatedAuthJob.this.callbacks.onTicketAcquired(DelegatedAuthJob.this, result.getTicket());
            } catch (RemoteException e) {
                Log.i(DelegatedAuthJob.TAG, "Callback failure");
                e.printStackTrace();
                DelegatedAuthJob.this.callbacks.onFailure(DelegatedAuthJob.this, e);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i(DelegatedAuthJob.TAG, "Service disconnected");
        }
    };
    public final Context context;
    IDelegateKeyService keyService;
    private final String packageName;

    public interface Callbacks {
        void onFailure(DelegatedAuthJob delegatedAuthJob, Exception exc);

        void onTicketAcquired(DelegatedAuthJob delegatedAuthJob, String str);

        void onUiNeeded(DelegatedAuthJob delegatedAuthJob);
    }

    public DelegatedAuthJob(Context context2, Callbacks callbacks2) {
        this.context = context2;
        this.callbacks = callbacks2;
        this.packageName = context2.getPackageName();
    }

    public static Intent getXboxAppLaunchIntent() {
        return launchIntent;
    }

    public static void clearXboxAppLaunchIntent() {
        launchIntent = null;
    }

    public DelegatedAuthJob start() {
        launchXboxApp();
        return this;
    }

    @SuppressLint("WrongConstant")
    public void launchXboxApp() {
        Log.i(TAG, "check service exists");
        if (XboxAppLinker.isServiceInstalled(XboxAppLinker.XBOXAPP_BETA_PACKAGE, this.context, "com.microsoft.xbox.authenticate.DelegateKeyService")) {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("launchType", "BETA");
            UTCPageAction.track("SignIn - DelegateRPSTicket", "DelegatedAuthJob", model);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(XboxAppLinker.XBOXAPP_BETA_PACKAGE, "com.microsoft.xbox.authenticate.DelegateKeyService"));
            this.context.bindService(intent, this.connection, 1);
        } else if (XboxAppLinker.isServiceInstalled(XboxAppLinker.XBOXAPP_PACKAGE, this.context, "com.microsoft.xbox.authenticate.DelegateKeyService")) {
            UTCAdditionalInfoModel model2 = new UTCAdditionalInfoModel();
            model2.addValue("launchType", "RETAIL");
            UTCPageAction.track("SignIn - DelegateRPSTicket", "DelegatedAuthJob", model2);
            Intent intent2 = new Intent();
            intent2.setComponent(new ComponentName(XboxAppLinker.XBOXAPP_PACKAGE, "com.microsoft.xbox.authenticate.DelegateKeyService"));
            this.context.bindService(intent2, this.connection, 1);
        } else {
            UTCAdditionalInfoModel model3 = new UTCAdditionalInfoModel();
            model3.addValue("launchType", "STORE");
            UTCPageAction.track("SignIn - DelegateRPSTicket", "DelegatedAuthJob", model3);
            launchIntent = XboxAppLinker.getXboxAppInOculusMarketIntent(this.context);
            this.callbacks.onUiNeeded(this);
        }
    }
}
