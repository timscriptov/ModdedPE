package com.microsoft.xbox.idp.interop;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Interop {
    public static final String TAG = "Interop";
    private static final String DNET_SCOPE = "open-user.auth.dnet.xboxlive.com";
    private static final String PACKAGE_NAME_TO_REMOVE = "com.microsoft.onlineid.sample";
    private static final String POLICY = "mbi_ssl";
    private static final String PROD_SCOPE = "open-user.auth.xboxlive.com";
    private static Context s_context;

    public static native boolean deinitializeInterop();

    public static native boolean initializeInterop(Context context);

    private static native void notificiation_registration_callback(String str);

    @NotNull
    public static String getSystemProxy() {
        String proxyPort;
        String proxyAddress = System.getProperty("http.proxyHost");
        if (proxyAddress == null || (proxyPort = System.getProperty("http.proxyPort")) == null) {
            return "";
        }
        String fullProxy = "http://" + proxyAddress + ":" + proxyPort;
        Log.i(TAG, fullProxy);
        return fullProxy;
    }

    @NotNull
    public static String getLocale() {
        String locale = Locale.getDefault().toString();
        Log.i(TAG, "locale is: " + locale);
        return locale;
    }

    @NotNull
    public static String ReadConfigFile(@NotNull Context context) {
        s_context = context;
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
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toString();
    }

    @NotNull
    public static String GetLocalStoragePath(@NotNull Context context) {
        return context.getFilesDir().getPath();
    }

    public static Context getApplicationContext() {
        return s_context;
    }

    public static void NotificationRegisterCallback(String token) {
        Log.i(TAG, "NotificationRegisterCallback, token:" + token);
        notificiation_registration_callback(token);
    }

    public static void RegisterWithGNS(Context context) {
        Log.i("XSAPI.Android", "trying to register..");
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(result -> {
                Log.d(Interop.TAG, "Got Firebase id:" + result.getId());
                Log.d(Interop.TAG, "Got Firebase token:" + result.getToken());
                Interop.NotificationRegisterCallback(result.getToken());
            }).addOnFailureListener(e -> Log.d(Interop.TAG, "Gettting Firebase token failed, message:" + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "Gettting Firebase instance failed, message:" + e.getMessage());
        }
    }

    public enum AuthFlowScreenStatus {
        NO_ERROR(0),
        ERROR_USER_CANCEL(1),
        PROVIDER_ERROR(2);

        private final int mId;

        private AuthFlowScreenStatus(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public enum ErrorType {
        BAN(0),
        CREATION(1),
        OFFLINE(2),
        CATCHALL(3);

        private final int mId;

        private ErrorType(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public enum ErrorStatus {
        TRY_AGAIN(0),
        CLOSE(1);

        private final int mId;

        private ErrorStatus(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public interface ErrorCallback {
        void onError(int i, int i2, String str);
    }

    public interface EventInitializationCallback extends ErrorCallback {
        void onSuccess();
    }
}