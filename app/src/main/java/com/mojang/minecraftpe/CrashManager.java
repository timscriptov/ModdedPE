package com.mojang.minecraftpe;

import android.util.Pair;

import org.apache.http.HttpResponse;

import java.io.File;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class CrashManager {
    public static final String FILENAME_SEQUENCE_SEPARATOR = "-";
    public static final String TIMESTAMP = "timestamp";
    private String mCrashDumpFolder = null;
    private String mCrashUploadURI = null;
    private String mCrashUploadURIWithSentryKey = null;
    private String mCurrentSessionId = null;
    private String mExceptionUploadURI = null;
    private Thread.UncaughtExceptionHandler mPreviousUncaughtExceptionHandler = null;


    public CrashManager(String str, String str2, SentryEndpointConfig sentryEndpointConfig) {
        /*this.mCrashDumpFolder = str;
        this.mCurrentSessionId = str2;
        this.mCrashUploadURI = sentryEndpointConfig.url + "/api/" + sentryEndpointConfig.projectId + "/minidump/";
        StringBuilder sb = new StringBuilder();
        sb.append(this.mCrashUploadURI);
        sb.append("?sentry_key=");
        sb.append(sentryEndpointConfig.publicKey);
        this.mCrashUploadURIWithSentryKey = sb.toString();
        this.mExceptionUploadURI = sentryEndpointConfig.url + "/api/" + sentryEndpointConfig.projectId + "/store/?sentry_version=7&sentry_key=" + sentryEndpointConfig.publicKey;*/
    }

    private static native String nativeNotifyUncaughtException();

    private static Pair<HttpResponse, String> uploadDump(File file, String str, String str2, String str3) {
        HttpResponse httpResponse = null;
        String httpResponse2 = null;
        /*try {
            Log.i("MCPE", "CrashManager: uploading " + file.getPath());
            Log.d("MCPE", "CrashManager: sentry parameters: " + str3);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(str);
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("upload_file_minidump", new FileBody(file));
            multipartEntity.addPart("sentry", new StringBody(str3));
            httpPost.setEntity(multipartEntity);
            httpResponse = defaultHttpClient.execute(httpPost);
            try {
                Log.d("MCPE", "CrashManager: Executed dump file upload with no exception: " + file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.w("MCPE", "CrashManager: Error uploading dump file: " + file.getPath());
            e.printStackTrace();
            return new Pair<>(httpResponse, httpResponse2);
        }*/
        return new Pair<>(httpResponse, httpResponse2);
    }

    public void installGlobalExceptionHandler() {
        /*this.mPreviousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(CrashManager.this::handleUncaughtException);*/
    }

    public String getCrashUploadURI() {
        return this.mCrashUploadURI;
    }

    public String getExceptionUploadURI() {
        return this.mExceptionUploadURI;
    }

    private void handleUncaughtException(Thread thread, Throwable th) {
        /*Thread.setDefaultUncaughtExceptionHandler(this.mPreviousUncaughtExceptionHandler);
        Log.e("MCPE", "In handleUncaughtException()");
        try {
            JSONObject jSONObject = new JSONObject(nativeNotifyUncaughtException());
            Object replaceAll = UUID.randomUUID().toString().toLowerCase().replaceAll(FILENAME_SEQUENCE_SEPARATOR, "");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Object format = simpleDateFormat.format(new Date());
            jSONObject.put("event_id", replaceAll);
            jSONObject.put(TIMESTAMP, format);
            jSONObject.put("logger", "na");
            jSONObject.put("platform", "java");
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("type", th.getClass().getName());
            jSONObject2.put("value", th.getMessage());
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
            String str = this.mCrashDumpFolder + "/" + this.mCurrentSessionId + ".except";
            Log.d("MCPE", "CrashManager: Writing unhandled exception information to: " + str);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(str));
            outputStreamWriter.write(jSONObject.toString(4));
            outputStreamWriter.close();
        } catch (JSONException e) {
            Log.e("MCPE", "JSON exception: " + e.toString());
        } catch (IOException e2) {
            Log.e("MCPE", "IO exception: " + e2.toString());
        }*/
        this.mPreviousUncaughtExceptionHandler.uncaughtException(thread, th);
    }

    private Pair<HttpResponse, String> uploadException(File file) {
        String str = "";
        HttpResponse httpResponse = null;
        /*try {
            Log.i("MCPE", "CrashManager: reading exception file at " + file.getPath());
            String str2 = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                str2 = str2 + readLine + "\n";
            }
            Log.i("MCPE", "Sending exception by HTTP to " + this.mExceptionUploadURI);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(this.mExceptionUploadURI);
            httpPost.setEntity(new StringEntity(str2));
            httpResponse = defaultHttpClient.execute(httpPost);
            str = null;
        } catch (Exception e) {
            Log.w("MCPE", "CrashManager: Error uploading exception: " + e.toString());
            e.printStackTrace();
            str = e.getMessage();
        }*/
        return new Pair<>(httpResponse, str);
    }

    private String uploadCrashFile(String str, String str2, String str3) {
        /*Pair<HttpResponse, String> pair;
        File file = new File(str);
        String str4 = null;
        while (true) {
            if (str.endsWith(".dmp")) {
                pair = uploadDump(file, this.mCrashUploadURIWithSentryKey, str2, str3);
            } else {
                pair = uploadException(file);
            }
            if (pair.first != null) {
                int statusCode = ((HttpResponse) pair.first).getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    Log.i("MCPE", "Successfully uploaded dump file " + str);
                    return str4;
                } else if (statusCode == 429) {
                    Header firstHeader = ((HttpResponse) pair.first).getFirstHeader("Retry-After");
                    if (firstHeader != null) {
                        int parseInt = Integer.parseInt(firstHeader.getValue());
                        Log.w("MCPE", "Received Too Many Requests response, retrying after " + parseInt + com.appboy.Constants.APPBOY_PUSH_SUMMARY_TEXT_KEY);
                        try {
                            Thread.sleep((long) (parseInt * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.w("MCPE", "Received Too Many Requests response with no Retry-After header, so dropping event " + str);
                        str4 = "TooManyRequestsNoRetryAfter";
                    }
                } else {
                    Log.e("MCPE", "Unrecognied HTTP response: \"" + ((HttpResponse) pair.first).getStatusLine() + "\", dropping event " + str);
                    str4 = "HTTP: " + ((HttpResponse) pair.first).getStatusLine().toString();
                }
            } else {
                Log.e("MCPE", "An error occurred uploading an event; dropping event " + str);
                str4 = (String) pair.second;
            }
        }*/
        return "";
    }
}