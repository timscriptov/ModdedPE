package com.microsoft.xbox.toolkit;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEException extends Exception {
    private long errorCode;
    private boolean isHandled;
    private Object userObject;

    public XLEException(long j) {
        this(j, (String) null, (Throwable) null, (Object) null);
    }

    public XLEException(long j, String str) {
        this(j, str, (Throwable) null, (Object) null);
    }

    public XLEException(long j, Throwable th) {
        this(j, (String) null, th, (Object) null);
    }

    public XLEException(long j, String str, Throwable th) {
        this(j, (String) null, th, (Object) null);
    }

    public XLEException(long j, String str, Throwable th, Object obj) {
        super(str, th);
        this.errorCode = j;
        this.userObject = obj;
        this.isHandled = false;
    }

    public long getErrorCode() {
        return this.errorCode;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public boolean getIsHandled() {
        return this.isHandled;
    }

    public void setIsHandled(boolean z) {
        this.isHandled = z;
    }

    @SuppressLint("DefaultLocale")
    public @NotNull String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("XLEException ErrorCode: %d; ErrorMessage: %s \n\n", new Object[]{Long.valueOf(this.errorCode), getMessage()}));
        if (getCause() != null) {
            sb.append(String.format("\t Cause ErrorMessage: %s, StackTrace: ", new Object[]{getCause().toString()}));
            for (StackTraceElement stackTraceElement : getCause().getStackTrace()) {
                sb.append("\n\n \t " + stackTraceElement.toString());
            }
        }
        return sb.toString();
    }
}
