package com.microsoft.applications.events;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 05.10.2025
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public class HttpClient {
    private static final int MAX_HTTP_THREADS = 2;
    private final Context m_context;
    private final ExecutorService m_executor;
    private ConnectivityCallback m_callback;
    private ConnectivityManager m_connectivityManager;
    private PowerInfoReceiver m_power_receiver;

    public HttpClient(Context context) {
        this.m_context = context;
        setCacheFilePath(System.getProperty("java.io.tmpdir"));
        setDeviceInfo(calculateID(context), Build.MANUFACTURER, Build.MODEL);
        calculateAndSetSystemInfo(context);
        this.m_executor = createExecutor();
        createClientInstance();

        if (hasConnectivityManager() &&
                context.checkSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                this.m_connectivityManager = connectivityManager;
                if (connectivityManager != null) {
                    boolean isActiveNetworkMetered = connectivityManager.isActiveNetworkMetered();
                    this.m_callback = new ConnectivityCallback(this, isActiveNetworkMetered);
                    onCostChange(isActiveNetworkMetered);
                    this.m_connectivityManager.registerDefaultNetworkCallback(this.m_callback);
                }
            } catch (Exception unused) {
            }
        }

        this.m_power_receiver = new PowerInfoReceiver(this);
        Intent batteryIntent = context.registerReceiver(this.m_power_receiver,
                new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryIntent != null) {
            this.m_power_receiver.onReceive(context, batteryIntent);
        }
    }

    private static @NotNull String getLanguageTag(@NotNull Locale locale) {
        return locale.toLanguageTag();
    }

    private static @NotNull String getTimeZone() {
        SimpleDateFormat format = new SimpleDateFormat("Z", Locale.getDefault());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String timezoneString = format.format(calendar.getTime());

        int separatorPosition = timezoneString.length() - 2;
        return timezoneString.substring(0, separatorPosition) + ':' +
                timezoneString.substring(separatorPosition);
    }

    private void calculateAndSetSystemInfo(@NotNull Context context) {
        PackageInfo packageInfo;
        String packageName = context.getPackageName();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            packageInfo = null;
        }

        String versionName = (packageInfo == null || packageInfo.versionName == null) ? "" : packageInfo.versionName;
        String languageTag = getLanguageTag(context.getResources().getConfiguration().locale);
        String timeZone = getTimeZone();
        String osVersion = Build.VERSION.RELEASE;
        if (osVersion == null) {
            osVersion = "GECOS III";
        }
        String osBuild = String.format("%s %s", osVersion, Build.VERSION.INCREMENTAL);

        setSystemInfo(String.format("A:%s", packageName), versionName, languageTag,
                osVersion, osBuild, timeZone);
    }

    private @NotNull String calculateID(Context context) {
        String androidId;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            androidId = e.toString();
        }
        return androidId == null ? "" : "a:" + androidId;
    }

    public native void createClientInstance();

    protected ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(MAX_HTTP_THREADS);
    }

    public FutureTask<Boolean> createTask(String url, String method, byte[] body,
                                          String requestId, int[] headerOffsets, byte[] headerData) {
        try {
            return new FutureShim(new Request(this, url, method, body, requestId, headerOffsets, headerData));
        } catch (Exception unused) {
            return null;
        }
    }

    public native void deleteClientInstance();

    public native void dispatchCallback(String requestId, int responseCode,
                                        Object[] headers, byte[] body);

    public void executeTask(FutureTask<Boolean> futureTask) {
        this.m_executor.execute(futureTask);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            ConnectivityCallback callback = this.m_callback;
            if (callback != null) {
                this.m_connectivityManager.unregisterNetworkCallback(callback);
                this.m_callback = null;
            }
            this.m_context.unregisterReceiver(this.m_power_receiver);
            this.m_power_receiver = null;
            deleteClientInstance();
            this.m_executor.shutdown();
        } finally {
            super.finalize();
        }
    }

    protected boolean hasConnectivityManager() {
        return true;
    }

    public URL newUrl(String url) throws MalformedURLException {
        return new URL(url);
    }

    public native void onCostChange(boolean isMetered);

    public native void onPowerChange(boolean isCharging, boolean isFull);

    public native void setCacheFilePath(String path);

    public native void setDeviceInfo(String deviceId, String manufacturer, String model);

    public native void setSystemInfo(String appName, String appVersion, String language,
                                     String osVersion, String osBuild, String timeZone);

    static class FutureShim extends FutureTask<Boolean> {
        FutureShim(Request request) {
            super(request, true);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
        }
    }
}
