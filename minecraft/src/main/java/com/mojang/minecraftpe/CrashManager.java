package com.mojang.minecraftpe;

import androidx.annotation.Nullable;
import org.jetbrains.annotations.Contract;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class CrashManager {
    private static native String nativeNotifyUncaughtException();

    public void installGlobalExceptionHandler() {
    }

    public String getCrashUploadURI() {
        return "http://localhost:1234/";
    }

    public String getExceptionUploadURI() {
        return "http://localhost:1234/";
    }

    @Nullable
    @Contract(pure = true)
    private String uploadCrashFile(String filePath, String sessionID, String sentryParametersJSON) {
        return null;
    }
}
