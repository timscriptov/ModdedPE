package com.microsoft.xbox.idp.interop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.internal.NativeProtocol;
import com.microsoft.appcenter.Constants;
import com.microsoft.cll.android.AndroidCll;
import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.cll.android.Verbosity;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.xbox.idp.jobs.DelegatedAuthJob;
import com.microsoft.xbox.idp.jobs.DelegatedAuthJob.Callbacks;
import com.microsoft.xbox.idp.jobs.JobSilentSignIn;
import com.microsoft.xbox.idp.jobs.MSAJob;
import com.microsoft.xbox.idp.model.gcm.RegistrationIntentService;
import com.microsoft.xbox.idp.services.Config;
import com.microsoft.xbox.idp.services.Endpoints.Type;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.ui.AuthFlowActivity;
import com.microsoft.xbox.idp.ui.AuthFlowActivity.StaticCallbacks;
import com.microsoft.xbox.idp.util.CacheUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Locale;

public class Interop {
    private static final String DNET_SCOPE = "user.auth.dnet.xboxlive.com";
    private static final String PACKAGE_NAME_TO_REMOVE = "com.microsoft.onlineid.sample";
    private static final String POLICY = "mbi_ssl";
    private static final String PROD_SCOPE = "user.auth.xboxlive.com";
    public static final String TAG = Interop.class.getSimpleName();
    private static final Callbacks brokeredSignInCallbacks = new Callbacks() {
        public void onUiNeeded(DelegatedAuthJob job) {
            Log.d(Interop.TAG, "DelegatedAuthJob UI Needed");
            Interop.MSACallback("", 0, MSAError.UI_INTERACTION_REQUIRED.id, "Must show UI to acquire an account.");
        }

        public void onFailure(DelegatedAuthJob job, Exception e) {
            Log.d(Interop.TAG, "DelegatedAuthJob Failure");
            Interop.MSACallback("", 0, MSAError.OTHER.id, "There was a problem acquiring an account: " + e);
        }

        public void onTicketAcquired(DelegatedAuthJob job, String ticket) {
            Log.d(Interop.TAG, "Ticket Acquired");
            Interop.MSACallback(ticket, 0, MSAError.NONE.id, "Got ticket");
        }
    };
    private static CllWrapper s_cll = null;
    private static final MSAJob.Callbacks silentSignInCallbacks = new MSAJob.Callbacks() {
        public void onUiNeeded(MSAJob job) {
            Log.d(Interop.TAG, "Java - onUiNeeded");
            Interop.MSACallback("", 0, MSAError.UI_INTERACTION_REQUIRED.id, "Must show UI to acquire an account.");
        }

        public void onFailure(MSAJob job, Exception e) {
            Log.d(Interop.TAG, "Java - onFailure");
            Interop.MSACallback("", 0, MSAError.OTHER.id, "There was a problem acquiring an account: " + e);
        }

        public void onUserCancel(MSAJob job) {
            Log.d(Interop.TAG, "Java - onUserCancel");
            Interop.MSACallback("", 0, MSAError.USER_CANCEL.id, "The user cancelled the UI to acquire a ticket.");
        }

        public void onSignedOut(MSAJob job) {
            Log.d(Interop.TAG, "Java - onSignedOut");
            Interop.MSACallback("", 0, MSAError.OTHER.id, "Signed out during silent sign in - should not be here");
        }

        public void onAccountAcquired(MSAJob job, UserAccount userAccount) {
            Log.d(Interop.TAG, "Java - Ticket Acquired");
        }

        public void onTicketAcquired(MSAJob job, Ticket ticket) {
            Log.d(Interop.TAG, "Java - Ticket Acquired");
            Interop.MSACallback(ticket.getValue(), 0, MSAError.NONE.id, "Got ticket");
        }
    };

    public enum AuthFlowScreenStatus {
        NO_ERROR(0),
        ERROR_USER_CANCEL(1),
        PROVIDER_ERROR(2);

        private final int id;

        private AuthFlowScreenStatus(int id2) {
            this.id = id2;
        }

        public int getId() {
            return this.id;
        }
    }

    public interface Callback extends ErrorCallback {
        void onXTokenAcquired(long j);
    }

    private static class CllWrapper {
        private final Context appContext;
        private final AndroidCll cll;

        public CllWrapper(AndroidCll cll2, Context appContext2) {
            this.cll = cll2;
            this.appContext = appContext2;
        }

        public AndroidCll getCll() {
            return this.cll;
        }

        public Context getAppContext() {
            return this.appContext;
        }
    }

    public interface ErrorCallback {
        void onError(int i, int i2, String str);
    }

    public enum ErrorStatus {
        TRY_AGAIN(0),
        CLOSE(1);

        private final int id;

        private ErrorStatus(int id2) {
            this.id = id2;
        }

        public int getId() {
            return this.id;
        }
    }

    public enum ErrorType {
        BAN(0),
        CREATION(1),
        OFFLINE(2),
        CATCHALL(3);

        private final int id;

        private ErrorType(int id2) {
            this.id = id2;
        }

        public int getId() {
            return this.id;
        }
    }

    public interface EventInitializationCallback extends ErrorCallback {
        void onSuccess();
    }

    public enum MSAError {
        NONE(0),
        UI_INTERACTION_REQUIRED(1),
        USER_CANCEL(2),
        OTHER(3);

        public final int id;

        private MSAError(int id2) {
            this.id = id2;
        }
    }

    public enum MSAPurpose {
        NONE(0),
        OPPORTUNISTIC_SIGN_IN(1),
        EXPLICIT_SIGN_IN(2),
        REACQUIRE_PREVIOUS_ACCOUNT(3),
        GET_TICKET(4),
        GET_VORTEX_TICKET(5),
        SIGN_OUT(6);

        public final int id;

        private MSAPurpose(int id2) {
            this.id = id2;
        }

        public static MSAPurpose fromId(int id2) {
            MSAPurpose[] values = values();
            if (id2 < 0 || values.length <= id2) {
                return null;
            }
            return values[id2];
        }
    }

    public interface XBLoginCallback extends ErrorCallback {
        void onLogin(long j, boolean z);
    }

    public interface XBLogoutCallback {
        void onLoggedOut();
    }

    public static native void auth_flow_callback(long j, int i, String str);

    public static native boolean deinitializeInterop();

    private static native void gamertag_updated_callback(String str);

    private static native String get_supporting_x_token_callback(String str);

    private static native String get_title_telemetry_device_id();

    private static native String get_title_telemetry_session_id();

    private static native String get_uploader_x_token_callback(boolean z);

    public static native boolean initializeInterop(Context context);

    private static native void invoke_event_initialization(long j, String str, EventInitializationCallback eventInitializationCallback);

    private static native void invoke_x_token_acquisition(long j, Callback callback);

    private static native void invoke_xb_login(long j, String str, XBLoginCallback xBLoginCallback);

    private static native void invoke_xb_logout(long j, XBLogoutCallback xBLogoutCallback);

    private static native void notification_registration_callback(String str, boolean z);

    private static native void sign_out_callback();

    private static native void ticket_callback(String str, int i, int i2, String str2);

    public static AndroidCll getCll() {
        return s_cll.getCll();
    }

    public static String getTitleDeviceId() {
        return get_title_telemetry_device_id();
    }

    public static String getTitleSessionId() {
        return get_title_telemetry_session_id();
    }

    public static String GetLiveXTokenCallback(boolean forceRefresh) {
        return get_uploader_x_token_callback(forceRefresh);
    }

    public static String GetXTokenCallback(String xuid) {
        return get_supporting_x_token_callback(xuid);
    }

    public static void LogCLL(String xuid, String eventName, String eventData) {
        Log.i("XSAPI.Android", "Log CLL");
        ArrayList<String> ids = new ArrayList<>();
        ids.add(xuid);
        if (s_cll == null) {
            Log.i("XSAPI.Android", "Log CLL null");
            return;
        }
        s_cll.getCll().log(eventName, eventData, Latency.LatencyRealtime, Persistence.PersistenceCritical, EnumSet.of(Sensitivity.SensitivityNone), 100.0d, ids);
    }

    public static void LogTelemetrySignIn(String api, String state) {
        Log.i("XSAPI.Android", "LogTelemetrySignIn");
        UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
        String name = api + state;
        UTCPageAction.track(name, name, model);
    }

    public static String getSystemProxy() {
        String proxyAddress = System.getProperty("http.proxyHost");
        if (proxyAddress != null) {
            String proxyPort = System.getProperty("http.proxyPort");
            if (proxyPort != null) {
                String fullProxy = "http://" + proxyAddress + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + proxyPort;
                Log.i(TAG, fullProxy);
                return fullProxy;
            }
        }
        return "";
    }

    public static String getLocale() {
        String locale = Locale.getDefault().toString();
        Log.i(TAG, "locale is: " + locale);
        return locale;
    }

    public static String ReadConfigFile(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(context.getResources().getIdentifier("xboxservices", "raw", context.getPackageName()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (true) {
            try {
                int len = inputStream.read(buf);
                if (len == -1) {
                    break;
                }
                outputStream.write(buf, 0, len);
            } catch (IOException e) {
            }
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toString();
    }

    public static String GetLocalStoragePath(Context context) {
        return context.getFilesDir().getPath();
    }

    public static void InitCLL(Context context, String iKey) {
        if (s_cll == null) {
            Log.i("XSAPI.Android", "Init CLL");
            s_cll = new CllWrapper(new AndroidCll(iKey, context), context.getApplicationContext());
            CLLCallback callback = new CLLCallback(context, null);
            AndroidCll cll = s_cll.getCll();
            cll.setXuidCallback(callback);
            cll.setDebugVerbosity(Verbosity.INFO);
            cll.start();
        }
    }

    public static Context getApplicationContext() {
        if (s_cll == null) {
            return null;
        }
        return s_cll.getAppContext();
    }

    public static void InvokeMSA(Context context, int requestCode, boolean isProd, String cid) {
        Log.i("XSAPI.Android", "Invoking MSA");
        if (!isProd) {
            Config.endpointType = Type.DNET;
        }
        MSAPurpose purpose = MSAPurpose.fromId(requestCode);
        if (purpose != null) {
            switch (purpose) {
                case OPPORTUNISTIC_SIGN_IN:
                    Log.i(TAG, "InvokeMSA OPPORTUNISTIC_SIGN_IN cid: " + cid);
                    if (TextUtils.isEmpty(cid)) {
                        MSACallback("", requestCode, MSAError.UI_INTERACTION_REQUIRED.id, "Must show UI to acquire an account.");
                        return;
                    } else {
                        new JobSilentSignIn(context, null, silentSignInCallbacks, isProd ? PROD_SCOPE : DNET_SCOPE, POLICY, cid).start();
                        return;
                    }
                case SIGN_OUT:
                    Log.i(TAG, "InvokeMSA SIGN_OUT");
                    CacheUtil.clearCaches();
                    sign_out_callback();
                    return;
                default:
                    MSACallback("", requestCode, MSAError.OTHER.id, "Invalid requestCode: " + requestCode);
                    return;
            }
        } else {
            MSACallback("", requestCode, MSAError.OTHER.id, "Invalid requestCode: " + requestCode);
        }
    }

    public static void InvokeBrokeredMSA(Context context, boolean isProd) {
        Log.d(TAG, "InvokeAuthFlow");
        if (!isProd) {
            Config.endpointType = Type.DNET;
        }
        new DelegatedAuthJob(context, brokeredSignInCallbacks).start();
    }

    public static void InvokeLatestIntent(Activity activity, Object intentObject) {
        Log.i(TAG, "InvokeLatestIntent");
        Intent launchIntent = DelegatedAuthJob.getXboxAppLaunchIntent();
        if (launchIntent == null) {
            Log.d(TAG, "Xbox App launch intent was null");
        } else if (intentObject instanceof Intent) {
            launchIntent.putExtra("com.microsoft.xbox.extra.RELAUNCH_INTENT", (Intent) intentObject);
            Log.d(TAG, "Invoking the launch intent...");
            String intentPackageName = launchIntent.getPackage();
            String intentAction = launchIntent.getAction();
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("packageName", intentPackageName);
            model.addValue(NativeProtocol.WEB_DIALOG_ACTION, intentAction);
            UTCPageAction.track("DeepLink - GearVR SignIn", "Minecraft GearVR SignIn", model);
            activity.startActivity(launchIntent);
        } else {
            Log.d(TAG, "Minecraft relaunch intent was null");
        }
    }

    public static void ClearIntent() {
        DelegatedAuthJob.clearXboxAppLaunchIntent();
    }

    public static void UpdateGamerTag(String newGamerTag) {
        gamertag_updated_callback(newGamerTag);
    }

    public static void MSACallback(String rpsTicket, int state, int errorCode, String errorMessage) {
        Log.i(TAG, "MSA Callback");
        ticket_callback(rpsTicket, state, errorCode, errorMessage);
    }

    public static void InvokeAuthFlow(long userPtr, Activity activity, boolean isProd, String signInText) {
        Log.d(TAG, "InvokeAuthFlow");
        if (!isProd) {
            Config.endpointType = Type.DNET;
        }
        AuthFlowActivity.setStaticCallbacks(new StaticCallbacks() {
            public void onAuthFlowFinished(long userPtr, AuthFlowScreenStatus authStatus, String cid) {
                AuthFlowActivity.setStaticCallbacks(null);
                Log.d(Interop.TAG, "onAuthFlowFinished: " + authStatus);
                CacheUtil.clearCaches();
                Interop.auth_flow_callback(userPtr, authStatus.getId(), cid);
            }
        });
        Intent intent = new Intent(activity, AuthFlowActivity.class);
        intent.putExtra("ARG_SECURITY_SCOPE", isProd ? PROD_SCOPE : DNET_SCOPE);
        intent.putExtra("ARG_SECURITY_POLICY", POLICY);
        intent.putExtra("ARG_USER_PTR", userPtr);
        if (!TextUtils.isEmpty(signInText)) {
            intent.putExtra("ARG_LOG_IN_BUTTON_TEXT", signInText);
        }
        activity.startActivity(intent);
    }

    public static void InvokeXBLogin(long userPtr, String rpsTicket, XBLoginCallback callback) {
        Log.d(TAG, "InvokeXBLogin");
        invoke_xb_login(userPtr, rpsTicket, callback);
    }

    public static void InvokeEventInitialization(long userPtr, String rpsTicket, EventInitializationCallback callback) {
        Log.d(TAG, "InvokeEventInitialization");
        invoke_event_initialization(userPtr, rpsTicket, callback);
    }

    public static void InvokeXBLogout(long userPtr, XBLogoutCallback callback) {
        Log.d(TAG, "InvokeSignOut");
        invoke_xb_logout(userPtr, callback);
    }

    public static void InvokeXTokenCallback(long userPtr, Callback callback) {
        Log.i(TAG, "InvokeXTokenCallback");
        invoke_x_token_acquisition(userPtr, callback);
    }

    public static void NotificationRegisterCallback(String regId, boolean isCached) {
        Log.i(TAG, "callback");
        try {
            notification_registration_callback(regId, isCached);
        } catch (UnsatisfiedLinkError e) {
            Log.i(TAG, "Token refreshed while process was not running");
        }
    }

    public static void RegisterWithGNS(Context context) {
        Log.i(TAG, "trying to register..");
        context.startService(new Intent(context, RegistrationIntentService.class));
    }
}
