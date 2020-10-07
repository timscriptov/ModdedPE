package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.XLEUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class FollowersData implements Serializable {
    private static final long serialVersionUID = 6714889261254600161L;
    public boolean isCurrentlyPlaying;
    public boolean isFavorite;
    public transient boolean isNew;
    public String presenceString;
    public UserStatus status;
    public long titleId;
    public UserProfileData userProfileData;
    public String xuid;
    protected boolean isDummy;
    protected DummyType itemDummyType;
    private String followerText;
    private Date lastPlayedWithDateTime;
    private IPeopleHubResult.PeopleHubPersonSummary personSummary;
    private String recentPlayerText;
    private SearchResultPerson searchResultPerson;
    private Date timeStamp;

    public FollowersData() {
        personSummary = null;
        isCurrentlyPlaying = false;
        isDummy = false;
        isNew = false;
    }

    public FollowersData(boolean isDummy2) {
        this(isDummy2, DummyType.NOT_SET);
    }

    public FollowersData(boolean isDummy2, DummyType type) {
        personSummary = null;
        isCurrentlyPlaying = false;
        isDummy = false;
        isNew = false;
        isDummy = isDummy2;
        itemDummyType = type;
    }

    public FollowersData(IPeopleHubResult.PeopleHubPersonSummary person) {
        personSummary = null;
        isCurrentlyPlaying = false;
        isDummy = false;
        isNew = false;
        XLEAssert.assertNotNull(person);
        personSummary = person;
        xuid = person.xuid;
        userProfileData = new UserProfileData(person);
        isFavorite = person.isFavorite;
        status = UserStatus.getStatusFromString(person.presenceState);
        presenceString = person.presenceText;
        if (person.titleHistory != null) {
            titleId = person.titleHistory.TitleId;
            timeStamp = person.titleHistory.LastTimePlayed;
        }
        if (person.recentPlayer != null) {
            recentPlayerText = person.recentPlayer.text;
            if (!XLEUtil.isNullOrEmpty(person.recentPlayer.titles)) {
                lastPlayedWithDateTime = person.recentPlayer.titles.get(0).lastPlayedWithDateTime;
            }
        }
        if (person.follower != null) {
            followerText = person.follower.text;
        }
        if (person.titlePresence != null) {
            isCurrentlyPlaying = person.titlePresence.IsCurrentlyPlaying;
            presenceString = person.titlePresence.PresenceText;
        }
    }

    @Contract(pure = true)
    public FollowersData(@NotNull FollowersData follower) {
        personSummary = null;
        isCurrentlyPlaying = false;
        isDummy = false;
        isNew = false;
        xuid = follower.xuid;
        isFavorite = follower.isFavorite;
        status = follower.status;
        presenceString = follower.presenceString;
        titleId = follower.titleId;
        userProfileData = follower.userProfileData;
        isCurrentlyPlaying = follower.isCurrentlyPlaying;
        timeStamp = follower.timeStamp;
        isDummy = follower.isDummy;
    }

    public DummyType getItemDummyType() {
        return itemDummyType;
    }

    public void setItemDummyType(DummyType type) {
        isDummy = true;
        itemDummyType = type;
    }

    public IPeopleHubResult.PeopleHubPersonSummary getPersonSummary() {
        return personSummary;
    }

    public int getGameScore() {
        if (userProfileData != null) {
            return Integer.parseInt(userProfileData.gamerScore);
        }
        return 0;
    }

    public String getGamertag() {
        if (userProfileData != null) {
            return userProfileData.gamerTag;
        }
        return "";
    }

    public String getGamerPicUrl() {
        if (userProfileData != null) {
            return userProfileData.profileImageUrl;
        }
        return null;
    }

    public String getGamerName() {
        if (userProfileData != null) {
            return userProfileData.appDisplayName;
        }
        return "";
    }

    public String getGamerRealName() {
        if (userProfileData == null) {
            return null;
        }
        return userProfileData.gamerRealName;
    }

    public boolean getIsOnline() {
        return status == UserStatus.Online;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp2) {
        timeStamp = timeStamp2;
    }

    public boolean getIsDummy() {
        return isDummy;
    }

    public Date getLastPlayedWithDateTime() {
        return lastPlayedWithDateTime;
    }

    public String getRecentPlayerTitleText() {
        return recentPlayerText;
    }

    public String getFollowersTitleText() {
        return followerText;
    }

    public SearchResultPerson getSearchResultPerson() {
        return searchResultPerson;
    }

    public void setSearchResultPerson(SearchResultPerson srp) {
        searchResultPerson = srp;
    }

    public enum DummyType {
        NOT_SET,
        DUMMY_HEADER,
        DUMMY_FRIENDS_HEADER,
        DUMMY_LINK_TO_FACEBOOK,
        DUMMY_FRIENDS_WHO_PLAY,
        DUMMY_VIPS,
        DUMMY_ERROR,
        DUMMY_NO_DATA,
        DUMMY_LOADING
    }
}
