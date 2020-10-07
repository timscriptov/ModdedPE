package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IUserProfileResult;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProfileData {
    private IUserProfileResult.UserProfileResult profileResult;
    private boolean shareRealName;
    private String shareRealNameStatus;
    private boolean sharingRealNameTransitively;

    public ProfileData(IUserProfileResult.UserProfileResult profileResult2, boolean shareRealName2) {
        profileResult = profileResult2;
        shareRealName = shareRealName2;
        shareRealNameStatus = null;
    }

    public ProfileData(IUserProfileResult.UserProfileResult profileResult2, boolean shareRealName2, String shareRealNameStatus2, boolean sharingRealNameTransitively2) {
        profileResult = profileResult2;
        shareRealName = shareRealName2;
        shareRealNameStatus = shareRealNameStatus2;
        sharingRealNameTransitively = sharingRealNameTransitively2;
    }

    public IUserProfileResult.UserProfileResult getProfileResult() {
        return profileResult;
    }

    public boolean getShareRealName() {
        return shareRealName;
    }

    public String getShareRealNameStatus() {
        return shareRealNameStatus;
    }

    public boolean getSharingRealNameTransitively() {
        return sharingRealNameTransitively;
    }
}
