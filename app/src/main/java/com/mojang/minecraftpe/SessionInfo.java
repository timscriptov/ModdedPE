package com.mojang.minecraftpe;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SessionInfo implements Serializable {
    private static final String NOT_YET_CONFIGURED = "Not yet configured";
    private static final String SEP = ";";
    public int appVersion;
    public String branchId;
    public String buildId;
    public String commitId;
    public transient Date crashTimestamp;
    public String flavor;
    public String gameVersionName;
    public Date recordDate;
    public String sessionId;

    public SessionInfo() {
        sessionId = null;
        buildId = null;
        commitId = null;
        branchId = null;
        flavor = null;
        gameVersionName = null;
        appVersion = 0;
        recordDate = null;
        crashTimestamp = null;
        sessionId = NOT_YET_CONFIGURED;
        buildId = NOT_YET_CONFIGURED;
        commitId = NOT_YET_CONFIGURED;
        branchId = NOT_YET_CONFIGURED;
        flavor = NOT_YET_CONFIGURED;
        gameVersionName = NOT_YET_CONFIGURED;
        recordDate = new Date();
    }

    public SessionInfo(String str, String str2, String str3, String str4, String str5, String str6, int i, Date date) {
        sessionId = null;
        buildId = null;
        commitId = null;
        branchId = null;
        flavor = null;
        gameVersionName = null;
        appVersion = 0;
        recordDate = null;
        crashTimestamp = null;
        sessionId = str;
        buildId = str2;
        commitId = str3;
        branchId = str4;
        flavor = str5;
        gameVersionName = str6;
        appVersion = i;
        recordDate = date;
    }

    public void setContents(Context context, String str, String str2, String str3, String str4, String str5) {
        sessionId = str;
        buildId = str2;
        commitId = str3;
        branchId = str4;
        flavor = str5;
        updateJavaConstants(context);
    }

    public void updateJavaConstants(@NotNull Context context) {
        appVersion = AppConstants.APP_VERSION;
        try {
            gameVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            gameVersionName = "Not found";
        }
    }

    public static @NotNull SessionInfo fromString(String str) {
        SessionInfo sessionInfo = new SessionInfo();
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Empty SessionInfo string");
        }
        String[] split = str.split(";");
        if (split.length == 8) {
            sessionInfo.sessionId = split[0];
            sessionInfo.buildId = split[1];
            sessionInfo.commitId = split[2];
            sessionInfo.branchId = split[3];
            sessionInfo.flavor = split[4];
            sessionInfo.gameVersionName = split[5];
            try {
                sessionInfo.appVersion = Integer.parseInt(split[6]);
                Date parse = getDateFormat().parse(split[7], new ParsePosition(0));
                sessionInfo.recordDate = parse;
                if (parse != null) {
                    return sessionInfo;
                }
                throw new IllegalArgumentException("Failed to parse date/time in SessionInfo string '" + str + "'");
            } catch (NumberFormatException unused) {
                throw new IllegalArgumentException("Failed to convert app version '" + split[6] + "' into an integer");
            }
        } else {
            throw new IllegalArgumentException("Invalid SessionInfo string '" + str + "', must be 8 parts split by '" + ";" + "'");
        }
    }

    public String toString() {
        return sessionId + ";" + buildId + ";" + commitId + ";" + branchId + ";" + flavor + ";" + gameVersionName + ";" + appVersion + ";" + getDateFormat().format(recordDate);
    }

    public static @NotNull SimpleDateFormat getDateFormat() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }
}