package com.microsoft.xbox.telemetry.helpers;

import android.util.Log;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class UTCLog {
    static final String UTCLOGTAG = "UTCLOGGING";

    public static void log(String message, Object... args) {
        try {
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            if (ste.length > 3) {
                String method = ste[3].getMethodName();
                Log.d(UTCLOGTAG, String.format(String.format("%s: ", new Object[]{method}) + message, args));
                return;
            }
            Log.d(UTCLOGTAG, String.format(message, args));
        } catch (Exception exception) {
            UTCError.trackException(exception, "UTCLog.log");
            if (exception.getMessage().equals("Format specifier: s")) {
                Log.e(UTCLOGTAG, exception.getMessage());
            }
            Log.e(UTCLOGTAG, exception.getMessage());
        }
    }
}
