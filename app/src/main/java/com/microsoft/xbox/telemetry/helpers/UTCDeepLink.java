package com.microsoft.xbox.telemetry.helpers;

import com.microsoft.xbox.telemetry.utc.CommonData;
import com.microsoft.xbox.telemetry.utc.model.UTCNames;

import java.util.HashMap;

public class UTCDeepLink {
    public static final String CALLING_APP_KEY = "deepLinkCaller";
    public static final String DEEPLINK_KEY_NAME = "deepLinkId";
    public static final String INTENDED_ACTION_KEY = "intendedAction";
    public static final String TARGET_TITLE_KEY = "targetTitleId";
    public static final String TARGET_XUID_KEY = "targetXUID";

    private static String generateCorrelationId() {
        return CommonData.getApplicationSession();
    }

    /* access modifiers changed from: private */
    public static HashMap<String, Object> getAdditionalInfo(String packageName) {
        HashMap<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(DEEPLINK_KEY_NAME, generateCorrelationId());
        additionalInfo.put(CALLING_APP_KEY, packageName);
        return additionalInfo;
    }

    public static String trackUserProfileLink(final CharSequence activityTitle, final String packageName, final String targetXuid) {
        return UTCEventTracker.callStringTrackWrapper(new UTCEventTracker.UTCStringEventDelegate() {
            public String call() {
                HashMap<String, Object> additionalInfo = UTCDeepLink.getAdditionalInfo(packageName);
                additionalInfo.put(UTCDeepLink.TARGET_XUID_KEY, "x:" + targetXuid);
                UTCPageAction.track(UTCNames.PageAction.DeepLink.UserProfile, activityTitle, additionalInfo);
                return additionalInfo.get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static String trackGameHubLink(final CharSequence activityTitle, final String packageName, final String titleId) {
        return UTCEventTracker.callStringTrackWrapper(new UTCEventTracker.UTCStringEventDelegate() {
            public String call() {
                HashMap<String, Object> additionalInfo = UTCDeepLink.getAdditionalInfo(packageName);
                additionalInfo.put(UTCDeepLink.TARGET_XUID_KEY, titleId);
                UTCPageAction.track(UTCNames.PageAction.DeepLink.TitleHub, activityTitle, additionalInfo);
                return additionalInfo.get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static String trackGameHubAchievementsLink(final CharSequence activityTitle, final String packageName, final String titleId) {
        return UTCEventTracker.callStringTrackWrapper(new UTCEventTracker.UTCStringEventDelegate() {
            public String call() {
                HashMap<String, Object> additionalInfo = UTCDeepLink.getAdditionalInfo(packageName);
                additionalInfo.put(UTCDeepLink.TARGET_TITLE_KEY, titleId);
                UTCPageAction.track(UTCNames.PageAction.DeepLink.TitleHub, activityTitle, additionalInfo);
                return additionalInfo.get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static String trackUserSettingsLink(final CharSequence activityTitle, final String packageName) {
        return UTCEventTracker.callStringTrackWrapper(new UTCEventTracker.UTCStringEventDelegate() {
            public String call() {
                HashMap<String, Object> additionalInfo = UTCDeepLink.getAdditionalInfo(packageName);
                UTCPageAction.track(UTCNames.PageAction.DeepLink.UserSettings, activityTitle, additionalInfo);
                return additionalInfo.get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static void trackUserSendToStore(final CharSequence activityTitle, final String packageName, final String intendedAction) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                HashMap<String, Object> additionalInfo = UTCDeepLink.getAdditionalInfo(packageName);
                additionalInfo.put(UTCDeepLink.INTENDED_ACTION_KEY, intendedAction);
                UTCPageAction.track(UTCNames.PageAction.DeepLink.SendToStore, activityTitle, additionalInfo);
            }
        });
    }

    public static String trackFriendSuggestionsLink(final CharSequence activityTitle, final String packageName) {
        return UTCEventTracker.callStringTrackWrapper(new UTCEventTracker.UTCStringEventDelegate() {
            public String call() {
                HashMap<String, Object> additionalInfo = UTCDeepLink.getAdditionalInfo(packageName);
                UTCPageAction.track(UTCNames.PageAction.DeepLink.FriendSuggestions, activityTitle, additionalInfo);
                return additionalInfo.get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }
}
