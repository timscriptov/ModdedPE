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

public class UTCPeopleHub {
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

    public static void trackPeopleHubView(final CharSequence activityTitle, final String targetXUID, final boolean isMeView) {
        UTCEventTracker.callTrackWrapper(() -> {
            String unused = UTCPeopleHub.currentXUID = targetXUID;
            CharSequence unused2 = UTCPeopleHub.currentActivityTitle = activityTitle;
            UTCPageView.track(isMeView ? UTCNames.PageView.PeopleHub.PeopleHubMeView : UTCNames.PageView.PeopleHub.PeopleHubYouView, UTCPeopleHub.currentActivityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID));
        });
    }

    public static void trackMute(boolean toBeMuted) {
        verifyTrackedDefaults();
        trackMute(currentActivityTitle, currentXUID, toBeMuted);
    }

    public static void trackMute(final CharSequence activityTitle, final String targetXUID, final boolean toBeMuted) {
        UTCEventTracker.callTrackWrapper(() -> {
            HashMap<String, Object> additionalInfo = UTCPeopleHub.getAdditionalInfo(targetXUID);
            additionalInfo.put("isMuted", Boolean.valueOf(toBeMuted));
            UTCPageAction.track(UTCNames.PageAction.PeopleHub.Mute, activityTitle, additionalInfo);
        });
    }

    public static void trackUnblock() {
        verifyTrackedDefaults();
        trackUnblock(currentActivityTitle, currentXUID);
    }

    public static void trackUnblock(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> UTCPageAction.track(UTCNames.PageAction.PeopleHub.Unblock, activityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID)));
    }

    public static void trackBlock() {
        verifyTrackedDefaults();
        trackBlock(currentActivityTitle, currentXUID);
    }

    public static void trackBlock(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> UTCPageAction.track(UTCNames.PageAction.PeopleHub.Block, activityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID)));
    }

    public static void trackBlockDialogComplete() {
        verifyTrackedDefaults();
        trackBlockDialogComplete(currentActivityTitle, currentXUID);
    }

    public static void trackBlockDialogComplete(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> UTCPageAction.track(UTCNames.PageAction.PeopleHub.BlockOK, activityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID)));
    }

    public static void trackReport() {
        verifyTrackedDefaults();
        trackReport(currentActivityTitle, currentXUID);
    }

    public static void trackReport(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> UTCPageAction.track(UTCNames.PageAction.PeopleHub.Report, activityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID)));
    }

    public static void trackViewInXboxApp() {
        verifyTrackedDefaults();
        trackViewInXboxApp(currentActivityTitle, currentXUID);
    }

    public static void trackViewInXboxApp(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> UTCPageAction.track(UTCNames.PageAction.PeopleHub.ViewXboxApp, activityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID)));
    }

    public static void trackViewInXboxAppDialogComplete() {
        verifyTrackedDefaults();
        trackViewInXboxAppDialogComplete(currentActivityTitle, currentXUID);
    }

    public static void trackViewInXboxAppDialogComplete(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> UTCPageAction.track(UTCNames.PageAction.PeopleHub.ViewXboxAppOK, activityTitle, UTCPeopleHub.getAdditionalInfo(targetXUID)));
    }
}
