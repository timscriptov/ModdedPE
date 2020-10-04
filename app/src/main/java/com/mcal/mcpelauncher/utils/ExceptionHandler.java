package com.mcal.mcpelauncher.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.mcal.mcpelauncher.activities.ExceptionActivity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity mContext;

    @Contract(pure = true)
    public ExceptionHandler(Activity activity) {
        mContext = activity;
    }

    public void uncaughtException(Thread thread, @NotNull Throwable th) {
        Writer stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("************ APPLICATION ERROR ************\n\n");
        stringBuilder.append(stringWriter.toString());
        stringBuilder.append("\n************ DEVICE INFORMATION ***********\n");
        stringBuilder.append("Brand: ");
        stringBuilder.append(Build.BRAND);
        stringBuilder.append("\n");
        stringBuilder.append("Device: ");
        stringBuilder.append(Build.DEVICE);
        stringBuilder.append("\n");
        stringBuilder.append("Model: ");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append("\n");
        stringBuilder.append("Id: ");
        stringBuilder.append(Build.ID);
        stringBuilder.append("\n");
        stringBuilder.append("Product: ");
        stringBuilder.append(Build.PRODUCT);
        stringBuilder.append("\n");
        stringBuilder.append("\n************ FIRMWARE ************\n");
        stringBuilder.append("SDK: ");
        stringBuilder.append(Build.VERSION.SDK);
        stringBuilder.append("\n");
        stringBuilder.append("Release: ");
        stringBuilder.append(Build.VERSION.RELEASE);
        stringBuilder.append("\n");
        stringBuilder.append("Incremental: ");
        stringBuilder.append(Build.VERSION.INCREMENTAL);
        stringBuilder.append("\n");
        try {
            Intent intent = new Intent(mContext, ExceptionActivity.class);
            intent.putExtra("error", stringBuilder.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}