package com.balsikandar.crashreporter.utils;

import org.jetbrains.annotations.NotNull;

public class CrashReporterExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler exceptionHandler;

    public CrashReporterExceptionHandler() {
        this.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NotNull Thread thread, @NotNull Throwable throwable) {

        CrashUtil.saveCrashReport(throwable);

        exceptionHandler.uncaughtException(thread, throwable);
    }
}
