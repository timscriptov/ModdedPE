package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEUnhandledExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static XLEUnhandledExceptionHandler Instance = new XLEUnhandledExceptionHandler();
    private Thread.UncaughtExceptionHandler oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    public void uncaughtException(Thread thread, @NotNull Throwable ex) {
        String th = ex.toString();
        if (ex.getCause() != null) {
            printStackTrace("CAUSE STACK TRACE", ex.getCause());
        }
        printStackTrace("MAIN THREAD STACK TRACE", ex);
        oldExceptionHandler.uncaughtException(thread, ex);
    }

    private void printStackTrace(String initialText, @NotNull Throwable ex) {
        new Date();
        String text = "";
        for (StackTraceElement elem : ex.getStackTrace()) {
            text = text + String.format("\t%s\n", new Object[]{elem.toString()});
        }
    }
}
