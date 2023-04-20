package com.mojang.minecraftpe;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.jetbrains.annotations.Contract;

import java.io.File;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class CrashManager {
    private String mCrashDumpFolder;
    private String mCrashUploadURI;
    private String mCrashUploadURIWithSentryKey;
    private String mCurrentSessionId;
    private String mExceptionUploadURI;
    private final Thread.UncaughtExceptionHandler mPreviousUncaughtExceptionHandler = null;

    private static native String nativeNotifyUncaughtException();

    public CrashManager(String crashDumpFolder, String currentSessionId, @NonNull SentryEndpointConfig sentryEndpointConfig) {
        this.mCrashUploadURI = null;
        this.mCrashUploadURIWithSentryKey = null;
        this.mExceptionUploadURI = null;
        this.mCrashDumpFolder = null;
        this.mCurrentSessionId = null;
        this.mCrashDumpFolder = crashDumpFolder;
        this.mCurrentSessionId = currentSessionId;
        this.mCrashUploadURI = sentryEndpointConfig.url + "/api/" + sentryEndpointConfig.projectId + "/minidump/";
        this.mCrashUploadURIWithSentryKey = mCrashUploadURI + "?sentry_key=" + sentryEndpointConfig.publicKey;
        this.mExceptionUploadURI = sentryEndpointConfig.url + "/api/" + sentryEndpointConfig.projectId + "/store/?sentry_version=7&sentry_key=" + sentryEndpointConfig.publicKey;
    }

    public void installGlobalExceptionHandler() {
    }

    public String getCrashUploadURI() {
        return this.mCrashUploadURI;
    }

    public String getExceptionUploadURI() {
        return this.mExceptionUploadURI;
    }

    public void handleUncaughtException(Thread t, Throwable e) {
        mPreviousUncaughtExceptionHandler.uncaughtException(t, e);
    }

    @NonNull
    @Contract("_ -> new")
    private Pair<HttpResponse, String> uploadException(File fp) {
        return new Pair<>(null, null);
    }

    @Nullable
    @Contract(pure = true)
    private String uploadCrashFile(String filePath, String sessionID, String sentryParametersJSON) {
        return null;
    }

    @NonNull
    @Contract("_, _, _, _ -> new")
    private static Pair<HttpResponse, String> uploadDump(File dumpFile, final String crashUploadURI, final String sessionID, final String sentryParametersJSON) {
        return new Pair<>(null, null);
    }
}