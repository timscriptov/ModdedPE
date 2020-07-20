package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.NativeActivity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mcal.mcpelauncher.app.SoundService;
import com.mojang.minecraftpe.platforms.Platform;

import org.fmod.FMOD;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import com.appsflyer.AppsFlyerLib;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends NativeActivity implements OnKeyListener {
    HeadsetConnectionReceiver headsetConnectionReceiver;
    public static MainActivity mInstance = null;
    private static boolean _isPowerVr = false;
    private static boolean mHasStoragePermission = false;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private final DateFormat DateFormat = new SimpleDateFormat();
    public int mLastPermissionRequestReason;
    public String preloadingHTML = "<!doctype html><html><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"><title>Log in</title><meta name=\"description\" content=\"\"><meta name=\"author\" content=\"\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1,maximum-scale=1\"></head><body style=\"padding:0;margin:0;background:linear-gradient(#F9F9F9, #EEEEEE) repeat scroll 0 0 #F9F9F9;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;\"><tr style=\"height:40px;\"><td style=\"background: #111111;border:1px solid #333;\"><div style=\"margin:0 auto;width:320px;line-height:26px;\"><img style=\"width:120px;height:30px;float:left;margin-right:24px;vertical-align:middle;\" alt=\"\" src=\"data:image/png;base64,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\" /><div style=\"float:left;font-size:16px;font-weight:bold;color:#fff;font-family:Helvetica,Arial,sans-serif;text-shadow:0 -1px 1px #000000;\">Log in</div><div style=\"clear:both;\"></div></div></td></tr><tr style=\"height:235px;\"><td><div style=\"margin:0 auto;width:320px;text-align:center;font-family: Helvetica,Arial,sans-serif;text-shadow: 0 1px 0 #FFFFFF;font-size:24px;font-weight:bold;\">Loading&hellip;</div></td></tr><tr style=\"height:32px;\"><td style=\"background: #111111;border:1px solid #333;\"><div style=\"text-align:center;margin:0 auto;width:320px;line-height:26px;font-size:12px;font-weight:bold;color:#fff;font-family:Helvetica,Arial,sans-serif;text-shadow:0 -1px 1px #000000;\">Mojang &copy; 2013</div></td></tr></table></body></html>";
    public int virtualKeyboardHeight = 0;
    protected DisplayMetrics displayMetrics;
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

    public SessionInfo getLastDeviceSessionInfo() {
        if (this.mLastDeviceSessionInfo == null) {
            this.mLastDeviceSessionInfo = SessionInfo.fromString(PreferenceManager.getDefaultSharedPreferences(this).getString("last-session-info", ""));
            Log.i("ModdedPE", "getLastDeviceSessionInfo was null and now: " + this.mLastDeviceSessionInfo.toString());
        } else {
            Log.i("ModdedPE", "getLastDeviceSessionInfo was not null with: " + this.mLastDeviceSessionInfo.toString());
        }
        return this.mLastDeviceSessionInfo;
    }
    
    public void setLastDeviceSessionInfo(@NotNull SessionInfo info) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("last-session-info", info.toString());
        edit.apply();
        Log.i("MCPE", "setLastDeviceSessionInfo: " + info.toString());
        this.mLastDeviceSessionInfo = info;
    }

    public void setLastDeviceSessionInfo(String sessionId, String buildId) {
        setLastDeviceSessionInfo(new SessionInfo(sessionId, buildId));
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

    public boolean supportsNonTouchscreen() {
        return isXperiaPlay();
    }

    public native void fireCrashedTelemetry(String str, String str2, String str3);
    native String nativeScreenIsPresentInStack(String str);
    native void nativeLoginData(String accessToken, String clientId, String profileId, String profileName);
    native void nativeTypeCharacter(String character);
    native void nativeWebRequestCompleted(int requestId, long userData, int httpStatusOrNegativeError, String content);
    public native void setUpBreakpad(String str);
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
    public native boolean nativeKeyHandler(int i, int i2);
    public native void nativeOnDestroy();
    public native void nativeOnPickImageCanceled(long j);
    public native void nativeOnPickImageSuccess(long j, String str);
    public native void nativeProcessIntentUriQuery(String str, String str2);
    public native void nativeRegisterThis();
    public native void nativeResize(int i, int i2);
    public native void nativeReturnKeyPressed();
    public native void nativeSetHeadphonesConnected(boolean z);
    public native String nativeSetOptions(String str);
    public native void nativeSetTextboxText(String str);
    public native void nativeShutdown();
    public native void nativeStopThis();
    public native void nativeStoragePermissionRequestResult(boolean z, int i);
    public native void nativeSuspend();
    public native void nativeUnregisterThis();

    public void onRequestComplete(int requestId, long userData, int httpStatusOrNegativeError, String content) {
        nativeWebRequestCompleted(requestId, userData, httpStatusOrNegativeError, content);
    }

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

    /*public void trackPurchaseEvent(String contentId, String contentType, String revenue, String playerId, String playerSessionId, String currencyCode, String eventName) {
        Map<String, Object> eventValue = new HashMap<String, Object>();
        eventValue.put("player_session_id", playerSessionId);
        eventValue.put("client_id", playerId);
        eventValue.put("af_revenue", revenue);
        eventValue.put("af_content_type", contentType);
        eventValue.put("af_content_id", contentId);
        eventValue.put("af_currency", currencyCode);
        Log.d("ModdedPE", contentId + ":" + revenue + ":" + playerId + ":" +playerSessionId + ":" + currencyCode + ":" + eventName );
    }*/
    
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

    public String getHockeyAppDevId() {
        return null;
    }

    public HardwareInformation getHardwareInfo() {
        if (mHardwareInformation == null) {
            mHardwareInformation = new HardwareInformation(this);
        }
        return mHardwareInformation;
    }

    @SuppressLint({"WrongConstant", "ResourceType"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeRegisterThis();
        displayMetrics = new DisplayMetrics();
        platform = Platform.createPlatform(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        FMOD.init(this);
        platform.onAppStart(getWindow().getDecorView());
        mHasStoragePermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
        nativeSetHeadphonesConnected(((AudioManager) getSystemService("audio")).isWiredHeadsetOn());
        clipboardManager = (ClipboardManager) getSystemService("clipboard");
        initialUserLocale = Locale.getDefault();
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
                        File file = new File(getApplicationContext().getCacheDir() + "/" + new File(uri.getPath()).getName());
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
                                th.printStackTrace();
                                try {
                                    input.close();
                                } catch (IOException ioe23) {
                                    Log.e("ModdedPE", "IOException while closing input stream\n" + ioe23.toString());
                                }
                            }
                        } catch (IOException ioe3) {
                            ioe3.printStackTrace();
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
                    } else if (command.equals("fileDialogResult") && this.mFileDialogCallback != 0) {
                        if (json2.getString("Result").equals("Ok")) {
                            nativeOnPickImageSuccess(this.mFileDialogCallback, json2.getString("Path"));
                        } else {
                            nativeOnPickImageCanceled(this.mFileDialogCallback);
                        }
                        this.mFileDialogCallback = 0;
                    }
                } catch (JSONException e2) {
                    Log.d("ModdedPE", "JSONObject exception:" + e2.toString());
                }
            }
        }
    }
    
    public boolean dispatchKeyEvent(KeyEvent event) {
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
        runOnUiThread(() -> finish());
    }

    public String getFormattedDateString(int s) {
        DateFormat dateFormat = DateFormat;
        dateFormat = DateFormat;
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
        return (long) (stat.getAvailableBlocks() * stat.getBlockSize());
    }

    @SuppressLint({"DefaultLocale"})
    public void onStart() {
        Log.d("ModdedPE", "onStart");
        super.onStart();
        if (this._fromOnCreate) {
            this._fromOnCreate = false;
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
    
    public void onResume() {
        boolean numbersOnly;
        boolean isMultiline;
        Log.d("ModdedPE", "onResume");
        super.onResume();
        registerReceiver(this.headsetConnectionReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        if (isTextWidgetActive()) {
            String oldText = this.textInputWidget.getText().toString();
            int maxNumCharacters = this.textInputWidget.allowedLength;
            if ((this.textInputWidget.getInputType() & 2) == 2) {
                numbersOnly = true;
            } else {
                numbersOnly = false;
            }
            if ((this.textInputWidget.getInputType() & 131072) == 131072) {
                isMultiline = true;
            } else {
                isMultiline = false;
            }
            dismissTextWidget();
            showKeyboard(oldText, maxNumCharacters, false, numbersOnly, isMultiline);
        }
        for (ActivityListener listener : this.mActivityListeners) {
            listener.onResume();
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
        for (ActivityListener listener : this.mActivityListeners) {
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
        nativeUnregisterThis();
        nativeOnDestroy();
        super.onDestroy();
        System.exit(0);
        /**********************************
         * Bg music                       *
         **********************************/
    	if(bound) {
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
        this.mCallback = callback;
        try {
            startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), 1);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public void setFileDialogCallback(long callback) {
        this.mFileDialogCallback = callback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (ActivityListener listener : mActivityListeners) {
            listener.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode != 1) {
            return;
        }
        if (resultCode == -1 && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String[] filePathColumn = new String[]{"_data"};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    nativeOnPickImageSuccess(mCallback, cursor.getString(cursor.getColumnIndex(filePathColumn[0])));
                    mCallback = 0;
                    cursor.close();
                }
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

    public String MC_ScreenIsPresentInStack(String expectedName) {
        if (isPublishBuild()) {
            return "";
        }
        return nativeScreenIsPresentInStack(expectedName);
    }

    enum MessageConnectionStatus {
        NOTSET,
        CONNECTED,
        DISCONNECTED
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