package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;

import Microsoft.Telemetry.Base;

public class UTCTelemetry {
    public static final String UNKNOWNPAGE = "Unknown";

    public enum CallBackSources {
        Account,
        Ticket
    }

    public static void LogEvent(Base event) {
        try {
            Interop.getCll().log(event);
        } catch (NullPointerException e) {
            UTCLog.log("CLL not initialized.  Is null", new Object[0]);
        }
    }

    public static String getErrorScreen(ErrorScreen errorScreen) {
        switch (errorScreen) {
            case BAN:
                return "Banned error view";
            case CATCHALL:
                return "Generic error view";
            case CREATION:
                return "Create error view";
            case OFFLINE:
                return "Offline error view";
            default:
                return String.format("%sErrorScreen", new Object[]{"Unknown"});
        }
    }
}
