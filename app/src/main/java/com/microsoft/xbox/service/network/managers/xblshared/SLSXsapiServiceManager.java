package com.microsoft.xbox.service.network.managers.xblshared;

import android.util.Log;
import android.util.Pair;

import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.service.model.privacy.PrivacySettings;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.IUserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.service.network.managers.ProfilePreferredColor;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.TcuiHttpUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SLSXsapiServiceManager implements ISLSServiceManager {
    private static final String TAG = "SLSXsapiServiceManager";

    public FamilySettings getFamilySettings(String xuid) throws XLEException {
        return null;
    }

    public boolean removeFriendFromShareIdentitySetting(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeFriendFromShareIdentitySetting");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getRemoveUsersFromShareIdentityUrlFormat(), new Object[]{xuid}), ""), "4");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{Integer.valueOf(HttpStatus.SC_NO_CONTENT)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean addFriendToShareIdentitySetting(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addFriendToShareIdentitySetting");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getAddFriendsToShareIdentityUrlFormat(), new Object[]{xuid}), ""), "4");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{Integer.valueOf(HttpStatus.SC_NO_CONTENT)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean addUserToFavoriteList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToFavoriteList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), new Object[]{"add"}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{Integer.valueOf(HttpStatus.SC_NO_CONTENT)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean removeUserFromFavoriteList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromFavoriteList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), new Object[]{"remove"}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{Integer.valueOf(HttpStatus.SC_NO_CONTENT)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToFollowingList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), new Object[]{"add"}), ""), "1");
        httpCall.setRequestBody(postBody);
        final AddFollowingUserResponseContainer.AddFollowingUserResponse result = new AddFollowingUserResponseContainer.AddFollowingUserResponse();
        final AtomicReference<Pair<Boolean, AddFollowingUserResponseContainer.AddFollowingUserResponse>> notifier = new AtomicReference<>();
        notifier.set(new Pair(false, null));
        httpCall.getResponseAsync((httpStatus, stream, headers) -> {
            synchronized (notifier) {
                if (httpStatus >= 200 || httpStatus <= 299) {
                    result.setAddFollowingRequestStatus(true);
                    notifier.set(new Pair(true, result));
                } else {
                    notifier.set(new Pair(true, GsonUtil.deserializeJson(stream, AddFollowingUserResponseContainer.AddFollowingUserResponse.class)));
                }
                notifier.notify();
            }
        });
        synchronized (notifier) {
            while (!((Boolean) notifier.get().first).booleanValue()) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        TcuiHttpUtil.throwIfNullOrFalse(notifier.get().second);
        return notifier.get().second;
    }

    public ProfileSummaryResultContainer.ProfileSummaryResult getProfileSummaryInfo(String xuid) throws XLEException {
        boolean z;
        boolean z2;
        Log.i(TAG, "getProfileSummaryInfo");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            z2 = true;
        } else {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        ProfileSummaryResultContainer.ProfileSummaryResult result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileSummaryUrlFormat(), new Object[]{xuid}), ""), "2"), ProfileSummaryResultContainer.ProfileSummaryResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean removeUserFromFollowingList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromFollowingList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), new Object[]{"remove"}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{Integer.valueOf(HttpStatus.SC_NO_CONTENT)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public IUserProfileResult.UserProfileResult getUserProfileInfo(String postBody) throws XLEException {
        Log.i(TAG, "getUserProfileInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall httpCall = new HttpCall(HttpPost.METHOD_NAME, XboxLiveEnvironment.Instance().getUserProfileInfoUrl(), "");
        HttpUtil.appendCommonParameters(httpCall, XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION);
        httpCall.setRequestBody(postBody);
        IUserProfileResult.UserProfileResult result = TcuiHttpUtil.getResponseSync(httpCall, IUserProfileResult.UserProfileResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public int[] getXTokenPrivileges() throws XLEException {
        return new int[0];
    }

    public ProfilePreferredColor getProfilePreferredColor(String url) throws XLEException {
        Log.i(TAG, "getProfilePreferredColor");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        ProfilePreferredColor result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, url, ""), "2"), ProfilePreferredColor.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public PrivacySettingsResult getUserProfilePrivacySettings() throws XLEException {
        Log.i(TAG, "getUserProfilePrivacySettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PrivacySettingsResult result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), ""), "4"), PrivacySettingsResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public NeverListResultContainer.NeverListResult getNeverListInfo(String xuid) throws XLEException {
        boolean z;
        boolean z2;
        Log.i(TAG, "getNeverListInfo");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            z2 = true;
        } else {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        NeverListResultContainer.NeverListResult result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{xuid}), ""), "1"), NeverListResultContainer.NeverListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean addUserToNeverList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToNeverList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean removeUserFromNeverList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromNeverList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpDelete.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public MutedListResultContainer.MutedListResult getMutedListInfo(String xuid) throws XLEException {
        boolean z;
        boolean z2;
        Log.i(TAG, "getMutedListInfo");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            z2 = true;
        } else {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        MutedListResultContainer.MutedListResult result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{xuid}), ""), "1"), MutedListResultContainer.MutedListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean addUserToMutedList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToMutedList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean removeUserFromMutedList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromMutedList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpDelete.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean submitFeedback(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "submitFeedback");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPost.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getSubmitFeedbackUrlFormat(), new Object[]{xuid}), ""), "101");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(HttpStatus.SC_ACCEPTED));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public PrivacySettings.PrivacySetting getPrivacySetting(PrivacySettings.PrivacySettingId settingId) throws XLEException {
        boolean z;
        Log.i(TAG, "getPrivacySetting");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        PrivacySettings.PrivacySetting result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getProfileSettingUrlFormat(), new Object[]{settingId.name()}), ""), "4"), PrivacySettings.PrivacySetting.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean setPrivacySettings(PrivacySettingsResult settings) throws XLEException {
        boolean z;
        Log.i(TAG, "setPrivacySettings");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), ""), "4");
        httpCall.setRequestBody(PrivacySettingsResult.getPrivacySettingRequestBody(settings));
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{Integer.valueOf(HttpStatus.SC_CREATED)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public IPeopleHubResult.PeopleHubPeopleSummary getPeopleHubRecommendations() throws XLEException {
        Log.i(TAG, "getPeopleHubRecommendations");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, XboxLiveEnvironment.Instance().getPeopleHubRecommendationsUrlFormat(), ""), "1");
        httpCall.setCustomHeader("Accept-Language", ProjectSpecificDataProvider.getInstance().getLegalLocale());
        httpCall.setCustomHeader("X-XBL-Contract-Version", "1");
        httpCall.setCustomHeader("X-XBL-Market", ProjectSpecificDataProvider.getInstance().getRegion());
        IPeopleHubResult.PeopleHubPeopleSummary result = TcuiHttpUtil.getResponseSync(httpCall, IPeopleHubResult.PeopleHubPeopleSummary.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public IUserProfileResult.UserProfileResult SearchGamertag(String gamertag) throws XLEException {
        boolean z = true;
        Log.i(TAG, "SearchGamertag");
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        try {
            IUserProfileResult.UserProfileResult result = TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, String.format(XboxLiveEnvironment.Instance().getGamertagSearchUrlFormat(), new Object[]{URLEncoder.encode(gamertag.toLowerCase(), "utf-8")}), ""), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION), IUserProfileResult.UserProfileResult.class);
            TcuiHttpUtil.throwIfNullOrFalse(result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new XLEException(15, (Throwable) e);
        }
    }
}