package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.NativeActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings.Secure;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appsflyer.AppsFlyerLib;
import com.mcal.mcpelauncher.BuildConfig;
import com.mcal.mcpelauncher.services.SoundService;
import com.mojang.android.StringValue;
import com.mojang.minecraftpe.input.InputDeviceManager;
import com.mojang.minecraftpe.platforms.Platform;

import org.fmod.FMOD;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.asn1.cmp.PKIFailureInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MainActivity extends NativeActivity implements OnKeyListener, CrashManagerOwner {
    private static final String SESSION_HISTORY_SEP = "&";
    private static final String SESSION_HISTORY_KEY = "session-history";
    public static MainActivity mInstance = null;
    private static boolean _isPowerVr = false;
    private static boolean mHasStoragePermission = false;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private final DateFormat DateFormat = new SimpleDateFormat();
    public int mLastPermissionRequestReason;
    public int virtualKeyboardHeight = 0;
    protected DisplayMetrics displayMetrics;
    HeadsetConnectionReceiver headsetConnectionReceiver;
    List<ActivityListener> mActivityListeners = new ArrayList<ActivityListener>();
    MessageConnectionStatus mBound = MessageConnectionStatus.NOTSET;
    MemoryInfo mCachedMemoryInfo = new MemoryInfo();
    long mCachedMemoryInfoUpdateTime = 0;
    long mCachedUsedMemory = 0;
    long mCachedUsedMemoryUpdateTime = 0;
    Messenger mService = null;
    Platform platform;
    TextInputProxyEditTextbox textInputWidget;
    private boolean _fromOnCreate = false;
    private int _userInputStatus = -1;
    private String[] _userInputText = null;
    private ClipboardManager clipboardManager;
    private Locale initialUserLocale;
    private long mCallback = 0;
    private SessionInfo mLastDeviceSessionInfo = null;
    private InputDeviceManager deviceManager;
    private SessionInfo mCurrentSession = null;
    private ArrayList<SessionInfo> mSessionHistory = null;
    private CrashManager mCrashManager = null;
    private AlertDialog mDialog;
    private ArrayList<StringValue> _userInputValues = new ArrayList<>();

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = MessageConnectionStatus.CONNECTED;
            Message msg = Message.obtain(null, 672, 0, 0);
            msg.replyTo = mMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
            mBound = MessageConnectionStatus.DISCONNECTED;
        }
    };
    /**********************************
     * Bg music                       *
     **********************************/
    private ServiceConnection sc;
    private boolean bound, paused;
    private SoundService ss;
    /**********************************
     * Bg music                       *
     **********************************/
    private long mFileDialogCallback = 0;
    private HardwareInformation mHardwareInformation;
    private String mLastDeviceSessionId = "";
    private TextToSpeech textToSpeechManager;

    public static boolean isXperiaPlay() {
        String[] tags = {Build.MODEL, Build.DEVICE, Build.PRODUCT};
        for (String tag : tags) {
            tag.toLowerCase(Locale.ENGLISH);
            if (tag.contains("r800") || tag.contains("so-01d") || (tag.contains("xperia") && tag.contains("play"))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPowerVR() {
        return _isPowerVr;
    }

    public static void saveScreenshot(String filename, int w, int h, int[] pixels) {
        Bitmap bitmap = Bitmap.createBitmap(pixels, w, h, Config.ARGB_8888);
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            bitmap.compress(CompressFormat.JPEG, 85, fos);
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (FileNotFoundException e3) {
            System.err.println("Couldn't create file: " + filename);
            e3.printStackTrace();
        }
    }

    private static void copyFile(@NotNull InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        int len = in.read(buff);
        while (len != -1) {
            out.write(buff, 0, len);
            len = in.read(buff);
        }
        in.close();
        out.close();
    }

    private static void copyAssetDir(AssetManager am, String outpath) {
        Log.w("ModdedPE", "EXTRACTING: " + "mono");
        try {
            String[] res = am.list("mono");
            int length = res.length;
            for (String result : res) {
                String fromFile = "mono" + "/" + result;
                String toFile = outpath + "/" + result;
                if (fromFile.endsWith(".dll")) {
                    Log.w("ModdedPE", "\tCOPYING " + fromFile + " to " + toFile);
                    copyFile(am.open(fromFile), new FileOutputStream(toFile));
                } else {
                    Log.w("ModdedPE", "\t" + fromFile + " is not a dll, skipping");
                }
            }
        } catch (Exception e) {
            Log.w("ModdedPE", "DLL copy failed: ", e);
        }
    }

    public SessionInfo getLastDeviceSessionInfo() {
        if (mLastDeviceSessionInfo == null) {
            mLastDeviceSessionInfo = SessionInfo.fromString(PreferenceManager.getDefaultSharedPreferences(this).getString("last-session-info", ""));
            Log.i("ModdedPE", "getLastDeviceSessionInfo was null and now: " + mLastDeviceSessionInfo.toString());
        } else {
            Log.i("ModdedPE", "getLastDeviceSessionInfo was not null with: " + mLastDeviceSessionInfo.toString());
        }
        return mLastDeviceSessionInfo;
    }

    public void setLastDeviceSessionInfo(@NotNull SessionInfo info) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("last-session-info", info.toString());
        edit.apply();
        Log.i("MCPE", "setLastDeviceSessionInfo: " + info.toString());
        this.mLastDeviceSessionInfo = info;
    }

    public boolean supportsNonTouchscreen() {
        return isXperiaPlay();
    }

    private native void fireCrashedTelemetry(String str, String str2, String str3);

    private static native void nativeConfigureNewSession(SessionInfo sessionInfo);

    private static native void nativeWaitCrashManagementSetupComplete();

    private native void setUpBreakpad(String str, String str2);

    public native boolean isAndroidTrial();

    public native boolean isBrazeEnabled();

    public native boolean isEduMode();

    public native boolean isPublishBuild();

    public native boolean isTestInfrastructureDisabled();

    public native void nativeBackPressed();

    public native void nativeBackSpacePressed();

    public native String nativeCheckIfTestsAreFinished();

    public native void nativeClearAButtonState();

    public native void nativeDeviceCorrelation(long j, String str, long j2, String str2);

    public native String nativeGetActiveScreen();

    public native String nativeGetDevConsoleLogName();

    public native String nativeGetDeviceId();

    public native String nativeGetLogText(String str);

    public native void nativeInitializeXboxLive(long j, long j2);

    public native boolean nativeKeyHandler(int i, int i2);

    public native void nativeOnDestroy();

    public native void nativeOnPickImageCanceled(long j);

    public native void nativeOnPickImageSuccess(long j, String str);

    public native void nativeProcessIntentUriQuery(String str, String str2);

    public native void nativeResize(int i, int i2);

    public native void nativeReturnKeyPressed();

    public native void nativeSetHeadphonesConnected(boolean z);

    public native String nativeSetOptions(String str);

    public native void nativeSetTextboxText(String str);

    public native void nativeShutdown();

    public native void nativeStopThis();

    public native void nativeStoragePermissionRequestResult(boolean z, int i);

    public native void nativeSuspend();

    public void launchUri(String uri) {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(uri)));
    }

    public void share(String title, String description, String uri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.SUBJECT", title);
        sendIntent.putExtra("android.intent.extra.TITLE", description);
        sendIntent.putExtra("android.intent.extra.TEXT", uri);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, title));
    }

    public void setClipboard(String value) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("MCPE-Clipdata", value));
    }

    public float getKeyboardHeight() {
        return (float) virtualKeyboardHeight;
    }

    public void trackPurchaseEvent(String contentId, String contentType, String revenue, String clientId, String userId, String playerSessionId, String currencyCode, String eventName) {
        Map<String, Object> eventValue = new HashMap<>();
        eventValue.put("player_session_id", playerSessionId);
        eventValue.put("client_id", clientId);
        eventValue.put("af_revenue", revenue);
        eventValue.put("af_content_type", contentType);
        eventValue.put("af_content_id", contentId);
        eventValue.put("af_currency", currencyCode);
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), eventName, eventValue);
    }

    public void sendBrazeEvent(String eventName) {
    }

    public void sendBrazeEventWithProperty(String eventName, String propertyName, int propertyValue) {
    }

    public void sendBrazeEventWithStringProperty(String eventName, String propertyName, String propertyValue) {
    }

    public void sendBrazeToastClick() {
    }

    public void sendBrazeDialogButtonClick(int buttonNumber) {
    }

    public String getCachedDeviceId() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("deviceId", "");
    }

    public void setCachedDeviceId(String deviceId) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("deviceId", deviceId);
        edit.apply();
    }

    public String getLastDeviceSessionId() {
        if (mLastDeviceSessionId.equals("")) {
            mLastDeviceSessionId = PreferenceManager.getDefaultSharedPreferences(this).getString("LastDeviceSessionId", "");
        }
        return mLastDeviceSessionId;
    }

    public void setLastDeviceSessionId(String currentDeviceSessionId) {
        if (mLastDeviceSessionId.equals("")) {
            getLastDeviceSessionId();
        }
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("LastDeviceSessionId", currentDeviceSessionId);
        edit.apply();
    }

    @SuppressLint("WrongConstant")
    public void deviceIdCorrelationStart() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int attempts = prefs.getInt("correlationAttempts", 10);
        if (attempts != 0) {
            Intent i = new Intent();
            //i.setComponent(new ComponentName(getPackageName().contains("trial") ? "com.mojang.minecraftpe" : "com.mojang.minecrafttrialpe", "com.mojang.minecraftpe.ImportService"));
            i.setComponent(new ComponentName(getPackageName().contains("trial") ? "com.mojang.minecraftpe" : "com.mojang.minecraftpe", "com.mojang.minecraftpe.ImportService"));
            bindService(i, mConnection, 1);
            Editor edit = prefs.edit();
            edit.putInt("correlationAttempts", attempts - 1);
            edit.apply();
        }
    }

    public HardwareInformation getHardwareInfo() {
        if (mHardwareInformation == null) {
            mHardwareInformation = new HardwareInformation(this);
        }
        return mHardwareInformation;
    }

    public void initializeCrashManager() {
        AppConstants.loadFromContext(getApplicationContext());
        SessionInfo sessionInfo = new SessionInfo();
        mCurrentSession = sessionInfo;
        nativeConfigureNewSession(sessionInfo);
        mCurrentSession.updateJavaConstants(this);
        loadSessionHistory();
        saveNewSession(mCurrentSession);
        File file = new File(getFilesDir(), "/minidumps");
        file.mkdir();
        Log.v("MinecraftPlatform", "Minidump directory is: " + file.getAbsolutePath());
        Log.i("MinecraftPlatform", "Setting up crash handler");
        CrashManager crashManager = new CrashManager((CrashManagerOwner) this, file.getAbsolutePath(), getCachedDeviceId(), isAndroidTrial() ? new SentryEndpointConfig("https://sentry.io", "2308440", "668bc09f7bcf461796ea07c1006076fe") : new SentryEndpointConfig("https://sentry.io", "2277697", "1c3f5cbd723a4a84879059d260b19ef6"), mCurrentSession);
        mCrashManager = crashManager;
        crashManager.installGlobalExceptionHandler();
        setUpBreakpad(file.getAbsolutePath(), mCurrentSession.sessionId);
    }

    private void loadSessionHistory() {
        mSessionHistory = new ArrayList<>();
        String string = PreferenceManager.getDefaultSharedPreferences(this).getString(SESSION_HISTORY_KEY, "");
        if (string.length() > 0) {
            for (String fromString : string.split(SESSION_HISTORY_SEP)) {
                try {
                    mSessionHistory.add(SessionInfo.fromString(fromString));
                } catch (IllegalArgumentException e) {
                    Log.i("ModdedPE", "loadSessionHistory: failed to decode session history item: " + e.toString());
                }
            }
            Log.i("ModdedPE", "loadSessionHistory: decoded " + mSessionHistory.size() + " items");
            return;
        }
        Log.i("ModdedPE", "loadSessionHistory: no history found");
    }

    private void saveSessionHistory() {
        ArrayList arrayList = new ArrayList();
        for (SessionInfo sessionInfo : mSessionHistory) {
            arrayList.add(sessionInfo.toString());
        }
        String join = TextUtils.join(SESSION_HISTORY_SEP, arrayList);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString(SESSION_HISTORY_KEY, join);
        edit.apply();
        Log.i("ModdedPE", "saveSessionHistory: " + mSessionHistory.size() + " entries saved");
    }

    private void saveNewSession(SessionInfo sessionInfo) {
        mSessionHistory.add(sessionInfo);
        for (int size = mSessionHistory.size() - 20; size > 0; size--) {
            mSessionHistory.remove(0);
        }
        saveSessionHistory();
    }

    public SessionInfo findSessionInfoForCrash(CrashManager crashManager, String str) {
        for (int size = mSessionHistory.size() - 1; size >= 0; size--) {
            if (mSessionHistory.get(size).sessionId.equals(str)) {
                return mSessionHistory.get(size);
            }
        }
        return null;
    }

    @Override
    public String getCachedDeviceId(CrashManager crashManager) {
        return getCachedDeviceId();
    }


    @Override
    public void notifyCrashUploadCompleted(CrashManager crashManager, @NotNull SessionInfo sessionInfo) {
        fireCrashedTelemetry(sessionInfo.sessionId, sessionInfo.buildId, CrashManager.formatTimestamp(sessionInfo.crashTimestamp));
    }

    @SuppressLint({"WrongConstant", "ResourceType"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeWaitCrashManagementSetupComplete();
        //displayMetrics = new DisplayMetrics();
        platform = Platform.createPlatform(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        FMOD.init(this);
        deviceManager = InputDeviceManager.create(this);
        platform.onAppStart(getWindow().getDecorView());
        mHasStoragePermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
        headsetConnectionReceiver = new HeadsetConnectionReceiver();

        nativeSetHeadphonesConnected(((AudioManager) getSystemService("audio")).isWiredHeadsetOn());
        clipboardManager = (ClipboardManager) getSystemService("clipboard");
        initialUserLocale = Locale.getDefault();
        AppConstants.loadFromContext(getApplicationContext());
        mInstance = this;
        _fromOnCreate = true;
        textInputWidget = createTextWidget();
        findViewById(16908290).getRootView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> nativeResize(right - left, bottom - top));
        /**********************************
         * Bg music                       *
         **********************************/
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("background_music", false)) {
            sc = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName p1, IBinder p2) {
                    bound = true;
                    ss = ((SoundService.SoundBinder) p2).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName p1) {
                    bound = false;
                }
            };
            bindService(new Intent(getApplicationContext(), SoundService.class), sc, BIND_AUTO_CREATE);
        }
        /**********************************
         * Bg music                        *
         **********************************/
    }


    private void createAlertDialog(boolean z, boolean z2, boolean z3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        if (z3) {
            builder.setCancelable(false);
        }
        builder.setOnCancelListener(dialogInterface -> onDialogCanceled());
        if (z) {
            builder.setPositiveButton("Ok", (dialogInterface, i) -> onDialogCompleted());
        }
        if (z2) {
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> onDialogCanceled());
        }
        AlertDialog create = builder.create();
        mDialog = create;
        create.setOwnerActivity(this);
    }

    public void onDialogCanceled() {
        _userInputStatus = 0;
    }

    @SuppressLint("WrongConstant")
    public void onDialogCompleted() {
        int size = _userInputValues.size();
        _userInputText = new String[size];
        for (int i = 0; i < size; i++) {
            _userInputText[i] = _userInputValues.get(i).getStringValue();
        }
        for (String str : _userInputText) {
            PrintStream printStream = System.out;
            printStream.println("js: " + str);
        }
        _userInputStatus = 1;
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(getCurrentFocus(), 1);
    }

    public void throwRuntimeExceptionFromNative(final String str) {
        new Handler(getMainLooper()).post(() -> {
            throw new RuntimeException(str);
        });
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            String extraCmd = intent.getStringExtra("intent_cmd");
            if (extraCmd == null || extraCmd.length() <= 0) {
                String action = intent.getAction();
                String type = intent.getType();
                if ("xbox_live_game_invite".equals(action)) {
                    String json = intent.getStringExtra("xbl");
                    Log.d("ModdedPE", "[XboxLive] Received Invite " + json);
                    nativeProcessIntentUriQuery(action, json);
                } else if ("android.intent.action.VIEW".equals(action) || "org.chromium.arc.intent.action.VIEW".equals(action)) {
                    String scheme = intent.getScheme();
                    Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    if ("minecraft".equalsIgnoreCase(scheme) || "minecraftedu".equalsIgnoreCase(scheme)) {
                        String host = uri.getHost();
                        String query = uri.getQuery();
                        if (host != null || query != null) {
                            nativeProcessIntentUriQuery(host, query);
                        }
                    } else if ("file".equalsIgnoreCase(scheme)) {
                        nativeProcessIntentUriQuery("fileIntent", uri.getPath() + "&" + uri.getPath());
                    } else if ("content".equalsIgnoreCase(scheme)) {
                        File file = new File(getApplicationContext().getCacheDir() + com.appsflyer.share.Constants.URL_PATH_DELIMITER + new File(uri.getPath()).getName());
                        try {
                            InputStream input = getContentResolver().openInputStream(uri);
                            try {
                                OutputStream output = new FileOutputStream(file);
                                byte[] tmp = new byte[1048576];
                                while (true) {
                                    int size = input.read(tmp);
                                    if (size != -1) {
                                        output.write(tmp, 0, size);
                                    } else {
                                        output.close();
                                        nativeProcessIntentUriQuery("contentIntent", uri.getPath() + "&" + file.getAbsolutePath());
                                        try {
                                            input.close();
                                            return;
                                        } catch (IOException ioe2) {
                                            Log.e("ModdedPE", "IOException while closing input stream\n" + ioe2.toString());
                                            return;
                                        }
                                    }
                                }
                            } catch (IOException ioe) {
                                Log.e("ModdedPE", "IOException while copying file from content intent\n" + ioe.toString());
                                try {
                                    file.delete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    input.close();
                                } catch (IOException ioe22) {
                                    Log.e("ModdedPE", "IOException while closing input stream\n" + ioe22.toString());
                                }
                            } catch (Throwable th) {
                                try {
                                    input.close();
                                } catch (IOException ioe23) {
                                    Log.e("ModdedPE", "IOException while closing input stream\n" + ioe23.toString());
                                }
                                throw th;
                            }
                        } catch (IOException ioe3) {
                            Log.e("ModdedPE", "IOException while opening file from content intent\n" + ioe3.toString());
                        }
                    }
                }
            } else {
                try {
                    JSONObject json2 = new JSONObject(extraCmd);
                    String command = json2.getString("Command");
                    if (command.equals("keyboardResult")) {
                        nativeSetTextboxText(json2.getString("Text"));
                    } else if (command.equals("fileDialogResult") && mFileDialogCallback != 0) {
                        if (json2.getString("Result").equals("Ok")) {
                            nativeOnPickImageSuccess(mFileDialogCallback, json2.getString("Path"));
                        } else {
                            nativeOnPickImageCanceled(mFileDialogCallback);
                        }
                        mFileDialogCallback = 0;
                    }
                } catch (JSONException e2) {
                    Log.d("ModdedPE", "JSONObject exception:" + e2.toString());
                }
            }
        }
    }


    public boolean dispatchKeyEvent(@NotNull KeyEvent event) {
        if (nativeKeyHandler(event.getKeyCode(), event.getAction())) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 25 || keyCode == 24) {
            platform.onVolumePressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setTextToSpeechEnabled(boolean enabled) {
        if (!enabled) {
            textToSpeechManager = null;
        } else if (textToSpeechManager == null) {
            try {
                textToSpeechManager = new TextToSpeech(getApplicationContext(), status -> {
                });
            } catch (Exception ignored) {
            }
        }
    }

    public void requestStoragePermission(int permissionReason) {
        String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
        mLastPermissionRequestReason = permissionReason;
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == 0) {
                mHasStoragePermission = true;
            } else {
                mHasStoragePermission = false;
            }
            nativeStoragePermissionRequestResult(mHasStoragePermission, mLastPermissionRequestReason);
        }
    }

    public boolean hasWriteExternalStoragePermission() {
        mHasStoragePermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
        return mHasStoragePermission;
    }

    public boolean hasHardwareKeyboard() {
        return getResources().getConfiguration().keyboard == 2;
    }

    public boolean isMixerCreateInstalled() {
        return isPackageInstalledByName("com.microsoft.beambroadcast") || isPackageInstalledByName("com.microsoft.beambroadcast.beta");
    }

    private boolean isPackageInstalledByName(String str) {
        try {
            return getPackageManager().getPackageInfo(str, 0) != null;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    @SuppressLint("LongLogTag")
    public void navigateToPlaystoreForMixerCreate() {
        launchUri("market://details?id=com.microsoft.beambroadcast");
        Log.w("ModdedPE", "Application context is null");
    }

    @SuppressLint("LongLogTag")
    public boolean launchMixerCreateForBroadcast() {
        try {
            launchUri("beambroadcast://");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("ModdedPE", "Application context is null");
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    public void setupKeyboardViews(String text, int maxLength, boolean limitInput, boolean numbersOnly, boolean isMultiline) {
        if (textInputWidget == null) {
            textInputWidget = createTextWidget();
        }
        textInputWidget.updateFilters(maxLength, !isMultiline);
        textInputWidget.setTextFromGame(text);
        textInputWidget.setVisibility(0);
        textInputWidget.setInputType(isMultiline ? 131072 : 524288);
        if (numbersOnly) {
            textInputWidget.setInputType(textInputWidget.getInputType() | 2);
        } else {
            textInputWidget.setInputType(textInputWidget.getInputType() | 1);
        }
        textInputWidget.requestFocus();
        getInputMethodManager().showSoftInput(textInputWidget, 0);
        textInputWidget.setSelection(textInputWidget.length());
    }

    @SuppressLint("ResourceType")
    public TextInputProxyEditTextbox createTextWidget() {
        final TextInputProxyEditTextbox textWidget = new TextInputProxyEditTextbox(this);
        textWidget.setVisibility(8);
        textWidget.setFocusable(true);
        textWidget.setFocusableInTouchMode(true);
        textWidget.setImeOptions(268435461);
        textWidget.setOnEditorActionListener((v, actionId, event) -> {
            boolean isVirtualEnter;
            boolean isHardwareEnter;
            boolean isMultiline = true;
            Log.w("ModdedPE", "onEditorAction: " + actionId);
            if (actionId == 5) {
                isVirtualEnter = true;
            } else {
                isVirtualEnter = false;
            }
            if (actionId == 0 && event != null && event.getAction() == 0) {
                isHardwareEnter = true;
            } else {
                isHardwareEnter = false;
            }
            if (isVirtualEnter || isHardwareEnter) {
                if (isVirtualEnter) {
                    nativeReturnKeyPressed();
                }
                String curText = textWidget.getText().toString();
                int curSelect = textWidget.getSelectionEnd();
                if (curSelect < 0 || curSelect > curText.length()) {
                    curSelect = curText.length();
                }
                if ((131072 & textWidget.getInputType()) == 0) {
                    isMultiline = false;
                }
                if (isMultiline) {
                    textWidget.setText(curText.substring(0, curSelect) + "\n" + curText.substring(curSelect, curText.length()));
                    textWidget.setSelection(Math.min(curSelect + 1, textWidget.getText().length()));
                }
                return true;
            } else if (actionId != 7) {
                return false;
            } else {
                nativeBackPressed();
                return true;
            }
        });
        textWidget.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                String textBoxText = s.toString();
                if (textWidget == null) {
                    nativeSetTextboxText(textBoxText);
                } else if (textWidget.shouldSendText()) {
                    nativeSetTextboxText(textBoxText);
                    textWidget.updateLastSentText();
                }
            }
        });
        textWidget.setOnMCPEKeyWatcher(new TextInputProxyEditTextbox.MCPEKeyWatcher() {
            public void onDeleteKeyPressed() {
                MainActivity.this.runOnUiThread(() -> nativeBackSpacePressed());
            }

            public boolean onBackKeyPressed() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.w("mcpe - keyboard", "textInputWidget.onBackPressed");
                        nativeBackPressed();
                    }
                });
                return true;
            }
        });
        ((ViewGroup) findViewById(16908290)).addView(textWidget, new ViewGroup.LayoutParams(320, 50));
        final View activityRootView = findViewById(16908290).getRootView();
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            activityRootView.getWindowVisibleDisplayFrame(r);
            virtualKeyboardHeight = activityRootView.getRootView().getHeight() - r.height();
        });
        return textWidget;
    }

    public void updateLocalization(String lang, String region) {
        final String langString = lang;
        final String regionString = region;
        runOnUiThread(() -> {
            Locale locale = new Locale(langString, regionString);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        });
    }

    public void showKeyboard(String text, int maxLength, boolean limitInput, boolean numbersOnly, boolean isMultiline) {
        final String startText = text;
        final int fMaxLength = maxLength;
        final boolean fLimitInput = limitInput;
        final boolean fNumbersOnly = numbersOnly;
        final boolean fIsMultiline = isMultiline;
        //Не поддерживается в Minecraft 1.11.4.2
        //nativeClearAButtonState();
        nativeClearAButtonState();
        runOnUiThread(new Runnable() {
            public void run() {
                setupKeyboardViews(startText, fMaxLength, fLimitInput, fNumbersOnly, fIsMultiline);
            }
        });
    }

    public void hideKeyboard() {
        runOnUiThread(() -> dismissTextWidget());
    }

    @SuppressLint("WrongConstant")
    public boolean isTextWidgetActive() {
        return textInputWidget != null && textInputWidget.getVisibility() == 0;
    }

    @SuppressLint("WrongConstant")
    public void dismissTextWidget() {
        if (isTextWidgetActive()) {
            getInputMethodManager().hideSoftInputFromWindow(textInputWidget.getWindowToken(), 0);
            textInputWidget.setInputType(524288);
            textInputWidget.setVisibility(8);
        }
    }

    public void updateTextboxText(String newText) {
        final String setText = newText;
        runOnUiThread(() -> {
            if (isTextWidgetActive()) {
                textInputWidget.setTextFromGame(setText);
                textInputWidget.setSelection(textInputWidget.length());
            }
        });
    }

    public int getCursorPosition() {
        if (isTextWidgetActive()) {
            return textInputWidget.getSelectionStart();
        }
        return -1;
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public void onBackPressed() {
    }

    @SuppressLint("WrongConstant")
    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getSystemService("input_method");
    }

    public void setIsPowerVR(boolean status) {
        _isPowerVr = status;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        platform.onViewFocusChanged(hasFocus);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public int getKeyFromKeyCode(int keyCode, int metaState, int deviceId) {
        if (deviceId < 0) {
            int[] ids = InputDevice.getDeviceIds();
            if (ids.length == 0) {
                return 0;
            }
            deviceId = ids[0];
        }
        InputDevice device = InputDevice.getDevice(deviceId);
        if (device != null) {
            return device.getKeyCharacterMap().get(keyCode, metaState);
        }
        return 0;
    }

    public boolean unpackMonoAssemblies() {
        try {
            Context context = getApplicationContext();
            String filesDir = context.getFilesDir().getAbsolutePath();
            Log.w("ModdedPE", "copy all DLLs to \"" + filesDir + "\"");
            new File(filesDir).mkdir();
            copyAssetDir(context.getAssets(), filesDir);
            Log.w("ModdedPE", "unpacking success :-)");
            return true;
        } catch (Exception e) {
            Log.e("ModdedPE", "unpacking failed :-(");
            return false;
        }
    }

    public byte[] getFileDataBytes(@NotNull String filename) {
        BufferedInputStream bis;
        if (filename.isEmpty()) {
            return null;
        }
        try {
            AssetManager assets = getApplicationContext().getAssets();
            if (assets == null) {
                System.err.println("getAssets returned null: Could not getFileDataBytes " + filename);
                return null;
            }
            try {
                bis = new BufferedInputStream(assets.open(filename));
            } catch (IOException e) {
                new File(filename);
                try {
                    bis = new BufferedInputStream(new FileInputStream(filename));
                } catch (IOException e2) {
                    return null;
                }
            }
            ByteArrayOutputStream s = new ByteArrayOutputStream(1048576);
            byte[] tmp = new byte[1048576];
            while (true) {
                try {
                    int count = bis.read(tmp);
                    if (count <= 0) {
                        try {
                            bis.close();
                            break;
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    } else {
                        s.write(tmp, 0, count);
                    }
                } catch (IOException e4) {
                    System.err.println("Cannot read from file " + filename);
                    try {
                        bis.close();
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                    try {
                        bis.close();
                    } catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
            }
            return s.toByteArray();
        } catch (NullPointerException e7) {
            e7.printStackTrace();
            System.err.println("getAssets threw NPE: Could not getFileDataBytes " + filename);
            return null;
        }
    }

    public int[] getImageData(String filename) {
        Bitmap bm = BitmapFactory.decodeFile(filename);
        if (bm == null) {
            try {
                AssetManager assets = getApplicationContext().getAssets();
                if (assets != null) {
                    try {
                        bm = BitmapFactory.decodeStream(assets.open(filename));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("getImageData: Could not open image " + filename);
                        return null;
                    }
                } else {
                    System.err.println("getAssets returned null: Could not open image " + filename);
                    return null;
                }
            } catch (NullPointerException e2) {
                e2.printStackTrace();
                System.err.println("getAssets threw NPE: Could not open image " + filename);
                return null;
            }
        }
        int w = bm.getWidth();
        int h = bm.getHeight();
        int[] pixels = new int[((w * h) + 2)];
        pixels[0] = w;
        pixels[1] = h;
        bm.getPixels(pixels, 2, w, 0, 0, w, h);
        return pixels;
    }

    public int getScreenWidth() {
        @SuppressLint("WrongConstant")
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int out = Math.max(display.getWidth(), display.getHeight());
        System.out.println("getwidth: " + out);
        return out;
    }

    public int getScreenHeight() {
        @SuppressLint("WrongConstant")
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int out = Math.min(display.getWidth(), display.getHeight());
        System.out.println("getheight: " + out);
        return out;
    }

    public int getAndroidVersion() {
        return VERSION.SDK_INT;
    }

    public String getDeviceModel() {
        return HardwareInformation.getDeviceModelName();
    }

    public String getLocale() {
        Locale locale = getResources().getConfiguration().locale;
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    public String getObbDirPath() {
        return getApplicationContext().getObbDir().getAbsolutePath();
    }

    public String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public float getPixelsPerMillimeter() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return ((metrics.xdpi + metrics.ydpi) * 0.5f) / 25.4f;
    }

    public int checkLicense() {
        return 0;
    }

    public boolean hasBuyButtonWhenInvalidLicense() {
        return false;
    }

    public void postScreenshotToFacebook(String filename, int w, int h, int[] pixels) {
    }

    public void quit() {
        runOnUiThread(this::finish);
    }

    public String getFormattedDateString(int s) {
        return java.text.DateFormat.getDateInstance(3, initialUserLocale).format(new Date(((long) s) * 1000));
    }

    @SuppressLint("SimpleDateFormat")
    public String getFileTimestamp(int s) {
        return new SimpleDateFormat("__EEE__yyyy_MM_dd__HH_mm_ss'.txt'").format(new Date(((long) s) * 1000));
    }

    public String createDeviceID() {
        @SuppressLint("HardwareIds") String androidId = Secure.getString(getContentResolver(), "android_id");
        if (androidId != null && !androidId.isEmpty()) {
            return androidId;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String snooperID = prefs.getString("snooperId", "");
        if (snooperID.isEmpty()) {
            snooperID = createUUID();
            Editor edit = prefs.edit();
            edit.putString("snooperId", snooperID);
            edit.apply();
        }
        return snooperID;
    }

    public void displayDialog(int dialogId) {
    }

    public void tick() {
    }

    public void buyGame() {
    }

    public String getSecureStorageKey(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(key, "");
    }

    public void setSecureStorageKey(String key, String value) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getPlatformStringVar(int id) {
        if (id == 0) {
            return Build.MODEL;
        }
        return null;
    }

    public boolean isNetworkEnabled(boolean onlyWifiAllowed) {
        @SuppressLint("WrongConstant")
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        NetworkInfo info = cm.getNetworkInfo(9);
        if (info != null && info.isConnected()) {
            return true;
        }
        info = cm.getNetworkInfo(1);
        if (info != null && info.isConnected()) {
            return true;
        }
        info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || onlyWifiAllowed) {
            return false;
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    public boolean isOnWifi() {
        return ((ConnectivityManager) getSystemService("connectivity")).getNetworkInfo(1).isConnectedOrConnecting();
    }

    public void setSession(String sessionId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("sessionID", sessionId);
        edit.apply();
    }

    public void setRefreshToken(String refreshToken) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("refreshToken", refreshToken);
        edit.apply();
    }

    public void setLoginInformation(String accessToken, String clientId, String profileId, String profileName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("accessToken", accessToken);
        edit.putString("clientId", clientId);
        edit.putString("profileId", profileId);
        edit.putString("profileName", profileName);
        edit.apply();
    }

    public void clearLoginInformation() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove("accessToken");
        edit.remove("clientId");
        edit.remove("profileId");
        edit.remove("profileName");
        edit.apply();
    }

    public String getAccessToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("accessToken", "");
    }

    public String getClientId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("clientId", "");
    }

    public String getProfileId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("profileId", "");
    }

    public String getProfileName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("profileName", "");
    }

    public void statsTrackEvent(String eventName, String eventParameters) {
    }

    public void statsUpdateUserData(String graphicsVendor, String graphicsRenderer) {
    }

    public String[] getBroadcastAddresses() {
        Log.i("ModdedPE", "get broadcast addresses");
        return new String[]{"255.255.255.255"};
    }

    public boolean isChromebook() {
        return getWindow().getContext().getPackageManager().hasSystemFeature("android.hardware.type.pc");
    }

    public String chromebookCompatibilityIP() {
        /*Context activityContext = getWindow().getContext();
        if (isChromebook() && activityContext.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == 0) {
            int ip = activityContext.getSystemService(WifiManager.class).getConnectionInfo().getIpAddress();
            if (ip != 0) {
                if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                    ip = Integer.reverseBytes(ip);
                }
                try {
                    return InetAddress.getByAddress(BigInteger.valueOf((long) ip).toByteArray()).getHostAddress();
                } catch (UnknownHostException e) {
                }
            }
        }*/
        return "";
    }

    public String[] getIPAddresses() {
        System.out.println("get IP addresses?");
        return new String[]{"127.0.0.1"};
    }

    public void initiateUserInput(int id) {
        _userInputText = null;
        _userInputStatus = -1;
    }

    public int getUserInputStatus() {
        return _userInputStatus;
    }

    public String[] getUserInputString() {
        return _userInputText;
    }

    @SuppressLint("WrongConstant")
    public void vibrate(int milliSeconds) {
        ((Vibrator) getSystemService("vibrator")).vibrate((long) milliSeconds);
    }

    @SuppressLint("WrongConstant")
    public MemoryInfo getMemoryInfo() {
        long currentTime = SystemClock.uptimeMillis();
        if (currentTime >= mCachedMemoryInfoUpdateTime) {
            ((ActivityManager) getSystemService("activity")).getMemoryInfo(mCachedMemoryInfo);
            mCachedMemoryInfoUpdateTime = 2000 + currentTime;
        }
        return mCachedMemoryInfo;
    }

    public long getTotalMemory() {
        MemoryInfo memoryInfo = getMemoryInfo();
        if (VERSION.SDK_INT >= 16) {
            return memoryInfo.totalMem;
        }
        return memoryInfo.availMem;
    }

    public long getFreeMemory() {
        MemoryInfo info = getMemoryInfo();
        return info.availMem - info.threshold;
    }

    public long getMemoryLimit() {
        return getTotalMemory() - getMemoryInfo().threshold;
    }

    public long getUsedMemory() {
        long currentTime = SystemClock.uptimeMillis();
        if (currentTime >= mCachedUsedMemoryUpdateTime) {
            mCachedUsedMemory = Debug.getNativeHeapAllocatedSize();
            mCachedUsedMemoryUpdateTime = 10000 + currentTime;
        }
        return mCachedUsedMemory;
    }

    public long calculateAvailableDiskFreeSpace(String rootPath) {
        StatFs stat = new StatFs(rootPath);
        if (VERSION.SDK_INT >= 18) {
            return stat.getAvailableBytes();
        }
        return stat.getAvailableBlocks() * stat.getBlockSize();
    }

    @SuppressLint({"DefaultLocale"})
    public void onStart() {
        Log.d("ModdedPE", "onStart");
        super.onStart();
        deviceManager.register();
        if (_fromOnCreate) {
            _fromOnCreate = false;
            processIntent(getIntent());
        }
        /**********************************
         * Bg music                       *
         **********************************/
        if (bound && paused) {
            ss.play();
            paused = false;
        }
        /**********************************
         * Bg music                       *
         **********************************/
    }

    @NotNull
    private File copyContentStoreToTempFile(Uri content) {
        return copyContentStoreToTempFile(content, "skintemp.png");
    }

    @SuppressLint("SdCardPath")
    @NotNull
    private File copyContentStoreToTempFile(Uri content, String targetName) {
        try {
            File tempFile = new File(this.getExternalFilesDir(null), targetName);
            tempFile.getParentFile().mkdirs();
            InputStream is = getContentResolver().openInputStream(content);
            OutputStream os = new FileOutputStream(tempFile);
            byte[] buffer = new byte[0x1000];
            int count;
            while ((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            is.close();
            os.close();
            return tempFile;
        } catch (IOException ie) {
            ie.printStackTrace();
            return new File("/sdcard/totally/fake");
        }
    }

    @Override
    public void onResume() {
        boolean numbersOnly;
        boolean isMultiline;
        Log.d("ModdedPE", "onResume");
        super.onResume();

    /*    // Show menu button.
        final FloatButton hb = new FloatButton(MainActivity.this);
        final MainActivity thiz = this;
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thiz.runOnUiThread(() -> hb.showAtLocation(thiz.getWindow().getDecorView(),
                    Gravity.TOP | Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, 0));
        }).start();
    */
        registerReceiver(this.headsetConnectionReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        if (isTextWidgetActive()) {
            String obj = this.textInputWidget.getText().toString();
            int i = this.textInputWidget.allowedLength;
            boolean z = (this.textInputWidget.getInputType() & 2) == 2;
            boolean z2 = (this.textInputWidget.getInputType() & PKIFailureInfo.unsupportedVersion) == 131072;
            dismissTextWidget();
            showKeyboard(obj, i, false, z, z2);
        }
        for (ActivityListener onResume : this.mActivityListeners) {
            onResume.onResume();
        }
    }

    public void onPause() {
        Log.d("MinecraftPE", "onPause");
        nativeSuspend();
        super.onPause();
        if (isFinishing()) {
            nativeShutdown();
        }
    }

    public void onStop() {
        Log.d("MinecraftPE", "onStop");
        nativeStopThis();
        super.onStop();
        deviceManager.unregister();
        for (ActivityListener listener : mActivityListeners) {
            listener.onStop();
        }
        /**********************************
         * Bg music                       *
         **********************************/
        if (bound && !paused) {
            ss.pause();
            paused = true;
        }
        /**********************************
         * Bg music                       *
         **********************************/
    }

    public void onDestroy() {
        Log.d("ModdedPE", "onDestroy");
        mInstance = null;
        System.out.println("onDestroy");
        FMOD.close();
        for (ActivityListener listener : new ArrayList<>(this.mActivityListeners)) {
            listener.onDestroy();
        }
        nativeOnDestroy();
        super.onDestroy();
        System.exit(0);
        /**********************************
         * Bg music                       *
         **********************************/
        if (bound) {
            unbindService(sc);
        }
        /**********************************
         * Bg music                       *
         **********************************/
        System.exit(0);
    }

    public boolean isDemo() {
        return false;
    }

    public boolean isFirstSnooperStart() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("snooperId", "").isEmpty();
    }

    public String getLegacyDeviceID() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("snooperId", "");
    }

    public String createUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public Intent createAndroidLaunchIntent() {
        Context context = getApplicationContext();
        return context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    }

    public boolean hasHardwareChanged() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lastAndroidVersion = prefs.getString("lastAndroidVersion", "");
        boolean firstHardwareStart = lastAndroidVersion.isEmpty() || !lastAndroidVersion.equals(VERSION.RELEASE);
        if (firstHardwareStart) {
            Editor edit = prefs.edit();
            edit.putString("lastAndroidVersion", VERSION.RELEASE);
            edit.apply();
        }
        return firstHardwareStart;
    }

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & 15) == 4;
    }

    public void pickImage(long callback) {
        mCallback = callback;
        try {
            startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setFileDialogCallback(long callback) {
        mFileDialogCallback = callback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] filePathColumn = new String[]{"_data"};
        Cursor cursor;
        super.onActivityResult(requestCode, resultCode, data);
        for (ActivityListener listener : mActivityListeners) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode != 1) {
            return;
        }
        if (resultCode == -1 && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null)) != null) {
                cursor.moveToFirst();
                nativeOnPickImageSuccess(mCallback, cursor.getString(cursor.getColumnIndex(filePathColumn[0])));
                mCallback = 0;
                cursor.close();
            }
        } else if (mCallback != 0) {
            nativeOnPickImageCanceled(mCallback);
            mCallback = 0;
        }
    }

    public void addListener(ActivityListener listener) {
        mActivityListeners.add(listener);
    }

    public void removeListener(ActivityListener listener) {
        mActivityListeners.remove(listener);
    }

    public void startTextToSpeech(String s) {
        if (textToSpeechManager != null) {
            textToSpeechManager.speak(s, 0, null);
        }
    }

    public void stopTextToSpeech() {
        if (textToSpeechManager != null) {
            textToSpeechManager.stop();
        }
    }

    public boolean isTextToSpeechInProgress() {
        if (textToSpeechManager != null) {
            return textToSpeechManager.isSpeaking();
        }
        return false;
    }

    public int getAPIVersion(String apiName) {
        System.out.println("Get API version: " + apiName);
        try {
            Field field = VERSION_CODES.class.getField(apiName);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void registerCrashManager() {
    }

    public String MC_GetActiveScreen() {
        if (isTestInfrastructureDisabled()) {
            return "";
        }
        return nativeGetActiveScreen();
    }

    public String MC_SetOptions(String optionsString) {
        if (isTestInfrastructureDisabled()) {
            return "";
        }
        return nativeSetOptions(optionsString);
    }

    public String MC_CheckIfTestsAreFinished() {
        if (isTestInfrastructureDisabled()) {
            return "";
        }
        return nativeCheckIfTestsAreFinished();
    }

    public String MC_GetDevConsoleLogName() {
        if (isTestInfrastructureDisabled()) {
            return "";
        }
        return nativeGetDevConsoleLogName();
    }

    public String MC_GetLogText(String fileInfo) {
        if (isTestInfrastructureDisabled()) {
            return "";
        }
        return nativeGetLogText(fileInfo);
    }

    public boolean isTTSEnabled() {
        if (getApplicationContext() != null) {
            @SuppressLint("WrongConstant")
            AccessibilityManager am = (AccessibilityManager) getSystemService("accessibility");
            if (!(am == null || !am.isEnabled() || am.getEnabledAccessibilityServiceList(1).isEmpty())) {
                return true;
            }
        }
        return false;
    }

    public void initializeXboxLive(long xalInitArgs, long xblInitArgs) {
        runOnUiThread(() -> nativeInitializeXboxLive(xalInitArgs, xblInitArgs));
    }

    enum MessageConnectionStatus {
        NOTSET,
        CONNECTED,
        DISCONNECTED
    }

    private class HeadsetConnectionReceiver extends BroadcastReceiver {
        private HeadsetConnectionReceiver() {
        }

        public void onReceive(Context context, @NotNull Intent intent) {
            if (intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
                switch (intent.getIntExtra("state", -1)) {
                    case 0:
                        Log.d("ModdedPE", "Headset unplugged");
                        nativeSetHeadphonesConnected(false);
                        return;
                    case 1:
                        Log.d("ModdedPE", "Headset plugged in");
                        nativeSetHeadphonesConnected(true);
                        return;
                    default:
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        public void handleMessage(@NotNull Message msg) {
            if (msg.what == 837) {
                String myName = getApplicationContext().getPackageName();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                try {
                    long myTime = getPackageManager().getPackageInfo(myName, 0).firstInstallTime;
                    String theirId = msg.getData().getString("deviceId");
                    String theirLastSessionId = msg.getData().getString("sessionId");
                    long theirTime = msg.getData().getLong("time");
                    if (myTime > theirTime) {
                        prefs.edit().apply();
                        nativeDeviceCorrelation(myTime, theirId, theirTime, theirLastSessionId);
                    }
                    Editor edit = prefs.edit();
                    edit.putInt("correlationAttempts", 0);
                    edit.apply();
                    if (mBound == MessageConnectionStatus.CONNECTED) {
                        unbindService(mConnection);
                        return;
                    }
                    return;
                } catch (NameNotFoundException e) {
                    return;
                }
            }
            super.handleMessage(msg);
        }
    }
}