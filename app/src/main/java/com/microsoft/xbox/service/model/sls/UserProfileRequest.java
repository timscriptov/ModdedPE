package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

import java.util.ArrayList;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class UserProfileRequest {
    public ArrayList<String> settings;
    public ArrayList<String> userIds;

    public UserProfileRequest() {
        settings = new ArrayList<>();
        userIds = new ArrayList<>();
        setDefaultProfileSettingsRequest(false);
    }

    public UserProfileRequest(ArrayList<String> userIds2, boolean dataEssentialForLoginOnly) {
        userIds = userIds2;
        settings = new ArrayList<>();
        setDefaultProfileSettingsRequest(dataEssentialForLoginOnly);
    }

    public UserProfileRequest(ArrayList<String> userIds2) {
        this(userIds2, false);
    }

    public UserProfileRequest(ArrayList<String> userIds2, ArrayList<String> settings2) {
        userIds = userIds2;
        settings = settings2;
    }

    public static String getUserProfileRequestBody(UserProfileRequest userProfileRequest) {
        return GsonUtil.toJsonString(userProfileRequest);
    }

    private void setDefaultProfileSettingsRequest(boolean dataEssentialForLoginOnly) {
        if (settings != null) {
            settings.add(UserProfileSetting.GameDisplayName.toString());
            settings.add(UserProfileSetting.AppDisplayName.toString());
            settings.add(UserProfileSetting.AppDisplayPicRaw.toString());
            settings.add(UserProfileSetting.Gamerscore.toString());
            settings.add(UserProfileSetting.Gamertag.toString());
            settings.add(UserProfileSetting.GameDisplayPicRaw.toString());
            settings.add(UserProfileSetting.AccountTier.toString());
            settings.add(UserProfileSetting.TenureLevel.toString());
            settings.add(UserProfileSetting.XboxOneRep.toString());
            settings.add(UserProfileSetting.PreferredColor.toString());
            settings.add(UserProfileSetting.Location.toString());
            settings.add(UserProfileSetting.Bio.toString());
            settings.add(UserProfileSetting.Watermarks.toString());
            if (!dataEssentialForLoginOnly) {
                settings.add(UserProfileSetting.RealName.toString());
            }
        }
    }
}
