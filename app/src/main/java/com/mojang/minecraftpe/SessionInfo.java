package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
        this.sessionId = null;
        this.buildId = null;
        this.commitId = null;
        this.branchId = null;
        this.flavor = null;
        this.gameVersionName = null;
        this.appVersion = 0;
        this.recordDate = null;
        this.crashTimestamp = null;
        this.sessionId = NOT_YET_CONFIGURED;
        this.buildId = NOT_YET_CONFIGURED;
        this.commitId = NOT_YET_CONFIGURED;
        this.branchId = NOT_YET_CONFIGURED;
        this.flavor = NOT_YET_CONFIGURED;
        this.gameVersionName = NOT_YET_CONFIGURED;
        this.recordDate = new Date();
    }

    public SessionInfo(String str, String str2, String str3, String str4, String str5, String str6, int i, Date date) {
        this.sessionId = null;
        this.buildId = null;
        this.commitId = null;
        this.branchId = null;
        this.flavor = null;
        this.gameVersionName = null;
        this.appVersion = 0;
        this.recordDate = null;
        this.crashTimestamp = null;
        this.sessionId = str;
        this.buildId = str2;
        this.commitId = str3;
        this.branchId = str4;
        this.flavor = str5;
        this.gameVersionName = str6;
        this.appVersion = i;
        this.recordDate = date;
    }

    public static SessionInfo fromString(String str) {
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

    public static SimpleDateFormat getDateFormat() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    public void setContents(Context context, String sessionId, String buildId, String commitId, String branchId, String flavor) {
        this.sessionId = sessionId;
        this.buildId = buildId;
        this.commitId = commitId;
        this.branchId = branchId;
        this.flavor = flavor;
        updateJavaConstants(context);
    }

    public void updateJavaConstants(Context context) {
        this.appVersion = AppConstants.APP_VERSION;
        try {
            this.gameVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            this.gameVersionName = "Not found";
        }
    }

    public @NotNull String toString() {
        return this.sessionId + ";" + this.buildId + ";" + this.commitId + ";" + this.branchId + ";" + this.flavor + ";" + this.gameVersionName + ";" + this.appVersion + ";" + getDateFormat().format(this.recordDate);
    }
}