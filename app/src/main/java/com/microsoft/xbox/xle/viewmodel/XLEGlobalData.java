package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MultiSelection;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.activity.ActivityBase;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEGlobalData {
    private static final int MAX_SEARCH_TEXT_LENGTH = 120;
    private boolean autoLoginStarted;
    private Class<? extends ActivityBase> defaultScreenClass;
    private long errorCodeForLogin;
    private boolean forceRefreshProfile;
    private HashSet<Class<? extends ViewModelBase>> forceRefreshVMs;
    private boolean friendListUpdated;
    private boolean hideCollectionFilter;
    private boolean isLoggedIn;
    private boolean isOffline;
    private boolean launchTitleIsBrowser;
    private String pivotTitle;
    private Class<? extends ViewModelBase> searchFilterSetterClass;
    private String searchTag;
    private String selectedAchievementKey;
    private String selectedDataSource;
    private String selectedGamertag;
    private ArrayList<URI> selectedImages;
    private MultiSelection<FriendSelectorItem> selectedRecipients;
    private String selectedXuid;
    private boolean showLoginError;
    private long titleId;

    private XLEGlobalData() {
        isOffline = true;
        friendListUpdated = false;
        launchTitleIsBrowser = false;
        hideCollectionFilter = false;
    }

    public static XLEGlobalData getInstance() {
        return XLEGlobalDataHolder.instance;
    }

    public long getLastLoginError() {
        long error = errorCodeForLogin;
        errorCodeForLogin = 0;
        return error;
    }

    public void setLoginErrorCode(long errorCode) {
        errorCodeForLogin = errorCode;
    }

    public String getSelectedGamertag() {
        return selectedGamertag;
    }

    public void setSelectedGamertag(String gamertag) {
        selectedGamertag = gamertag;
    }

    public ArrayList<URI> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(ArrayList<URI> imageUrl) {
        selectedImages = imageUrl;
    }

    public String getSelectedXuid() {
        if (JavaUtil.isNullOrEmpty(selectedXuid)) {
            return ProjectSpecificDataProvider.getInstance().getXuidString();
        }
        return selectedXuid;
    }

    public void setSelectedXuid(String xuid) {
        selectedXuid = xuid;
    }

    public MultiSelection<FriendSelectorItem> getSelectedRecipients() {
        if (selectedRecipients == null) {
            selectedRecipients = new MultiSelection<>();
        }
        return selectedRecipients;
    }

    public void AddForceRefresh(Class<? extends ViewModelBase> vmclass) {
        XLEAssert.assertIsUIThread();
        if (forceRefreshVMs == null) {
            forceRefreshVMs = new HashSet<>();
        }
        forceRefreshVMs.add(vmclass);
    }

    public boolean CheckDrainShouldRefresh(Class<? extends ViewModelBase> vmclass) {
        return forceRefreshVMs != null && forceRefreshVMs.remove(vmclass);
    }

    public String getSelectedAchievementKey() {
        return selectedAchievementKey;
    }

    public void setSelectedAchievementKey(String key) {
        selectedAchievementKey = key;
    }

    public String getSelectedDataSource() {
        return selectedDataSource;
    }

    public void setSelectedDataSource(String dataSource) {
        selectedDataSource = dataSource;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean value) {
        isLoggedIn = value;
    }

    public boolean getShowLoginError() {
        boolean val = showLoginError;
        showLoginError = false;
        return val;
    }

    public void setShowLoginError(boolean value) {
        showLoginError = value;
    }

    public boolean getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean value) {
        isOffline = value;
    }

    public boolean getIsOnline() {
        return !isOffline;
    }

    public void resetGlobalParameters() {
        selectedGamertag = null;
        selectedAchievementKey = null;
        selectedDataSource = null;
        isLoggedIn = false;
        showLoginError = false;
        isOffline = true;
        searchTag = null;
        selectedImages = null;
        titleId = 0;
        forceRefreshVMs = null;
    }

    public boolean getFriendListUpdated() {
        return friendListUpdated;
    }

    public void setFriendListUpdated(boolean updated) {
        friendListUpdated = updated;
    }

    public boolean getForceRefreshProfile() {
        return forceRefreshProfile;
    }

    public void setForceRefreshProfile(boolean forceRefresh) {
        forceRefreshProfile = forceRefresh;
    }

    public String getSearchTag() {
        return searchTag;
    }

    public void setSearchTag(String searchTag2) {
        if (searchTag2 == null || searchTag2.length() <= MAX_SEARCH_TEXT_LENGTH) {
            searchTag = searchTag2;
        } else {
            searchTag = searchTag2.substring(0, MAX_SEARCH_TEXT_LENGTH);
        }
    }

    public Class<? extends ViewModelBase> getSearchFilterSetterClass() {
        return searchFilterSetterClass;
    }

    public boolean getAutoLoginStarted() {
        return autoLoginStarted;
    }

    public void setAutoLoginStarted(boolean autoLoginStarted2) {
        autoLoginStarted = autoLoginStarted2;
    }

    public Class<? extends ActivityBase> getDefaultScreenClass() {
        return defaultScreenClass;
    }

    public void setDefaultScreenClass(Class<? extends ActivityBase> screenClass) {
        defaultScreenClass = screenClass;
    }

    public boolean getLaunchTitleIsBrowser() {
        return launchTitleIsBrowser;
    }

    public void setLaunchTitleIsBrowser(boolean v) {
        launchTitleIsBrowser = v;
    }

    public String getPivotTitle() {
        return pivotTitle;
    }

    public void setPivotTitle(String pivotTitle2) {
        pivotTitle = pivotTitle2;
    }

    public boolean getHideCollectionFilter() {
        return hideCollectionFilter;
    }

    public void setHideCollectionFilter(boolean isHide) {
        hideCollectionFilter = isHide;
    }

    public long getSelectedTitleId() {
        return titleId;
    }

    public void setSelectedTitleId(long titleId2) {
        titleId = titleId2;
    }

    private static class XLEGlobalDataHolder {
        public static final XLEGlobalData instance = new XLEGlobalData();

        private XLEGlobalDataHolder() {
        }
    }
}
