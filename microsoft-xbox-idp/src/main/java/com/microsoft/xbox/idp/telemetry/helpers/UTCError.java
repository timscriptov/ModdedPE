package com.microsoft.xbox.idp.telemetry.helpers;

import com.facebook.share.internal.ShareConstants;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.ClientError;
import com.microsoft.xbox.idp.telemetry.utc.ServiceError;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction.Errors;
import com.microsoft.xbox.idp.toolkit.HttpError;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;

public class UTCError {
    private static final int CLIENTERRORVERSION = 1;
    private static final String FAILURE = "Client Error Type - Failure";
    private static final String MSACANCEL = "Client Error Type - MSA canceled";
    private static final int SERVICEERRORVERSION = 1;
    private static final String SIGNEDOUT = "Client Error Type - Signed out";
    private static final String UINEEDEDERROR = "Client Error Type - UI Needed";
    private static final String USERCANCEL = "Client Error Type - User canceled";

    public static void trackUINeeded(String MSAJobName, boolean isSilent, CallBackSources source) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            model.addValue("job", MSAJobName);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, source);
            ClientError error = new ClientError();
            error.setPageName(UTCPageView.getCurrentPage());
            error.setErrorName("Client Error Type - UI Needed");
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Error:%s, additionalInfo:%s", "Client Error Type - UI Needed", model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUINeeded");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackUserCancel(String MSAJobName, boolean isSilent, CallBackSources source) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            model.addValue("job", MSAJobName);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, source);
            ClientError error = new ClientError();
            error.setPageName(UTCPageView.getCurrentPage());
            error.setErrorName("Client Error Type - User canceled");
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Error:%s, additionalInfo:%s", "Client Error Type - User canceled", model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUserCancel");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackMSACancel(String MSAJobName, boolean isSilent, CallBackSources source) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            model.addValue("job", MSAJobName);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, source);
            ClientError error = new ClientError();
            error.setPageName(UTCPageView.getCurrentPage());
            error.setErrorName("Client Error Type - MSA canceled");
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Error:%s, additionalInfo:%s", "Client Error Type - MSA canceled", model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackUserCancel");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackSignedOut(String MSAJobName, boolean isSilent, CallBackSources source) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            model.addValue("job", MSAJobName);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, source);
            ClientError error = new ClientError();
            error.setPageName(UTCPageView.getCurrentPage());
            error.setErrorName("Client Error Type - Signed out");
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Error:%s, additionalInfo:%s", "Client Error Type - Signed out", model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackSignedOut");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackFailure(String jobName, boolean isSilent, CallBackSources source, Exception exception) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            model.addValue("job", jobName);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, source);
            ClientError error = new ClientError();
            error.setErrorName("Client Error Type - Failure");
            error.setPageName(UTCPageView.getCurrentPage());
            String exceptionName = "";
            String str = "";
            if (exception != null) {
                exceptionName = exception.getClass().getSimpleName();
                String exceptionMessage = exception.getMessage();
                error.setErrorName(exceptionName);
                error.setErrorText(exceptionMessage);
            }
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Error:%s, exception:%s, additionalInfo:%s", "Client Error Type - Failure", exceptionName, model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackFailure");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackFailure(String jobName, boolean isSilent, CallBackSources source, long errorCode) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("isSilent", Boolean.valueOf(isSilent));
            model.addValue("job", jobName);
            model.addValue(ShareConstants.FEED_SOURCE_PARAM, source);
            ClientError error = new ClientError();
            error.setErrorName("Client Error Type - Failure");
            error.setErrorCode(String.format("%s", new Object[]{Long.valueOf(errorCode)}));
            error.setPageName(UTCPageView.getCurrentPage());
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Error:%s, errorCode:%s, additionalInfo:%s", "Client Error Type - Failure", Long.valueOf(errorCode), model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackFailure");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackException(Exception ex, String callingSource) {
        String str = "unknown";
        ClientError error = new ClientError();
        if (ex != null && callingSource != null) {
            UTCLog.log(String.format("%s:%s", new Object[]{callingSource, ex.getMessage()}), new Object[0]);
            error.setErrorName(ex.getClass().getSimpleName());
            error.setErrorText(ex.getMessage());
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
            error.setCallStack(callStack);
            error.setPageName(UTCPageView.getCurrentPage());
            UTCTelemetry.LogEvent(error);
        }
    }

    public static void trackServiceFailure(String errorName, String pageName, HttpError httpError) {
        try {
            UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
            model.addValue("pageName", pageName);
            String errorMessage = "UNKNOWN";
            String errorCode = "0";
            if (httpError != null) {
                errorMessage = httpError.getErrorMessage();
                errorCode = String.format("%s", new Object[]{Integer.valueOf(httpError.getErrorCode())});
            }
            ServiceError error = new ServiceError();
            error.setErrorName(errorName);
            error.setErrorText(errorMessage);
            if (pageName == null) {
                pageName = UTCPageView.getCurrentPage();
            }
            error.setPageName(pageName);
            error.setErrorCode(String.format("%s", new Object[]{errorCode}));
            error.setBaseData(UTCCommonDataModel.getCommonData(1, model));
            UTCLog.log("Service Error:%s, errorCode:%s, additionalInfo:%s", "Client Error Type - Failure", errorCode, model);
            UTCTelemetry.LogEvent(error);
        } catch (Exception e) {
            trackException(e, "UTCError.trackServiceFailure");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackClose(ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track("Errors - Close error screen", activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackClose");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackGoToEnforcement(ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track("Errors - View enforcement site", activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackGoToEnforcement");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackTryAgain(ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track("Errors - Try again", activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackTryAgain");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackRightButton(ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageAction.track(Errors.RightButton, activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackRightButton");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackPageView(ErrorScreen errorScreen, CharSequence activityTitle) {
        try {
            UTCPageView.track(UTCTelemetry.getErrorScreen(errorScreen), activityTitle);
        } catch (Exception e) {
            trackException(e, "UTCError.trackPageView");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
