package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.idp.ui.ErrorActivity;
import com.microsoft.xbox.telemetry.utc.ClientError;
import com.microsoft.xbox.telemetry.utc.model.UTCNames;

public class UTCError {
    private static final String UINEEDEDERROR = "Client Error Type - UI Needed";

    public static void trackUINeeded(String MSAJobName, boolean isSilent, UTCTelemetry.CallBackSources source) {
        try {
            ClientError error = new ClientError();
            error.pageName = UTCPageView.getCurrentPage();
            error.errorName = "Client Error Type - UI Needed";
            error.additionalInfo.put("isSilent", Boolean.valueOf(isSilent));
            error.additionalInfo.put("job", MSAJobName);
            error.additionalInfo.put("source", source);
            UTCLog.log("Error:%s, additionalInfo:%s", "Client Error Type - UI Needed", error.GetAdditionalInfoString());
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUINeeded");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackException(Exception ex, String callingSource) {
        ClientError error = new ClientError();
        if (ex != null && callingSource != null) {
            UTCLog.log(String.format("%s:%s", new Object[]{callingSource, ex.getMessage()}), new Object[0]);
            error.errorName = ex.getClass().getSimpleName();
            error.errorText = ex.getMessage();
            StackTraceElement[] stackTrace = ex.getStackTrace();
            String callStack = callingSource;
            if (stackTrace != null && stackTrace.length > 0) {
                int i = 0;
                while (i < stackTrace.length && i < 10) {
                    StackTraceElement element = stackTrace[i];
                    if (element != null) {
                        callStack = String.format("%s;%s", new Object[]{callStack, element.toString()});
                    }
                    if (callStack.length() > 200) {
                        break;
                    }
                    i++;
                }
            }
            error.callStack = callStack;
            error.pageName = UTCPageView.getCurrentPage();
            UTCTelemetry.LogEvent(error);
        }
    }

    public static void trackClose(ErrorActivity.ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track(UTCNames.PageAction.Errors.Close, activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackClose");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackGoToEnforcement(ErrorActivity.ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track(UTCNames.PageAction.Errors.GoToBanned, activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackGoToEnforcement");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackTryAgain(ErrorActivity.ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track(UTCNames.PageAction.Errors.Retry, activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackTryAgain");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackRightButton(ErrorActivity.ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track(UTCNames.PageAction.Errors.RightButton, activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackRightButton");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackPageView(ErrorActivity.ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageView.track(UTCTelemetry.getErrorScreen(errorScreen), activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackPageView");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
