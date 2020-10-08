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

public class UTCChangeRelationship {
    public static CharSequence currentActivityTitle = "";
    public static String currentXUID = "";

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(""));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(""));
    }

    @NotNull
    public static HashMap<String, Object> getAdditionalInfo(String targetXUID) {
        HashMap<String, Object> additionalInfoModel = new HashMap<>();
        additionalInfoModel.put(UTCDeepLink.TARGET_XUID_KEY, "x:" + targetXUID);
        return additionalInfoModel;
    }

    public static void trackChangeRelationshipView(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> {
            CharSequence unused = UTCChangeRelationship.currentActivityTitle = activityTitle;
            String unused2 = UTCChangeRelationship.currentXUID = targetXUID;
            UTCPageView.track(UTCNames.PageView.ChangeRelationship.ChangeRelationshipView, UTCChangeRelationship.currentActivityTitle, UTCChangeRelationship.getAdditionalInfo(UTCChangeRelationship.currentXUID));
        });
    }

    public static void trackChangeRelationshipAction(boolean isFollowing, boolean isFromFacebook) {
        verifyTrackedDefaults();
        trackChangeRelationshipAction(currentActivityTitle, currentXUID, isFollowing, isFromFacebook);
    }

    public static void trackChangeRelationshipAction(final CharSequence activityTitle, final String targetXUID, final boolean isFollowing, final boolean isFromFacebook) {
        UTCEventTracker.callTrackWrapper(() -> {
            HashMap<String, Object> additionalInfo = UTCChangeRelationship.getAdditionalInfo(targetXUID);
            additionalInfo.put("relationship", Integer.valueOf(isFollowing ? Relationship.EXISTINGFRIEND.getValue() : Relationship.ADDFRIEND.getValue()));
            UTCPageAction.track(UTCNames.PageAction.ChangeRelationship.Action, activityTitle, additionalInfo);
            if (isFromFacebook) {
                UTCChangeRelationship.trackChangeRelationshipDone(activityTitle, targetXUID, Relationship.ADDFRIEND, RealNameStatus.SHARINGON, FavoriteStatus.NOTFAVORITED, GamerType.FACEBOOK);
            }
        });
    }

    public static void trackChangeRelationshipRemoveFriend() {
        verifyTrackedDefaults();
        trackChangeRelationshipRemoveFriend(currentActivityTitle, currentXUID);
    }

    public static void trackChangeRelationshipRemoveFriend(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(() -> {
            HashMap<String, Object> additionalInfo = UTCChangeRelationship.getAdditionalInfo(targetXUID);
            additionalInfo.put("relationship", Relationship.REMOVEFRIEND);
            UTCPageAction.track(UTCNames.PageAction.ChangeRelationship.Action, activityTitle, additionalInfo);
        });
    }

    public static void trackChangeRelationshipDone(Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        verifyTrackedDefaults();
        trackChangeRelationshipDone(currentActivityTitle, currentXUID, relationship, realNameStatus, favoriteStatus, gamerType);
    }

    public static void trackChangeRelationshipDone(CharSequence activityTitle, String targetXUID, Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        final String str = targetXUID;
        final Relationship relationship2 = relationship;
        final FavoriteStatus favoriteStatus2 = favoriteStatus;
        final RealNameStatus realNameStatus2 = realNameStatus;
        final GamerType gamerType2 = gamerType;
        final CharSequence charSequence = activityTitle;
        UTCEventTracker.callTrackWrapper(() -> {
            HashMap<String, Object> additionalInfo = UTCChangeRelationship.getAdditionalInfo(str);
            additionalInfo.put("relationship", Integer.valueOf(relationship2.getValue()));
            additionalInfo.put("favorite", Integer.valueOf(favoriteStatus2.getValue()));
            additionalInfo.put("realname", Integer.valueOf(realNameStatus2.getValue()));
            additionalInfo.put("gamertype", Integer.valueOf(gamerType2.getValue()));
            UTCPageAction.track(UTCNames.PageAction.ChangeRelationship.Done, charSequence, additionalInfo);
        });
    }

    public enum Relationship {
        UNKNOWN(0),
        ADDFRIEND(1),
        REMOVEFRIEND(2),
        EXISTINGFRIEND(3),
        NOTCHANGED(4);

        private int value;

        private Relationship(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum FavoriteStatus {
        UNKNOWN(0),
        FAVORITED(1),
        UNFAVORITED(2),
        NOTFAVORITED(3),
        EXISTINGFAVORITE(4),
        EXISTINGNOTFAVORITED(5);

        private int value;

        private FavoriteStatus(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum RealNameStatus {
        UNKNOWN(0),
        SHARINGON(1),
        SHARINGOFF(2),
        EXISTINGSHARED(3),
        EXISTINGNOTSHARED(4);

        private int value;

        private RealNameStatus(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum GamerType {
        UNKNOWN(0),
        NORMAL(1),
        FACEBOOK(2),
        SUGGESTED(3);

        private int value;

        private GamerType(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }
}
