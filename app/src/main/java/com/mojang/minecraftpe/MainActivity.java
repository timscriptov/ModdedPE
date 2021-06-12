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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
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
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appboy.Constants;
import com.appsflyer.AppsFlyerLib;
import com.mcal.mcpelauncher.BuildConfig;
import com.mcal.mcpelauncher.utils.ScopedStorage;
import com.mojang.android.StringValue;
import com.mojang.minecraftpe.input.InputDeviceManager;
import com.mojang.minecraftpe.platforms.Platform;

import org.fmod.FMOD;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MainActivity extends NativeActivity implements OnKeyListener {
    public static MainActivity mInstance = null;
    public static int RESULT_PICK_IMAGE = 1;
    private static boolean _isPowerVr = false;
    private final String MARKET_URL_FORMAT = "market://details?id=%s";
    private final String ACTION_TCUI_BROADCAST_LAUNCH = "com.microsoft.beambroadcast.xle.app.action.TCUI";
    private final String BROADCAST_TITLE_NAME_KEY = "name";
    private final String MINECRAFT_BROADCAST_TITLE = "Minecraft";
    private final String PACKAGE_NAME_KEY = "package";
    private final String SESSION_HISTORY_SEP = "&";
    private final String SESSION_HISTORY_KEY = "session-history";
    private final String MIXER_CREATE_BETA_PACKAGE = "com.microsoft.beambroadcast.beta";
    private final String MIXER_CREATE_INTERNAL_BETA_PACKAGE = "com.microsoft.beambroadcast.beta.internal";
    private final String MIXER_CREATE_RETAIL_PACKAGE = "com.microsoft.beambroadcast";
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private final List<ActivityListener> mActivityListeners = new ArrayList<ActivityListener>();
    private final MemoryInfo mCachedMemoryInfo = new MemoryInfo();
    private final ArrayList<StringValue> _userInputValues = new ArrayList<>();
    private final SentryEndpointConfig mSentryEndpointAndroidTrial = new SentryEndpointConfig("https://sentry.io", "2308440", "668bc09f7bcf461796ea07c1006076fe");
    private final SentryEndpointConfig mSentryEndpointBedrock = new SentryEndpointConfig("https://sentry.io", "2277697", "1c3f5cbd723a4a84879059d260b19ef6");
    private final SentryEndpointConfig mSentryEndpointEDU = new SentryEndpointConfig("https://o339720.ingest.sentry.io", "5242741", "0d409f4cd2f64aa3a686a1b5193a4bf9");
    public int mLastPermissionRequestReason;
    public int virtualKeyboardHeight = 0;
    public ParcelFileDescriptor mPickedFileDescriptor;
    Class SystemProperties;
    Method getPropMethod;
    private boolean mHasStoragePermission = false;
    private HeadsetConnectionReceiver headsetConnectionReceiver;
    private MessageConnectionStatus mBound = MessageConnectionStatus.NOTSET;
    private long mCachedMemoryInfoUpdateTime = 0;
    private long mCachedUsedMemory = 0;
    private long mCachedUsedMemoryUpdateTime = 0;
    private Messenger mService = null;
    private final ServiceConnection mConnection = new ServiceConnection() {
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
    private Platform platform;
    private TextInputProxyEditTextbox textInputWidget;
    private boolean _fromOnCreate = false;
    private int _userInputStatus = -1;
    private String[] _userInputText = null;
    private ClipboardManager clipboardManager;
    private long mCallback = 0;
    private InputDeviceManager deviceManager;
    private ArrayList<SessionInfo> mSessionHistory = null;
    private long mFileDialogCallback = 0;
    private HardwareInformation mHardwareInformation;
    private TextToSpeech textToSpeechManager;
    private ThermalMonitor mThermalMonitor;
    private BatteryMonitor mBatteryMonitor;
    private float mVolume = 1.0f;
    private Locale initialUserLocale;
    private boolean mCursorLocked = false;
    private boolean mIsSoftKeyboardVisible = false;

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

    private static native void nativeConfigureNewSession(SessionInfo sessionInfo);

    private static native void nativeWaitCrashManagementSetupComplete();

    public void openAndroidAppSettings() {
        try {
            startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            Log.e("MCPE", "openAndroidAppSettings: Failed to open android app settings: " + e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void lockCursor() {
        if (getAndroidVersion() >= 26) {
            @SuppressLint("ResourceType") View rootView = findViewById(16908290).getRootView();
            this.mCursorLocked = true;
            rootView.requestPointerCapture();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void unlockCursor() {
        if (getAndroidVersion() >= 26) {
            @SuppressLint("ResourceType") View rootView = findViewById(16908290).getRootView();
            this.mCursorLocked = false;
            rootView.releasePointerCapture();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        if (this.mCursorLocked && (motionEvent.getSource() & 8194) == 8194) {
            lockCursor();
        }
        return super.dispatchGenericMotionEvent(motionEvent);
    }

    public boolean isSoftKeyboardVisible() {
        return this.mIsSoftKeyboardVisible;
    }

    public void copyToPickedFile(String str) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(str));
            try {
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(this.mPickedFileDescriptor);
                try {
                    copyFile(fileInputStream, autoCloseOutputStream);
                    autoCloseOutputStream.close();
                } catch (Throwable th) {
                    autoCloseOutputStream.close();
                    throw th;
                }
            } finally {
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mPickedFileDescriptor = null;
    }

    public void copyFromPickedFile(String str) {
        try {
            ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(this.mPickedFileDescriptor);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
                try {
                    copyFile(autoCloseInputStream, fileOutputStream);
                    fileOutputStream.close();
                } catch (Throwable th) {
                    fileOutputStream.close();
                    throw th;
                }
            } finally {
                autoCloseInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mPickedFileDescriptor = null;
    }

    public byte[] getFileDataBytes(String str) {
        BufferedInputStream bufferedInputStream;
        if (str.isEmpty()) {
            return null;
        }
        try {
            AssetManager assets = getApplicationContext().getAssets();
            if (assets == null) {
                PrintStream printStream = System.err;
                printStream.println("getAssets returned null: Could not getFileDataBytes " + str);
                return null;
            }
            try {
                bufferedInputStream = new BufferedInputStream(assets.open(str));
            } catch (IOException unused) {
                new File(str);
                try {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(str));
                } catch (IOException unused2) {
                    return null;
                }
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1048576);
            byte[] bArr = new byte[1048576];
            try {
                while (true) {
                    int read = 0;

                    read = bufferedInputStream.read(bArr);

                    if (read > 0) {
                        byteArrayOutputStream.write(bArr, 0, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedInputStream.close();
            } catch (IOException unused3) {
            }
            return byteArrayOutputStream.toByteArray();
        } catch (NullPointerException unused4) {
            PrintStream printStream2 = System.err;
            printStream2.println("getAssets threw NPE: Could not getFileDataBytes " + str);
            return null;
        }
    }

    public int[] getImageData(String str) {
        Bitmap decodeFile = BitmapFactory.decodeFile(str);
        if (decodeFile == null) {
            try {
                AssetManager assets = getApplicationContext().getAssets();
                if (assets != null) {
                    try {
                        decodeFile = BitmapFactory.decodeStream(assets.open(str));
                    } catch (IOException unused) {
                        PrintStream printStream = System.err;
                        printStream.println("getImageData: Could not open image " + str);
                        return null;
                    }
                } else {
                    PrintStream printStream2 = System.err;
                    printStream2.println("getAssets returned null: Could not open image " + str);
                    return null;
                }
            } catch (NullPointerException unused2) {
                PrintStream printStream3 = System.err;
                printStream3.println("getAssets threw NPE: Could not open image " + str);
                return null;
            }
        }
        int width = decodeFile.getWidth();
        int height = decodeFile.getHeight();
        int[] iArr = new int[((width * height) + 2)];
        iArr[0] = width;
        iArr[1] = height;
        decodeFile.getPixels(iArr, 2, width, 0, 0, width, height);
        return iArr;
    }

    private native void fireCrashedTelemetry(String str, String str2, String str3);

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

    public native void nativeOnPickFileSuccess(String str);

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

    public native void nativeOnPickFileCanceled();

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
        clipboardManager.setPrimaryClip(ClipData.newPlainText("ModdedPE-Clipdata", value));
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
        ArrayList<SessionInfo> arrayList = mSessionHistory;
        return arrayList.get(arrayList.size() - 1).sessionId;

    }

    @SuppressLint("WrongConstant")
    public void deviceIdCorrelationStart() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int attempts = prefs.getInt("correlationAttempts", 10);
        if (attempts != 0) {
            Intent i = new Intent();
            i.setComponent(new ComponentName(getPackageName().contains("trial") ? BuildConfig.APPLICATION_ID : "com.mojang.minecrafttrialpe", "com.mojang.minecraftpe.ImportService"));
            bindService(i, mConnection, 1);
            Editor edit = prefs.edit();
            edit.putInt("correlationAttempts", attempts - 1);
            edit.apply();
        }
    }

    public BatteryMonitor getBatteryMonitor() {
        if (mBatteryMonitor == null) {
            mBatteryMonitor = new BatteryMonitor(this);
        }
        return mBatteryMonitor;
    }

    public HardwareInformation getHardwareInfo() {
        if (mHardwareInformation == null) {
            mHardwareInformation = new HardwareInformation(this);
        }
        return mHardwareInformation;
    }

    public ThermalMonitor getThermalMonitor() {
        if (mThermalMonitor == null) {
            mThermalMonitor = new ThermalMonitor(this);
        }
        return mThermalMonitor;
    }

    public CrashManager initializeCrashManager(String str, String str2) {
        SentryEndpointConfig sentryEndpointConfig;
        if (isAndroidTrial()) {
            sentryEndpointConfig = this.mSentryEndpointAndroidTrial;
        } else if (isEduMode()) {
            sentryEndpointConfig = this.mSentryEndpointEDU;
        } else {
            sentryEndpointConfig = this.mSentryEndpointBedrock;
        }
        CrashManager crashManager = new CrashManager(str, str2, sentryEndpointConfig);
        crashManager.installGlobalExceptionHandler();
        return crashManager;
    }

    @SuppressLint({"WrongConstant", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void setVolume(float f) {
        mVolume = f;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onWindowFocusChanged(boolean z) {
        if (z && this.mCursorLocked) {
            lockCursor();
        }
        super.onWindowFocusChanged(z);
        this.platform.onViewFocusChanged(z);
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
            String stringExtra = intent.getStringExtra("intent_cmd");
            if (stringExtra == null || stringExtra.length() <= 0) {
                String action = intent.getAction();
                intent.getType();
                if ("xbox_live_game_invite".equals(action)) {
                    String stringExtra2 = intent.getStringExtra("xbl");
                    Log.d("ModdedPE", "[XboxLive] Received Invite " + stringExtra2);
                    nativeProcessIntentUriQuery(action, stringExtra2);
                } else if ("android.intent.action.VIEW".equals(action) || "org.chromium.arc.intent.action.VIEW".equals(action)) {
                    String scheme = intent.getScheme();
                    Uri data2 = intent.getData();
                    if (data2 != null) {
                        if ("minecraft".equalsIgnoreCase(scheme) || "minecraftedu".equalsIgnoreCase(scheme)) {
                            String host = data2.getHost();
                            String query = data2.getQuery();
                            if (host != null || query != null) {
                                nativeProcessIntentUriQuery(host, query);
                            }
                        } else if ("file".equalsIgnoreCase(scheme)) {
                            nativeProcessIntentUriQuery("fileIntent", data2.getPath() + SESSION_HISTORY_SEP + data2.getPath());
                        } else if ("content".equalsIgnoreCase(scheme)) {
                            String name = new File(data2.getPath()).getName();
                            File file = new File(getApplicationContext().getCacheDir() + "/" + name);
                            try {
                                InputStream openInputStream = getContentResolver().openInputStream(data2);
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    byte[] bArr = new byte[1048576];
                                    while (true) {
                                        int read = openInputStream.read(bArr);
                                        if (read != -1) {
                                            fileOutputStream.write(bArr, 0, read);
                                        } else {
                                            fileOutputStream.close();
                                            nativeProcessIntentUriQuery("contentIntent", data2.getPath() + SESSION_HISTORY_SEP + file.getAbsolutePath());
                                            try {
                                                openInputStream.close();
                                                return;
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } catch (IOException e2) {
                                    Log.e("ModdedPE", "IOException while copying file from content intent\n" + e2.toString());
                                    file.delete();
                                    openInputStream.close();
                                } catch (Throwable th) {
                                    try {
                                        openInputStream.close();
                                    } catch (IOException e3) {
                                        Log.e("ModdedPE", "IOException while closing input stream\n" + e3.toString());
                                    }
                                    throw th;
                                }
                            } catch (IOException e4) {
                                Log.e("ModdedPE", "IOException while opening file from content intent\n" + e4.toString());
                            }
                        }
                    }
                }
            } else {
                try {
                    JSONObject jSONObject = new JSONObject(stringExtra);
                    String string = jSONObject.getString("Command");
                    if (string.equals("keyboardResult")) {
                        nativeSetTextboxText(jSONObject.getString("Text"));
                    } else if (string.equals("fileDialogResult") && mFileDialogCallback != 0) {
                        if (jSONObject.getString("Result").equals("Ok")) {
                            nativeOnPickImageSuccess(mFileDialogCallback, jSONObject.getString("Path"));
                        } else {
                            nativeOnPickImageCanceled(mFileDialogCallback);
                        }
                        this.mFileDialogCallback = 0;
                    }
                } catch (JSONException e5) {
                    Log.d("ModdedPE", "JSONObject exception:" + e5.toString());
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
                textToSpeechManager = new TextToSpeech(getApplicationContext(), i -> {
                });
            } catch (Exception e) {
                e.printStackTrace();
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
            mHasStoragePermission = grantResults[0] == 0;
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
        return getApplicationContext() != null && getMixerCreateInstalledPackage() != null;
    }

    private @Nullable Intent getMixerCreateInstalledPackage() {
        Context applicationContext = getApplicationContext();
        if (applicationContext != null) {
            Intent mixerCreateInternalBetaPackage = applicationContext.getPackageManager().getLaunchIntentForPackage(MIXER_CREATE_INTERNAL_BETA_PACKAGE);
            if (mixerCreateInternalBetaPackage != null) {
                Log.d("ModdedPE", "Found internal beta package installed");
                return mixerCreateInternalBetaPackage;
            }
            Intent mixerCreateBetaPackage = applicationContext.getPackageManager().getLaunchIntentForPackage(MIXER_CREATE_BETA_PACKAGE);
            if (mixerCreateBetaPackage != null) {
                Log.d("ModdedPE", "Found beta package installed");
                return mixerCreateBetaPackage;
            }
            Intent mixerCreateRetailPackage = applicationContext.getPackageManager().getLaunchIntentForPackage(MIXER_CREATE_RETAIL_PACKAGE);
            if (mixerCreateRetailPackage == null) {
                return null;
            }
            Log.d("ModdedPE", "Found retail package installed");
            return mixerCreateRetailPackage;
        }
        Log.w("ModdedPE", "Application context is null");
        return null;
    }

    @SuppressLint("WrongConstant")
    public void navigateToPlaystoreForMixerCreate() {
        Context applicationContext = getApplicationContext();
        if (applicationContext != null) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(String.format(MARKET_URL_FORMAT, MIXER_CREATE_RETAIL_PACKAGE)));
            intent.addFlags(268435456);
            try {
                applicationContext.startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                Log.w("ModdedPE", "launch market place- failed");
            }
        } else {
            Log.w("ModdedPE", "Application context is null");
        }
    }

    @SuppressLint("WrongConstant")
    public boolean launchMixerCreateForBroadcast() {
        Context applicationContext = getApplicationContext();
        if (applicationContext != null) {
            Intent mixerCreateInstalledPackage = getMixerCreateInstalledPackage();
            if (mixerCreateInstalledPackage != null) {
                mixerCreateInstalledPackage.setAction(ACTION_TCUI_BROADCAST_LAUNCH);
                Bundle bundle = new Bundle();
                bundle.putString(BROADCAST_TITLE_NAME_KEY, MINECRAFT_BROADCAST_TITLE);
                bundle.putString(PACKAGE_NAME_KEY, getPackageName());
                mixerCreateInstalledPackage.putExtras(bundle);
                mixerCreateInstalledPackage.addFlags(67108864);
                applicationContext.startActivity(mixerCreateInstalledPackage);
                return true;
            }
            Log.w("ModdedPE", "No mixer create package installed on the device");
            return false;
        }
        Log.w("ModdedPE", "Application context is null");
        return false;
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

    @SuppressLint({"ResourceType", "SetTextI18n"})
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
            isVirtualEnter = actionId == 5;
            isHardwareEnter = actionId == 0 && event != null && event.getAction() == 0;
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
                    textWidget.setText(curText.substring(0, curSelect) + "\n" + curText.substring(curSelect));
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
                runOnUiThread(() -> {
                    Log.w("ModdedPE", "textInputWidget.onBackPressed");
                    nativeBackPressed();
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
        runOnUiThread(() -> {
            Locale locale = new Locale(lang, region);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        });
    }

    public void showKeyboard(String text, int maxLength, boolean limitInput, boolean numbersOnly, boolean isMultiline) {
        nativeClearAButtonState();
        final String startText = text;
        final int fMaxLength = maxLength;
        final boolean fLimitInput = limitInput;
        final boolean fNumbersOnly = numbersOnly;
        final boolean fIsMultiline = isMultiline;
        runOnUiThread(new Runnable() {
            public void run() {
                setupKeyboardViews(startText, fMaxLength, fLimitInput, fNumbersOnly, fIsMultiline);
            }
        });
    }

    public void hideKeyboard() {
        runOnUiThread(this::dismissTextWidget);
    }

    @SuppressLint("WrongConstant")
    public boolean isTextWidgetActive() {
        TextInputProxyEditTextbox textInputProxyEditTextbox = textInputWidget;
        return textInputProxyEditTextbox != null && textInputProxyEditTextbox.getVisibility() == 0;
    }

    @SuppressLint("WrongConstant")
    public void dismissTextWidget() {
        if (isTextWidgetActive()) {
            getInputMethodManager().hideSoftInputFromWindow(textInputWidget.getWindowToken(), 0);
            textInputWidget.setInputType(PKIFailureInfo.signerNotTrusted);
            textInputWidget.setVisibility(8);
        }
    }

    public void updateTextboxText(String newText) {
        runOnUiThread(() -> {
            if (isTextWidgetActive()) {
                textInputWidget.setTextFromGame(newText);
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

    public int getScreenWidth() {
        @SuppressLint("WrongConstant")
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int max = Math.max(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        System.out.println("getwidth: " + max);
        return max;
    }

    public int getScreenHeight() {
        @SuppressLint("WrongConstant")
        Display defaultDisplay = ((WindowManager) getSystemService("window")).getDefaultDisplay();
        int min = Math.min(defaultDisplay.getWidth(), defaultDisplay.getHeight());
        System.out.println("getheight: " + min);
        return min;
    }

    public void saveFile(String str) {
        Intent intent = new Intent("android.intent.action.CREATE_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.TITLE", str);
        startActivityForResult(intent, 4);
    }

    public void openFile() {
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        startActivityForResult(intent, 5);
    }

    public void onPickFileSuccess(boolean z) {
        String str = getApplicationContext().getCacheDir() + "/tempPickedFile";
        if (z) {
            copyFromPickedFile(str);
        }
        nativeOnPickFileSuccess(str);
    }

    public String getProp(String str) {
        try {
            if (this.getPropMethod == null || this.SystemProperties == null) {
                @SuppressLint("PrivateApi") Class<?> cls = Class.forName("android.os.SystemProperties");
                this.SystemProperties = cls;
                this.getPropMethod = cls.getMethod("get", String.class);
            }
            return (String) this.getPropMethod.invoke(this.SystemProperties, str);
        } catch (Exception e) {
            Log.e("MCPE", "Exception occured while getting a property [" + str + "]\n" + e.getMessage());
            return "";
        }
    }

    public int getAndroidVersion() {
        return VERSION.SDK_INT;
    }

    public String getDeviceModel() {
        return HardwareInformation.getDeviceModelName();
    }

    public String getLocale() {
        return HardwareInformation.getLocale();
    }

    public String getObbDirPath() {
        return getApplicationContext().getObbDir().getAbsolutePath();
    }

    public String getExternalStoragePath() {
        return ScopedStorage.getStorageDirectory().toString();
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
        return info != null && info.isConnected() && !onlyWifiAllowed;
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
        ArrayList arrayList = new ArrayList();
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if (!nextElement.isLoopback()) {
                    for (InterfaceAddress next : nextElement.getInterfaceAddresses()) {
                        if (next.getBroadcast() != null) {
                            arrayList.add(next.getBroadcast().toString().substring(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public boolean isChromebook() {
        return getWindow().getContext().getPackageManager().hasSystemFeature("android.hardware.type.pc");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    public String chromebookCompatibilityIP() {
        int ipAddress;
        Context context = getWindow().getContext();
        if (!isChromebook() || context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") != 0 || (ipAddress = (context.getSystemService(WifiManager.class)).getConnectionInfo().getIpAddress()) == 0) {
            return "";
        }
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }
        try {
            return InetAddress.getByAddress(BigInteger.valueOf(ipAddress).toByteArray()).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String[] getIPAddresses() {
        ArrayList arrayList = new ArrayList();
        String chromebookCompatibilityIP = chromebookCompatibilityIP();
        if (!chromebookCompatibilityIP.isEmpty()) {
            arrayList.add(chromebookCompatibilityIP);
        }
        try {
            System.setProperty("java.net.preferIPv4Stack", "true");
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nextElement = networkInterfaces.nextElement();
                if (!nextElement.isLoopback() && nextElement.isUp()) {
                    for (InterfaceAddress next : nextElement.getInterfaceAddresses()) {
                        InetAddress address = next.getAddress();
                        if (address != null && !address.isAnyLocalAddress() && !address.isMulticastAddress() && !address.isLinkLocalAddress()) {
                            arrayList.add(next.getAddress().toString().substring(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);

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
        ((Vibrator) getSystemService("vibrator")).vibrate(milliSeconds);
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
        try {
            StatFs statFs = new StatFs(rootPath);
            if (Build.VERSION.SDK_INT >= 18) {
                return statFs.getAvailableBytes();
            }
            return statFs.getAvailableBlocks() * statFs.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
    }

    @Override
    public void onResume() {
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
        registerReceiver(headsetConnectionReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        if (isTextWidgetActive()) {
            String obj = textInputWidget.getText().toString();
            int i = textInputWidget.allowedLength;
            boolean z = (textInputWidget.getInputType() & 2) == 2;
            boolean z2 = (textInputWidget.getInputType() & PKIFailureInfo.unsupportedVersion) == 131072;
            dismissTextWidget();
            showKeyboard(obj, i, false, z, z2);
        }
        for (ActivityListener onResume : mActivityListeners) {
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
    }

    public void onDestroy() {
        Log.d("ModdedPE", "onDestroy");
        mInstance = null;
        System.out.println("onDestroy");
        FMOD.close();
        for (ActivityListener listener : new ArrayList<>(mActivityListeners)) {
            listener.onDestroy();
        }
        nativeOnDestroy();
        super.onDestroy();
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

    @SuppressLint("Range")
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

    @SuppressLint("ObsoleteSdkInt")
    public void startTextToSpeech(String str) {
        if (textToSpeechManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < 21) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("volume", String.valueOf(mVolume));
            this.textToSpeechManager.speak(str, 0, hashMap);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putFloat("volume", mVolume);
        this.textToSpeechManager.speak(str, 0, bundle, null);
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
        Field[] fields = Build.VERSION_CODES.class.getFields();
        int length = fields.length;
        int i = 0;
        while (i < length) {
            Field field = fields[i];
            if (field.getName().equals(apiName)) {
                try {
                    return field.getInt(new Object());
                } catch (IllegalArgumentException unused) {
                    Log.e("ModdedPE", "IllegalArgumentException in getApiVersion(" + apiName + ")");
                } catch (IllegalAccessException unused2) {
                    Log.e("ModdedPE", "IllegalAccessException in getApiVersion(" + apiName + ")");
                } catch (NullPointerException unused3) {
                    Log.e("ModdedPE", "NullPointerException in getApiVersion(" + apiName + ")");
                }
            } else {
                i++;
            }
        }
        Log.e("ModdedPE", "Failed to find API version for: " + apiName);
        return -1;

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

    @SuppressLint("WrongConstant")
    public boolean isTTSEnabled() {
        AccessibilityManager accessibilityManager;
        return getApplicationContext() != null && (accessibilityManager = (AccessibilityManager) getSystemService("accessibility")) != null && accessibilityManager.isEnabled() && !accessibilityManager.getEnabledAccessibilityServiceList(1).isEmpty();
    }

    public void initializeXboxLive(long xalInitArgs, long xblInitArgs) {
        runOnUiThread(() -> nativeInitializeXboxLive(xalInitArgs, xblInitArgs));
    }

    enum MessageConnectionStatus {
        NOTSET,
        CONNECTED,
        DISCONNECTED
    }

    @SuppressLint("HandlerLeak")
    public class IncomingHandler extends Handler {
        public void handleMessage(Message message) {
            if (message.what != 837) {
                super.handleMessage(message);
                return;
            }
            String packageName = MainActivity.this.getApplicationContext().getPackageName();
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
            defaultSharedPreferences.getString("deviceId", "");
            try {
                long j = MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).firstInstallTime;
                String string = message.getData().getString("deviceId");
                String string2 = message.getData().getString("sessionId");
                long j2 = message.getData().getLong(Constants.APPBOY_LOCATION_TIME_INTERVAL_KEY);
                if (j > j2) {
                    defaultSharedPreferences.edit().apply();
                    MainActivity.this.nativeDeviceCorrelation(j, string, j2, string2);
                }
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putInt("correlationAttempts", 0);
                edit.apply();
                if (MainActivity.this.mBound == MessageConnectionStatus.CONNECTED) {
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.unbindService(mainActivity.mConnection);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
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
}