package com.mojang.minecraftpe;

import android.annotation.SuppressLint;

import com.microsoft.aad.adal.AuthenticationConstants;

import org.jetbrains.annotations.NotNull;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SessionInfo {
    public String buildId = null;
    public Date recordDate = null;
    public String sessionId = null;
    public boolean valid = false;

    public String toString() {
        return toString(getDateFormat());
    }

    public String toString(SimpleDateFormat dateFormat) {
        return valid ? sessionId + AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER + buildId + AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER + dateFormat.format(recordDate) : "<null>";
    }

    public SessionInfo() {
    }

    public SessionInfo(String aSessionId, String aBuildId) {
        sessionId = aSessionId;
        buildId = aBuildId;
        recordDate = new Date();
        valid = true;
    }

    public SessionInfo(String aSessionId, String aBuildId, Date aRecordDate) {
        sessionId = aSessionId;
        buildId = aBuildId;
        recordDate = aRecordDate;
        valid = true;
    }

    @NotNull
    public static SessionInfo fromString(String s) {
        return fromString(s, getDateFormat());
    }

    @NotNull
    public static SessionInfo fromString(String s, SimpleDateFormat dateFormat) {
        SessionInfo result = new SessionInfo();
        if (!(s == null || s.length() == 0)) {
            String[] parts = s.split(AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
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
        SimpleDateFormat result = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
        result.setTimeZone(TimeZone.getTimeZone("UTC"));
        return result;
    }
}