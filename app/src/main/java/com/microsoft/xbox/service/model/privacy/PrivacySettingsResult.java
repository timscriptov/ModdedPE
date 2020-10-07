package com.microsoft.xbox.service.model.privacy;

import com.microsoft.xbox.toolkit.GsonUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class PrivacySettingsResult {
    public ArrayList<PrivacySettings.PrivacySetting> settings;

    public PrivacySettingsResult() {
    }

    public PrivacySettingsResult(ArrayList<PrivacySettings.PrivacySetting> settings2) {
        settings = new ArrayList<>(settings2);
    }

    public static PrivacySettingsResult deserialize(String input) {
        return GsonUtil.deserializeJson(input, PrivacySettingsResult.class);
    }

    @Nullable
    public static String getPrivacySettingRequestBody(PrivacySettingsResult privacySettingsResult) {
        try {
            return GsonUtil.toJsonString(privacySettingsResult);
        } catch (Exception e) {
            return null;
        }
    }

    public String getShareRealNameStatus() {
        Iterator<PrivacySettings.PrivacySetting> it = settings.iterator();
        while (it.hasNext()) {
            PrivacySettings.PrivacySetting s = it.next();
            if (s.getPrivacySettingId() == PrivacySettings.PrivacySettingId.ShareIdentity) {
                return s.value;
            }
        }
        return PrivacySettings.PrivacySettingValue.PeopleOnMyList.name();
    }

    public boolean getSharingRealNameTransitively() {
        Iterator<PrivacySettings.PrivacySetting> it = settings.iterator();
        while (it.hasNext()) {
            PrivacySettings.PrivacySetting s = it.next();
            if (s.getPrivacySettingId() == PrivacySettings.PrivacySettingId.ShareIdentityTransitively) {
                return s.value.equalsIgnoreCase(PrivacySettings.PrivacySettingValue.Everyone.name());
            }
        }
        return false;
    }
}