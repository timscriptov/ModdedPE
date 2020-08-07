package com.mojang.minecraftpe;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SessionInfo {
    public String buildId = null;
    public Date recordDate = null;
    public String sessionId = null;
    public boolean valid = false;

    public SessionInfo() {
    }

    public SessionInfo(String aSessionId, String aBuildId) {
        this.sessionId = aSessionId;
        this.buildId = aBuildId;
        this.recordDate = new Date();
        this.valid = true;
    }

    public SessionInfo(String aSessionId, String aBuildId, Date aRecordDate) {
        this.sessionId = aSessionId;
        this.buildId = aBuildId;
        this.recordDate = aRecordDate;
        this.valid = true;
    }

    @NotNull
    public static SessionInfo fromString(String s) {
        return fromString(s, getDateFormat());
    }

    @NotNull
    public static SessionInfo fromString(String s, SimpleDateFormat dateFormat) {
        SessionInfo result = new SessionInfo();
        if (!(s == null || s.length() == 0)) {
            String[] parts = s.split(";");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid SessionInfo string '" + s + "', must be 3 parts split by ';'");
            }
            result.sessionId = parts[0];
            result.buildId = parts[1];
            result.recordDate = dateFormat.parse(parts[2], new ParsePosition(0));
            if (result.recordDate == null) {
                throw new IllegalArgumentException("Failed to parse date/time in SessionInfo string '" + s + "'");
            }
            result.valid = true;
        }
        return result;
    }

    @NotNull
    public static SimpleDateFormat getDateFormat() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat result = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
        result.setTimeZone(TimeZone.getTimeZone("UTC"));
        return result;
    }

    public String toString() {
        return toString(getDateFormat());
    }

    public String toString(SimpleDateFormat dateFormat) {
        return this.valid ? this.sessionId + ";" + this.buildId + ";" + dateFormat.format(this.recordDate) : "<null>";
    }
}