package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.util.Log;

import com.appboy.ui.inappmessage.jsinterface.AppboyInAppMessageHtmlUserJavascriptInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class CrashManager {
    private static final int MAX_CONCURRENT_UPLOADS = 4;
    private static final String FILENAME_SEQUENCE_SEPARATOR = "-";
    private String mCrashUploadURI = null;
    private SessionInfo mCurrentSession = null;
    private ConcurrentLinkedQueue<String> mDumpFileQueue = null;
    private String mDumpFilesPath = null;
    private String mExceptionUploadURI = null;
    private CrashManagerOwner mOwner = null;
    private Thread.UncaughtExceptionHandler mPreviousUncaughtExceptionHandler = null;
    private SentryEndpointConfig mSentryEndpointConfig = null;
    private String mSentrySessionParameters = null;
    private String mUserId = null;

    public CrashManager(CrashManagerOwner crashManagerOwner, String str, String str2, SentryEndpointConfig sentryEndpointConfig, SessionInfo sessionInfo) {
        mOwner = crashManagerOwner;
        mDumpFilesPath = str;
        mUserId = str2;
        mSentryEndpointConfig = sentryEndpointConfig;
        mCurrentSession = sessionInfo;
        mDumpFileQueue = new ConcurrentLinkedQueue<>();
        mSentrySessionParameters = getSentryParametersJSON(mCurrentSession);
        mCrashUploadURI = mSentryEndpointConfig.url + "/api/" + mSentryEndpointConfig.projectId + "/minidump/?sentry_key=" + mSentryEndpointConfig.publicKey;
        mExceptionUploadURI = mSentryEndpointConfig.url + "/api/" + mSentryEndpointConfig.projectId + "/store/?sentry_version=7&sentry_key=" + mSentryEndpointConfig.publicKey;
    }

    private static native String getSentryParameters(String str, String str2, String str3, String str4, String str5, String str6, int i);

    public static @Nullable String createLogFile(String str, String str2, String str3, String str4) {
        Date date = new Date();
        try {
            String uuid = UUID.randomUUID().toString();
            String str5 = str + "/" + uuid + ".faketrace";
            Log.d("ModdedPE", "CrashManager: Writing unhandled exception information to: " + str5);
            Log.d("ModdedPE", "CrashManager: Dump timestamp: " + str2);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(str5));
            bufferedWriter.write("Package: " + AppConstants.APP_PACKAGE + "\n");
            bufferedWriter.write("Version Code: " + String.valueOf(AppConstants.APP_VERSION) + "\n");
            bufferedWriter.write("Version Name: " + AppConstants.APP_VERSION_NAME + "\n");
            bufferedWriter.write("Android: " + AppConstants.ANDROID_VERSION + "\n");
            bufferedWriter.write("Manufacturer: " + AppConstants.PHONE_MANUFACTURER + "\n");
            bufferedWriter.write("Model: " + AppConstants.PHONE_MODEL + "\n");
            bufferedWriter.write("DeviceId: " + str3 + "\n");
            bufferedWriter.write("DeviceSessionId: " + str4 + "\n");
            bufferedWriter.write("Dmp timestamp: " + str2 + "\n");
            bufferedWriter.write("Upload Date: " + date + "\n");
            bufferedWriter.write("\n");
            bufferedWriter.write("MinidumpContainer");
            bufferedWriter.flush();
            bufferedWriter.close();
            return uuid + ".faketrace";
        } catch (Exception unused) {
            Log.w("ModdedPE", "CrashManager: failed to create accompanying log file");
            return null;
        }
    }

    public static HttpResponse uploadDumpAndLog(File file, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
        File file2 = new File(str2, str4);
        HttpResponse httpResponse = null;
        try {
            Log.i("ModdedPE", "CrashManager: uploading " + str3);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(str);
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("upload_file_minidump", new FileBody(file));
            Log.d("ModdedPE", "CrashManager: sentry parameters: " + str8);
            multipartEntity.addPart("sentry", new StringBody(str8));
            multipartEntity.addPart("log", new FileBody(file2));
            httpPost.setEntity(multipartEntity);
            httpResponse = defaultHttpClient.execute(httpPost);
            Log.d("ModdedPE", "CrashManager: Executed dump file upload with no exception: " + str3);
        } catch (Exception e) {
            Log.w("ModdedPE", "CrashManager: Error uploading dump file: " + str3);
            e.printStackTrace();
        } catch (Throwable th) {
            deleteWithLogging(file2);
            throw th;
        }
        deleteWithLogging(file2);
        return httpResponse;
    }

    private static void deleteWithLogging(@NotNull File file) {
        if (file.delete()) {
            Log.d("ModdedPE", "CrashManager: Deleted file " + file.getName());
            return;
        }
        Log.w("ModdedPE", "CrashManager: Couldn't delete file" + file.getName());
    }

    public static @NotNull String formatTimestamp(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    private static Date getFileTimestamp(String str, String str2) {
        Date date = new Date();
        try {
            return new Date(new File(str + "/" + str2).lastModified());
        } catch (Exception e) {
            Log.w("ModdedPE", "CrashManager: Error getting dump timestamp: " + str2);
            e.printStackTrace();
            return date;
        }
    }

    private static String[] searchForDumpFiles(String str, final String str2) {
        if (str != null) {
            Log.d("ModdedPE", "CrashManager: Searching for dump files in " + str);
            File file = new File(str + "/");
            if (file.mkdir() || file.exists()) {
                return file.list((file1, str1) -> str1.endsWith(str2));
            }
            return new String[0];
        }
        Log.e("ModdedPE", "CrashManager: Can't search for exception as file path is null.");
        return new String[0];
    }

    private String getSentryParametersJSON(@NotNull SessionInfo sessionInfo) {
        return getSentryParameters(mOwner.getCachedDeviceId(this), sessionInfo.sessionId, sessionInfo.buildId, sessionInfo.commitId, sessionInfo.branchId, sessionInfo.flavor, sessionInfo.appVersion);
    }

    public void installGlobalExceptionHandler() {
        mPreviousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
    }

    public void handleUncaughtException(Thread thread, @NotNull Throwable th) {
        Thread.setDefaultUncaughtExceptionHandler(mPreviousUncaughtExceptionHandler);
        Log.e("MCPE", "In handleUncaughtException()");
        try {
            JSONObject jSONObject = new JSONObject(mSentrySessionParameters);
            String replaceAll = UUID.randomUUID().toString().toLowerCase().replaceAll(FILENAME_SEQUENCE_SEPARATOR, "");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String format = simpleDateFormat.format(new Date());
            jSONObject.put("event_id", replaceAll);
            jSONObject.put("timestamp", format);
            jSONObject.put("logger", "na");
            jSONObject.put("platform", "java");
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("type", th.getClass().getName());
            jSONObject2.put(AppboyInAppMessageHtmlUserJavascriptInterface.JS_BRIDGE_ATTRIBUTE_VALUE, th.getMessage());
            JSONObject jSONObject3 = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            StackTraceElement[] stackTrace = th.getStackTrace();
            for (int length = stackTrace.length - 1; length >= 0; length--) {
                StackTraceElement stackTraceElement = stackTrace[length];
                JSONObject jSONObject4 = new JSONObject();
                jSONObject4.put("filename", stackTraceElement.getFileName());
                jSONObject4.put("function", stackTraceElement.getMethodName());
                jSONObject4.put("module", stackTraceElement.getClassName());
                jSONObject4.put("in_app", stackTraceElement.getClassName().startsWith("com.mojang"));
                if (stackTraceElement.getLineNumber() > 0) {
                    jSONObject4.put("lineno", stackTraceElement.getLineNumber());
                }
                jSONArray.put(jSONObject4);
            }
            jSONObject3.put("frames", jSONArray);
            jSONObject2.put("stacktrace", jSONObject3);
            jSONObject.put("exception", jSONObject2);
            String str = mDumpFilesPath + "/" + mCurrentSession.sessionId + ".except";
            Log.d("ModdedPE", "CrashManager: Writing unhandled exception information to: " + str);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(str));
            outputStreamWriter.write(jSONObject.toString(4));
            outputStreamWriter.close();
        } catch (JSONException e) {
            Log.e("ModdedPE", "JSON exception: " + e.toString());
        } catch (IOException e2) {
            Log.e("ModdedPE", "IO exception: " + e2.toString());
        }
        mPreviousUncaughtExceptionHandler.uncaughtException(thread, th);
    }

    private @Nullable HttpResponse uploadException(File file, String str) {
        try {
            Log.i("ModdedPE", "CrashManager: reading exception file at " + str);
            String str2 = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    str2 = str2 + readLine + "\n";
                } else {
                    Log.i("ModdedPE", "Sending exception by HTTP to " + mExceptionUploadURI);
                    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(mExceptionUploadURI);
                    httpPost.setEntity(new StringEntity(str2));
                    return defaultHttpClient.execute(httpPost);
                }
            }
        } catch (Exception e) {
            Log.w("ModdedPE", "CrashManager: Error uploading exception: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public void handlePreviousDumps() {
        Log.d("ModdedPE", "CrashManager: handlePreviousDumps: Device ID: " + mUserId);
        mDumpFileQueue.addAll(Arrays.asList(searchForDumpFiles(mDumpFilesPath, ".dmp")));
        mDumpFileQueue.addAll(Arrays.asList(searchForDumpFiles(mDumpFilesPath, ".except")));
        int min = Math.min(mDumpFileQueue.size(), 4);
        for (int i = 0; i < min; i++) {
            new Thread() {
                public void run() {
                    handlePreviousDumpsWorkerThread();
                }
            }.start();
        }
    }

    public void handlePreviousDumpsWorkerThread() {
        HttpResponse httpResponse;
        while (true) {
            String poll = mDumpFileQueue.poll();
            if (poll != null) {
                File file = new File(mDumpFilesPath, poll);
                boolean z = true;
                if (poll.endsWith(".dmp")) {
                    httpResponse = uploadMinidump(file, poll);
                } else {
                    httpResponse = uploadException(file, poll);
                }
                if (httpResponse != null) {
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        Log.i("ModdedPE", "Successfully uploaded dump file " + poll);
                    } else if (statusCode == 429) {
                        Header firstHeader = httpResponse.getFirstHeader("Retry-After");
                        if (firstHeader != null) {
                            int parseInt = Integer.parseInt(firstHeader.getValue());
                            Log.w("ModdedPE", "Received Too Many Requests response, retrying after " + parseInt + com.appboy.Constants.APPBOY_PUSH_SUMMARY_TEXT_KEY);
                            try {
                                Thread.sleep((long) (parseInt * 1000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            this.mDumpFileQueue.add(poll);
                            z = false;
                        } else {
                            Log.w("ModdedPE", "Received Too Many Requests response with no Retry-After header, so dropping event " + poll);
                        }
                    } else {
                        Log.e("ModdedPE", "Unrecognied HTTP response: \"" + httpResponse.getStatusLine() + "\", dropping event " + poll);
                    }
                } else {
                    Log.e("ModdedPE", "An error occurred uploading an event; dropping event " + poll);
                }
                if (z) {
                    deleteWithLogging(file);
                }
            } else {
                return;
            }
        }
    }

    private HttpResponse uploadMinidump(File file, @NotNull String str) {
        Log.d("ModdedPE", "CrashManager: Located this dump file: " + str);
        String replace = str.replace(".dmp", "");
        Date fileTimestamp = getFileTimestamp(mDumpFilesPath, str);
        SessionInfo findSessionInfoForCrash = mOwner.findSessionInfoForCrash(this, replace);
        HttpResponse httpResponse = null;
        if (findSessionInfoForCrash != null) {
            String createLogFile = createLogFile(mDumpFilesPath, formatTimestamp(fileTimestamp), mUserId, findSessionInfoForCrash.sessionId);
            if (createLogFile != null) {
                httpResponse = uploadDumpAndLog(file, mCrashUploadURI, mDumpFilesPath, str, createLogFile, findSessionInfoForCrash.gameVersionName, mUserId, findSessionInfoForCrash.sessionId, getSentryParametersJSON(findSessionInfoForCrash));
            } else {
                Log.e("ModdedPE", "CrashManager: Could not generate log file for previously crashed session " + replace);
            }
            findSessionInfoForCrash.crashTimestamp = fileTimestamp;
            mOwner.notifyCrashUploadCompleted(this, findSessionInfoForCrash);
        } else {
            Log.e("ModdedPE", "CrashManager: Could not locate session information for previously crashed session " + replace);
        }
        return httpResponse;
    }
}