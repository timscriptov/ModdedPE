package com.microsoft.onlineid.internal.log;

import android.util.Log;
import com.microsoft.onlineid.internal.ui.ProgressView;

public class LogInstance {
    public static final String LogTag = "MSA";
    public static final int MaxLogLength = 4000;
    private boolean _isLoggingEnabled = false;
    private boolean _isRedactionEnabled = true;
    private boolean _isStackTraceLoggingEnabled = true;

    protected LogInstance(boolean isRedactionEnabled, boolean isLoggingEnabled, boolean isStackTraceLoggingEnabled) {
        this._isRedactionEnabled = isRedactionEnabled;
        this._isLoggingEnabled = isLoggingEnabled;
        this._isStackTraceLoggingEnabled = isStackTraceLoggingEnabled;
    }

    protected LogInstance() {
    }

    protected void setIsRedactionEnable(boolean isRedactionEnabled) {
        this._isRedactionEnabled = isRedactionEnabled;
    }

    protected void setIsLoggingEnabled(boolean isLoggingEnabled) {
        this._isLoggingEnabled = isLoggingEnabled;
    }

    protected void setIsStackTraceLoggingEnabled(boolean isStackTraceLoggingEnabled) {
        this._isStackTraceLoggingEnabled = isStackTraceLoggingEnabled;
    }

    protected void logInfo(String message) {
        Log.i(LogTag, message);
    }

    protected void logInfo(String message, Throwable throwable) {
        Log.i(LogTag, message, throwable);
    }

    protected void logWarning(String message) {
        Log.w(LogTag, message);
    }

    protected void logWarning(String message, Throwable throwable) {
        Log.w(LogTag, message, throwable);
    }

    protected void logError(String message) {
        Log.e(LogTag, message);
    }

    protected void logError(String message, Throwable throwable) {
        Log.e(LogTag, message, throwable);
    }

    protected boolean shouldRedact() {
        return this._isRedactionEnabled && this._isLoggingEnabled;
    }

    protected String getStackTraceInfo(String message, int stackTraceDepth) {
        if (!this._isStackTraceLoggingEnabled) {
            return message;
        }
        StringBuilder returnValue = new StringBuilder();
        try {
            returnValue.append(message);
            StackTraceElement[] stackTraceInfo = Thread.currentThread().getStackTrace();
            int index = 0;
            for (StackTraceElement stackTraceElement : stackTraceInfo) {
                index++;
                if (stackTraceElement.getMethodName().contains("getStackTrace")) {
                    index += stackTraceDepth;
                    returnValue.append(" ");
                    returnValue.append(stackTraceInfo[index].getMethodName());
                    returnValue.append("()@");
                    returnValue.append(stackTraceInfo[index].getFileName());
                    returnValue.append("_");
                    returnValue.append(stackTraceInfo[index].getLineNumber());
                    break;
                }
            }
        } catch (Exception e) {
            logWarning("Error in getStackTraceInfo", e);
        }
        return returnValue.toString();
    }

    protected void logMessage(String message, int loggingLevel, Throwable throwable) {
        logMessage(message, loggingLevel, throwable, 4);
    }

    protected void logMessage(String message, int loggingLevel, Throwable throwable, int stackTraceDepth) {
        if (message != null && this._isLoggingEnabled && Log.isLoggable(LogTag, loggingLevel)) {
            int len = message.length();
            int start = 0;
            while (start < len) {
                int end = Math.min(len, start + MaxLogLength);
                logMessageLevel(getStackTraceInfo(message.substring(start, end), stackTraceDepth), loggingLevel, throwable);
                start = end;
            }
        }
    }

    private void logMessageLevel(String message, int loggingLevel, Throwable throwable) {
        switch (loggingLevel) {
            case ProgressView.NumberOfDots /*5*/:
                if (throwable == null) {
                    logWarning(message);
                    return;
                } else {
                    logWarning(message, throwable);
                    return;
                }
            case 6:
                if (throwable == null) {
                    logError(message);
                    return;
                } else {
                    logError(message, throwable);
                    return;
                }
            default:
                if (throwable == null) {
                    logInfo(message);
                    return;
                } else {
                    logInfo(message, throwable);
                    return;
                }
        }
    }

    protected void logRedactedMessage(IRedactable redactableMessage, int loggingLevel) {
        if (redactableMessage == null) {
            return;
        }
        if (this._isRedactionEnabled) {
            logMessage(redactableMessage.getRedactedString(), loggingLevel, null, 4);
        } else {
            logMessage(redactableMessage.getUnredactedString(), loggingLevel, null, 4);
        }
    }
}
