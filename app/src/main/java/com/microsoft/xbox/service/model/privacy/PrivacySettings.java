package com.microsoft.xbox.service.model.privacy;

import org.jetbrains.annotations.NotNull;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class PrivacySettings {

    public enum PrivacySettingId {
        None,
        ShareFriendList,
        ShareGameHistory,
        CommunicateUsingTextAndVoice,
        SharePresence,
        ShareProfile,
        ShareVideoAndMusicStatus,
        CommunicateUsingVideo,
        CollectVoiceData,
        ShareXboxMusicActivity,
        ShareExerciseInfo,
        ShareIdentity,
        ShareRecordedGameSessions,
        ShareIdentityTransitively,
        CanShareIdentity;

        public static PrivacySettingId getPrivacySettingId(String id) {
            for (PrivacySettingId status : values()) {
                if (status.name().equalsIgnoreCase(id)) {
                    return status;
                }
            }
            return None;
        }
    }

    public enum PrivacySettingValue {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked;

        public static PrivacySettingValue getPrivacySettingValue(String value) {
            for (PrivacySettingValue status : values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return NotSet;
        }
    }

    public static class PrivacySetting {
        public String setting;
        public String value;
        private PrivacySettingId settingId;
        private PrivacySettingValue settingValue;

        public PrivacySetting() {
        }

        public PrivacySetting(@NotNull PrivacySettingId settingId2, @NotNull PrivacySettingValue value2) {
            setting = settingId2.name();
            value = value2.name();
        }

        public PrivacySettingId getPrivacySettingId() {
            settingId = PrivacySettingId.getPrivacySettingId(setting);
            return settingId;
        }

        public void setPrivacySettingId(@NotNull PrivacySettingId settingId2) {
            setting = settingId2.name();
            settingId = settingId2;
        }

        public PrivacySettingValue getPrivacySettingValue() {
            settingValue = PrivacySettingValue.getPrivacySettingValue(value);
            return settingValue;
        }
    }
}
