package com.microsoft.xbox.toolkit;

import android.annotation.SuppressLint;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEException extends Exception {
    private long errorCode;
    private boolean isHandled;
    private Object userObject;

    public XLEException(long errorCode2) {
        this(errorCode2, null, null, null);
    }

    public XLEException(long errorCode2, String message) {
        this(errorCode2, message, null, null);
    }

    public XLEException(long errorCode2, Throwable innerException) {
        this(errorCode2, null, innerException, null);
    }

    public XLEException(long errorCode2, String message, Throwable innerException) {
        this(errorCode2, null, innerException, null);
    }

    public XLEException(long errorCode2, String message, Throwable innerException, Object userObject2) {
        super(message, innerException);
        errorCode = errorCode2;
        userObject = userObject2;
        isHandled = false;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public Object getUserObject() {
        return userObject;
    }

    public boolean getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(boolean isHandled2) {
        isHandled = isHandled2;
    }

    @SuppressLint("DefaultLocale")
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("XLEException ErrorCode: %d; ErrorMessage: %s \n\n", new Object[]{Long.valueOf(errorCode), getMessage()}));
        if (getCause() != null) {
            builder.append(String.format("\t Cause ErrorMessage: %s, StackTrace: ", new Object[]{getCause().toString()}));
            StackTraceElement[] stackTrace = getCause().getStackTrace();
            int length = stackTrace.length;
            for (int i = 0; i < length; i++) {
                builder.append("\n\n \t " + stackTrace[i].toString());
            }
        }
        return builder.toString();
    }
}