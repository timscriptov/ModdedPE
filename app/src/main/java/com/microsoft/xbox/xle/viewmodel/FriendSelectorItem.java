package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.UserProfileData;

import org.jetbrains.annotations.NotNull;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class FriendSelectorItem extends FollowersData {
    private static final long serialVersionUID = 5799344980951867134L;
    private boolean selected;

    public FriendSelectorItem(FollowersData friend) {
        super(friend);
        selected = false;
    }

    public FriendSelectorItem(@NotNull ProfileModel profileModel) {
        xuid = profileModel.getXuid();
        userProfileData = new UserProfileData();
        userProfileData.gamerTag = profileModel.getGamerTag();
        userProfileData.xuid = profileModel.getXuid();
        userProfileData.profileImageUrl = profileModel.getGamerPicImageUrl();
        userProfileData.gamerScore = profileModel.getGamerScore();
        userProfileData.appDisplayName = profileModel.getAppDisplayName();
        userProfileData.accountTier = profileModel.getAccountTier();
        userProfileData.gamerRealName = profileModel.getRealName();
    }

    public void toggleSelection() {
        selected = !selected;
    }

    public boolean getIsSelected() {
        return selected;
    }

    public void setSelected(boolean value) {
        selected = value;
    }

    public int hashCode() {
        return ((userProfileData == null || userProfileData.gamerTag == null) ? 0 : userProfileData.gamerTag.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FriendSelectorItem other = (FriendSelectorItem) obj;
        if (userProfileData == null || userProfileData.gamerTag == null) {
            if (other.userProfileData == null && other.userProfileData.gamerTag == null) {
                return true;
            }
            return false;
        } else if (!userProfileData.gamerTag.equals(other.userProfileData.gamerTag)) {
            return false;
        } else {
            return true;
        }
    }
}
