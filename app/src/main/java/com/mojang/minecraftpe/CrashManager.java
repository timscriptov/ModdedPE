package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class CrashManager {
    private static native String getSentryParameters(String str, String str2, int i);

    @NotNull
    public static Map<String, Date> handlePreviousDumps(String dumpFilesPath, String crashUploadURI, String gameVersion, String userId) {
        Log.d("ModdedPE", "CrashManager: handlePreviousDumps: Device ID: " + userId);
        Map<String, Date> crashReportsSent = new HashMap<>();
        for (String dumpFilename : searchForDumpFiles(dumpFilesPath)) {
            String sessionID = dumpFilename.replace(".dmp", "");
            String sentryParametersJSON = getSentryParameters(userId, sessionID, AppConstants.APP_VERSION);
            Log.d("ModdedPE", "CrashManager: Located this dump file: " + dumpFilename);
            Date timestamp = getFileTimestamp(dumpFilesPath, dumpFilename);
            String logFilename = createLogFile(dumpFilesPath, formatTimestamp(timestamp), userId, sessionID);
            if (logFilename != null) {
                crashReportsSent.put(sessionID, timestamp);
                uploadDumpAndLogAsync(crashUploadURI, dumpFilesPath, dumpFilename, logFilename, gameVersion, userId, sessionID, sentryParametersJSON);
            }
        }
        return crashReportsSent;
    }

    public static String createLogFile(String dumpFilesPath, String formattedDumpTimestamp, String userId, String lastDeviceSessionId) {
        Date now = new Date();
        try {
            String filename = UUID.randomUUID().toString();
            String path = dumpFilesPath + "/" + filename + ".faketrace";
            Log.d("ModdedPE", "CrashManager: Writing unhandled exception information to: " + path);
            Log.d("ModdedPE", "CrashManager: Dump timestamp: " + formattedDumpTimestamp);
            BufferedWriter write = new BufferedWriter(new FileWriter(path));
            write.write("Package: " + AppConstants.APP_PACKAGE + "\n");
            write.write("Version Code: " + String.valueOf(AppConstants.APP_VERSION) + "\n");
            write.write("Version Name: " + AppConstants.APP_VERSION_NAME + "\n");
            write.write("Android: " + AppConstants.ANDROID_VERSION + "\n");
            write.write("Manufacturer: " + AppConstants.PHONE_MANUFACTURER + "\n");
            write.write("Model: " + AppConstants.PHONE_MODEL + "\n");
            write.write("DeviceId: " + userId + "\n");
            write.write("DeviceSessionId: " + lastDeviceSessionId + "\n");
            write.write("Dmp timestamp: " + formattedDumpTimestamp + "\n");
            write.write("Upload Date: " + now + "\n");
            write.write("\n");
            write.write("MinidumpContainer");
            write.flush();
            write.close();
            return filename + ".faketrace";
        } catch (Exception e) {
            Log.w("MinecraftPlatform", "CrashManager: failed to create accompanying log file");
            return null;
        }
    }

    public static void uploadDumpAndLogAsync(String crashUploadURI, String dumpFilesPath, String dumpFilename, String logFilename, String gameVersion, String userId, String lastDeviceSessionId, String sentryParametersJSON) {
        final String str = dumpFilesPath;
        final String str2 = dumpFilename;
        final String str3 = logFilename;
        final String str4 = crashUploadURI;
        final String str5 = sentryParametersJSON;
        new Thread() {
            public void run() {
                File dumpFile = new File(str, str2);
                File logFile = new File(str, str3);
                try {
                    Log.i("ModdedPE", "CrashManager: uploading " + str2);
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(str4);
                    MultipartEntity entity = new MultipartEntity();
                    entity.addPart("upload_file_minidump", new FileBody(dumpFile));
                    Log.d("ModdedPE", "CrashManager: sentry parameters: " + str5);
                    entity.addPart("sentry", new StringBody(str5));
                    entity.addPart("log", new FileBody(logFile));
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
                    Log.d("ModdedPE", "CrashManager: Executed dump file upload with no exception: " + str2);
                } catch (Exception e) {
                    Log.w("ModdedPE", "CrashManager: Error uploading dump file: " + str2);
                    e.printStackTrace();
                } finally {
                    CrashManager.deleteWithLogging(dumpFile);
                    CrashManager.deleteWithLogging(logFile);
                }
                Log.v("ModdedPE", "CrashManager: exiting upload thread");
            }
        }.start();
        Log.v("ModdedPE", "CrashManager: upload thread started");
    }

    public static void deleteWithLogging(@NotNull File toBeDeleted) {
        if (toBeDeleted.delete()) {
            Log.d("ModdedPE", "CrashManager: Deleted file " + toBeDeleted.getName());
        } else {
            Log.w("ModdedPE", "CrashManager: Couldn't delete file" + toBeDeleted.getName());
        }
    }

    @NotNull
    public static String formatTimestamp(Date timestamp) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(timestamp);
    }

    private static Date getFileTimestamp(String dumpFilesPath, String filename) {
        Date date = new Date();
        try {
            return new Date(new File(dumpFilesPath + "/" + filename).lastModified());
        } catch (Exception e) {
            Log.w("ModdedPE", "CrashManager: Error getting dump timestamp: " + filename);
            e.printStackTrace();
            return date;
        }
    }

    private static String[] searchForDumpFiles(String dumpFilesPath) {
        if (dumpFilesPath != null) {
            Log.d("ModdedPE", "CrashManager: Searching for dump files in " + dumpFilesPath);
            File dir = new File(dumpFilesPath + "/");
            if (dir.mkdir() || dir.exists()) {
                return dir.list((dir1, name) -> name.endsWith(".dmp"));
            }
            return new String[0];
        }
        Log.e("ModdedPE", "CrashManager: Can't search for exception as file path is null.");
        return new String[0];
    }
}