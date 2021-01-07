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
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Interop {
    public static final String TAG = Interop.class.getSimpleName();
    private static final String DNET_SCOPE = "open-user.auth.dnet.xboxlive.com";
    private static final String PACKAGE_NAME_TO_REMOVE = "com.microsoft.onlineid.sample";
    private static final String POLICY = "mbi_ssl";
    private static final String PROD_SCOPE = "open-user.auth.xboxlive.com";
    private static Context s_context;

    public static native boolean deinitializeInterop();

    public static native boolean initializeInterop(Context context);

    private static native void notificiation_registration_callback(String str);

    public static @NotNull String getSystemProxy() {
        String property;
        String property2 = System.getProperty("http.proxyHost");
        if (property2 == null || (property = System.getProperty("http.proxyPort")) == null) {
            return "";
        }
        String str = "http://" + property2 + ":" + property;
        Log.i(TAG, str);
        return str;
    }

    public static @NotNull String getLocale() {
        String locale = Locale.getDefault().toString();
        String str = TAG;
        Log.i(str, "locale is: " + locale);
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

    public static @NotNull String GetLocalStoragePath(@NotNull Context context) {
        return context.getFilesDir().getPath();
    }

    public static Context getApplicationContext() {
        return s_context;
    }

    public static void NotificationRegisterCallback(String str) {
        String str2 = TAG;
        Log.i(str2, "NotificationRegisterCallback, token:" + str);
        notificiation_registration_callback(str);
    }

    public static void RegisterWithGNS(Context context) {
        Log.i("XSAPI.Android", "trying to register..");
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                Log.d(Interop.TAG, "Got Firebase id:" + instanceIdResult.getId());
                Log.d(Interop.TAG, "Got Firebase token:" + instanceIdResult.getToken());
                Interop.NotificationRegisterCallback(instanceIdResult.getToken());
            }).addOnFailureListener(exc -> {
                Log.d(Interop.TAG, "Gettting Firebase token failed, message:" + exc.getMessage());
            });
        } catch (Exception e) {
            Log.e(Interop.TAG, "Gettting Firebase instance failed, message:" + e.getMessage());
        }
    }

    public enum AuthFlowScreenStatus {
        NO_ERROR(0),
        ERROR_USER_CANCEL(1),
        PROVIDER_ERROR(2);

        private final int id;

        private AuthFlowScreenStatus(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }
    }

    public enum ErrorType {
        BAN(0),
        CREATION(1),
        OFFLINE(2),
        CATCHALL(3);

        private final int id;

        private ErrorType(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }
    }

    public enum ErrorStatus {
        TRY_AGAIN(0),
        CLOSE(1);

        private final int id;

        private ErrorStatus(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }
    }

    public interface ErrorCallback {
        void onError(int i, int i2, String str);
    }

    public interface EventInitializationCallback extends ErrorCallback {
        void onSuccess();
    }
}
