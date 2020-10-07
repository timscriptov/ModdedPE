package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IPeopleHubResult;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class UserProfileData {
    public String TenureLevel;
    public String accountTier;
    public String appDisplayName;
    public String gamerRealName;
    public String gamerScore;
    public String gamerTag;
    public String profileImageUrl;
    public String xuid;

    public UserProfileData() {
    }

    @Contract(pure = true)
    public UserProfileData(@NotNull IPeopleHubResult.PeopleHubPersonSummary person) {
        xuid = person.xuid;
        profileImageUrl = person.displayPicRaw;
        gamerTag = person.gamertag;
        appDisplayName = person.displayName;
        gamerRealName = person.realName;
        gamerScore = person.gamerScore;
        accountTier = person.xboxOneRep;
    }
}
