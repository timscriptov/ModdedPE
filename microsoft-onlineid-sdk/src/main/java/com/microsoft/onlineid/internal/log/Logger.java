package com.microsoft.onlineid.internal.log;

import android.content.Context;
import com.microsoft.onlineid.internal.configuration.Settings;

public class Logger {
    private static LogInstance logInstance = new LogInstance();

    public static boolean shouldRedact() {
        return logInstance.shouldRedact();
    }

    public static String getLogTag() {
        return LogInstance.LogTag;
    }

    public static void info(String message) {
        logInstance.logMessage(message, 4, null);
    }

    public static void info(IRedactable message) {
        logInstance.logRedactedMessage(message, 4);
    }

    public static void info(String message, Throwable throwable) {
        logInstance.logMessage(message, 4, throwable);
    }

    public static void warning(String message) {
        logInstance.logMessage(message, 5, null);
    }

    public static void warning(IRedactable message) {
        logInstance.logRedactedMessage(message, 5);
    }

    public static void warning(String message, Throwable throwable) {
        logInstance.logMessage(message, 5, throwable);
    }

    public static void error(String message) {
        logInstance.logMessage(message, 6, null);
    }

    public static void error(IRedactable message) {
        logInstance.logRedactedMessage(message, 6);
    }

    public static void error(String message, Throwable throwable) {
        logInstance.logMessage(message, 6, throwable);
    }

    public static synchronized void initialize(Context applicationContext) {
        synchronized (Logger.class) {
            Settings settings = Settings.getInstance(applicationContext);
            if (logInstance == null) {
                logInstance = new LogInstance(Boolean.parseBoolean(settings.getSetting(Settings.IsRedactionEnabled)), Boolean.parseBoolean(settings.getSetting(Settings.IsLoggingEnabled)), true);
            } else {
                logInstance.setIsLoggingEnabled(Boolean.parseBoolean(settings.getSetting(Settings.IsLoggingEnabled)));
                logInstance.setIsRedactionEnable(Boolean.parseBoolean(settings.getSetting(Settings.IsRedactionEnabled)));
            }
        }
    }

    public static void setStackTraceLoggingOption(boolean enableStackTraceLogging) {
        logInstance.setIsStackTraceLoggingEnabled(enableStackTraceLogging);
    }

    static void setLogInstance(LogInstance loggerInstance) {
        logInstance = loggerInstance;
    }
}
