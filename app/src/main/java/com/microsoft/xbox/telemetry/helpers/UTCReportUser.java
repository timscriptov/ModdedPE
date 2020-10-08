package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.telemetry.utc.model.UTCNames;
import com.microsoft.xbox.toolkit.XLEAssert;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class UTCReportUser {
    public static CharSequence currentActivityTitle = "";
    public static String currentXUID = "";

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(""));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(""));
    }

    @NotNull
    public static HashMap<String, Object> getAdditionalInfo(String targetXUID) {
        HashMap<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(UTCDeepLink.TARGET_XUID_KEY, "x:" + targetXUID);
        return additionalInfo;
    }

    public static void trackReportView(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> {
            CharSequence unused = UTCReportUser.currentActivityTitle = activityTitle;
            String unused2 = UTCReportUser.currentXUID = targetXUID;
            UTCPageView.track(UTCNames.PageView.PeopleHub.ReportView, UTCReportUser.currentActivityTitle, UTCReportUser.getAdditionalInfo(targetXUID));
        });
    }

    public static void trackReportDialogOK(String reason) {
        verifyTrackedDefaults();
        trackReportDialogOK(currentActivityTitle, currentXUID, reason);
    }

    public static void trackReportDialogOK(final CharSequence activityTitle, final String targetXUID, final String reason) {
        UTCEventTracker.callTrackWrapper(() -> {
            HashMap<String, Object> additionalInfo = UTCReportUser.getAdditionalInfo(targetXUID);
            additionalInfo.put("reason", reason);
            UTCPageAction.track(UTCNames.PageAction.PeopleHub.ReportOK, activityTitle, additionalInfo);
        });
    }
}
