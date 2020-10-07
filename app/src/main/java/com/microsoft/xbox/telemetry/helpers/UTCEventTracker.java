package com.microsoft.xbox.telemetry.helpers;

public class UTCEventTracker {

    public static void callTrackWrapper(UTCEventDelegate delegate) {
        try {
            delegate.call();
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
        }
    }

    public static String callStringTrackWrapper(UTCStringEventDelegate delegate) {
        try {
            return delegate.call();
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            return null;
        }
    }

    public interface UTCEventDelegate {
        void call();
    }

    public interface UTCStringEventDelegate {
        String call();
    }
}
