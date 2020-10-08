package com.microsoft.xbox.xle.app.activity.Profile;

import androidx.appcompat.app.AlertDialog;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.activity.ReportUserScreen;
import com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import com.microsoft.xboxtcui.XboxAppDeepLinker;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProfileScreenViewModel extends ViewModelBase {
    private static final String TAG = ProfileScreenViewModel.class.getSimpleName();
    public boolean isAddingUserToBlockList;
    public boolean isAddingUserToFollowingList;
    public boolean isAddingUserToMutedList;
    public boolean isAddingUserToShareIdentityList;
    public boolean isLoadingUserMutedList;
    public boolean isLoadingUserNeverList;
    public boolean isLoadingUserProfile;
    public boolean isRemovingUserFromBlockList;
    public boolean isRemovingUserFromMutedList;
    protected ProfileModel model = ProfileModel.getProfileModel(NavigationManager.getInstance().getActivityParameters().getSelectedProfile());
    private AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask;
    private AddUserToMutedListAsyncTask addUserToMutedListAsyncTask;
    private AddUserToNeverListAsyncTask addUserToNeverListAsyncTask;
    private AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask;
    private FollowersData basicData;
    private ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel;
    private HashSet<ChangeFriendshipFormOptions> changeFriendshipForm = new HashSet<>();
    private boolean isBlocked = false;
    private boolean isFavorite = false;
    private boolean isFollowing = false;
    private boolean isMuted = false;
    private boolean isShowingFailureDialog;
    private LoadUserProfileAsyncTask loadMeProfileTask;
    private LoadUserMutedListAsyncTask loadUserMutedListTask;
    private LoadUserNeverListAsyncTask loadUserNeverListTask;
    private LoadUserProfileAsyncTask loadUserProfileTask;
    private RemoveUserFromMutedListAsyncTask removeUserFromMutedListAsyncTask;
    private RemoveUserToNeverListAsyncTask removeUserToNeverListAsyncTask;

    public ProfileScreenViewModel(ScreenLayout screen) {
        super(screen);
        adapter = new ProfileScreenAdapter(this);
    }

    public void onRehydrate() {
        adapter = new ProfileScreenAdapter(this);
    }

    public String getGamerTag() {
        return model.getGamerTag();
    }

    public String getGamerScore() {
        return model.getGamerScore();
    }

    public String getGamerPicUrl() {
        return model.getGamerPicImageUrl();
    }

    public String getXuid() {
        return model.getXuid();
    }

    public String getRealName() {
        return model.getRealName();
    }

    public boolean isCallerFollowingTarget() {
        return isFollowing;
    }

    public int getPreferredColor() {
        return model.getPreferedColor();
    }

    public boolean isMeProfile() {
        return model.isMeProfile();
    }

    public void onStartOverride() {
        isShowingFailureDialog = false;
    }

    public boolean isBusy() {
        return isLoadingUserProfile || isLoadingUserNeverList || isLoadingUserMutedList || isAddingUserToFollowingList || isAddingUserToShareIdentityList || isRemovingUserFromBlockList || isAddingUserToBlockList || isAddingUserToMutedList || isRemovingUserFromMutedList;
    }

    public void onStopOverride() {
        if (loadMeProfileTask != null) {
            loadMeProfileTask.cancel();
        }
        if (loadUserNeverListTask != null) {
            loadUserNeverListTask.cancel();
        }
        if (loadUserMutedListTask != null) {
            loadUserMutedListTask.cancel();
        }
        if (loadUserProfileTask != null) {
            loadUserProfileTask.cancel();
        }
        if (addUserToFollowingListAsyncTask != null) {
            addUserToFollowingListAsyncTask.cancel();
        }
        if (addUserToShareIdentityListAsyncTask != null) {
            addUserToShareIdentityListAsyncTask.cancel();
        }
        if (addUserToNeverListAsyncTask != null) {
            addUserToNeverListAsyncTask.cancel();
        }
        if (removeUserToNeverListAsyncTask != null) {
            removeUserToNeverListAsyncTask.cancel();
        }
        if (addUserToMutedListAsyncTask != null) {
            addUserToMutedListAsyncTask.cancel();
        }
        if (removeUserFromMutedListAsyncTask != null) {
            removeUserFromMutedListAsyncTask.cancel();
        }
    }

    public void load(boolean forceRefresh) {
        if (loadUserProfileTask != null) {
            loadUserProfileTask.cancel();
        }
        loadMeProfileTask = new LoadUserProfileAsyncTask(ProfileModel.getMeProfileModel());
        loadMeProfileTask.load(true);
        if (!isMeProfile()) {
            if (loadUserNeverListTask != null) {
                loadUserNeverListTask.cancel();
            }
            loadUserNeverListTask = new LoadUserNeverListAsyncTask(ProfileModel.getMeProfileModel());
            loadUserNeverListTask.load(true);
            if (loadUserMutedListTask != null) {
                loadUserMutedListTask.cancel();
            }
            loadUserMutedListTask = new LoadUserMutedListAsyncTask(ProfileModel.getMeProfileModel());
            loadUserMutedListTask.load(true);
            loadUserProfileTask = new LoadUserProfileAsyncTask(model);
            loadUserProfileTask.load(true);
        }
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public boolean getIsMuted() {
        return isMuted;
    }

    public boolean getIsAddingUserToBlockList() {
        return isAddingUserToBlockList;
    }

    public boolean getIsRemovingUserFromBlockList() {
        return isRemovingUserFromBlockList;
    }

    public boolean getIsAddingUserToMutedList() {
        return isAddingUserToMutedList;
    }

    public boolean getIsRemovingUserFromMutedList() {
        return isRemovingUserFromMutedList;
    }

    public void navigateToChangeRelationship() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            UTCChangeRelationship.trackChangeRelationshipAction(getScreen().getName(), getXuid(), isCallerFollowingTarget(), isFacebookFriend());
            showChangeFriendshipDialog();
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void addFollowingUser() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            if (addUserToFollowingListAsyncTask != null) {
                addUserToFollowingListAsyncTask.cancel();
            }
            addUserToFollowingListAsyncTask = new AddUserToFollowingListAsyncTask(model.getXuid());
            addUserToFollowingListAsyncTask.load(true);
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void addUserToShareIdentityList() {
        if (addUserToShareIdentityListAsyncTask != null) {
            addUserToShareIdentityListAsyncTask.cancel();
        }
        ArrayList<String> users = new ArrayList<>();
        users.add(model.getXuid());
        addUserToShareIdentityListAsyncTask = new AddUserToShareIdentityListAsyncTask(users);
        addUserToShareIdentityListAsyncTask.load(true);
    }

    private void notifyDialogAsyncTaskCompleted() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskCompleted();
    }

    private void notifyDialogAsyncTaskFailed(String errorMessage) {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskFailed(errorMessage);
    }

    public void showChangeFriendshipDialog() {
        if (changeFriendshipDialogViewModel == null) {
            changeFriendshipDialogViewModel = new ChangeFriendshipDialogViewModel(model);
        }
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).showChangeFriendshipDialog(changeFriendshipDialogViewModel, this);
    }

    public void blockUser() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogTitle), XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogBody), XboxTcuiSdk.getResources().getString(R.string.OK_Text), new Runnable() {
            public void run() {
                blockUserInternal();
            }
        }, XboxTcuiSdk.getResources().getString(R.string.MessageDialog_Cancel), null);
        updateAdapter();
    }

    public void blockUserInternal() {
        UTCPeopleHub.trackBlockDialogComplete();
        if (addUserToNeverListAsyncTask != null) {
            addUserToNeverListAsyncTask.cancel();
        }
        addUserToNeverListAsyncTask = new AddUserToNeverListAsyncTask(model.getXuid());
        addUserToNeverListAsyncTask.load(true);
    }

    public void unblockUser() {
        if (removeUserToNeverListAsyncTask != null) {
            removeUserToNeverListAsyncTask.cancel();
        }
        removeUserToNeverListAsyncTask = new RemoveUserToNeverListAsyncTask(model.getXuid());
        removeUserToNeverListAsyncTask.load(true);
    }

    public void muteUser() {
        if (addUserToMutedListAsyncTask != null) {
            addUserToMutedListAsyncTask.cancel();
        }
        addUserToMutedListAsyncTask = new AddUserToMutedListAsyncTask(model.getXuid());
        addUserToMutedListAsyncTask.load(true);
    }

    public void unmuteUser() {
        if (removeUserFromMutedListAsyncTask != null) {
            removeUserFromMutedListAsyncTask.cancel();
        }
        removeUserFromMutedListAsyncTask = new RemoveUserFromMutedListAsyncTask(model.getXuid());
        removeUserFromMutedListAsyncTask.load(true);
    }

    public void showReportDialog() {
        try {
            NavigationManager.getInstance().PopScreensAndReplace(0, ReportUserScreen.class, false, false, false, NavigationManager.getInstance().getActivityParameters());
        } catch (XLEException e) {
            e.printStackTrace();
        }
    }

    public void launchXboxApp() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogTitle), XboxTcuiSdk.getResources().getString(R.string.Messages_ViewInXboxApp_DialogBody), XboxTcuiSdk.getResources().getString(R.string.ConnectDialog_ContinueAsGuest), new Runnable() {
            public void run() {
                UTCPeopleHub.trackViewInXboxAppDialogComplete();
                XboxAppDeepLinker.showUserProfile(XboxTcuiSdk.getActivity(), model.getXuid());
            }
        }, XboxTcuiSdk.getResources().getString(R.string.MessageDialog_Cancel), null);
        updateAdapter();
    }

    public boolean isFacebookFriend() {
        return false;
    }

    public void onLoadUserProfileCompleted(@NotNull AsyncActionStatus status) {
        isLoadingUserProfile = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                if (!isMeProfile() && ProfileModel.getMeProfileModel() != null) {
                    isFollowing = model.isCallerFollowingTarget();
                    break;
                }
            case FAIL:
            case NO_OP_FAIL:
                if (!isShowingFailureDialog) {
                    isShowingFailureDialog = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(XboxTcuiSdk.getActivity());
                    builder.setMessage(R.string.Service_ErrorText);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.OK_Text, (dialog, which) -> {
                        try {
                            NavigationManager.getInstance().PopAllScreens();
                        } catch (XLEException e) {
                            e.printStackTrace();
                        }
                    });
                    builder.create().show();
                    break;
                }
                break;
        }
        updateAdapter();
    }

    public void onLoadUserNeverListCompleted(@NotNull AsyncActionStatus status) {
        isLoadingUserNeverList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (!isMeProfile() && meProfile != null) {
                    isBlocked = false;
                    NeverListResultContainer.NeverListResult meNeverList = meProfile.getNeverListData();
                    if (meNeverList != null) {
                        isBlocked = meNeverList.contains(model.getXuid());
                        break;
                    }
                }
                break;
        }
        updateAdapter();
    }

    public void onLoadUserMutedListCompleted(@NotNull AsyncActionStatus status) {
        isLoadingUserMutedList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (!isMeProfile() && meProfile != null) {
                    isMuted = false;
                    MutedListResultContainer.MutedListResult meMutedList = meProfile.getMutedList();
                    if (meMutedList != null) {
                        isMuted = meMutedList.contains(model.getXuid());
                        break;
                    }
                }
                break;
        }
        updateAdapter();
    }

    public void onAddUserToFollowingListCompleted(@NotNull AsyncActionStatus status, boolean isFollowing2) {
        isAddingUserToFollowingList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                isFollowing = isFollowing2;
                XLEGlobalData.getInstance().AddForceRefresh(ProfileScreenViewModel.class);
                notifyDialogAsyncTaskCompleted();
                break;
            case FAIL:
            case NO_OP_FAIL:
                AddFollowingUserResponseContainer.AddFollowingUserResponse result = null;
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (meProfile != null) {
                    result = meProfile.getAddUserToFollowingResult();
                }
                if (result != null && !result.getAddFollowingRequestStatus() && result.code == 1028) {
                    notifyDialogAsyncTaskFailed(result.description);
                    break;
                } else {
                    notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorAddingFriend));
                    break;
                }
        }
        updateAdapter();
    }

    public void onAddUseToShareIdentityListCompleted(@NotNull AsyncActionStatus status) {
        isAddingUserToShareIdentityList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                notifyDialogAsyncTaskCompleted();
                break;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                break;
        }
        updateAdapter();
    }

    public void onAddUserToBlockListCompleted(@NotNull AsyncActionStatus status) {
        isAddingUserToBlockList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (meProfile != null) {
                    isBlocked = false;
                    NeverListResultContainer.NeverListResult neverList = meProfile.getNeverListData();
                    if (neverList != null) {
                        isBlocked = neverList.contains(model.getXuid());
                    }
                    isFollowing = false;
                    break;
                }
                break;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Messages_Error_FailedToBlockUser);
                break;
        }
        updateAdapter();
    }

    public void onRemoveUserFromBlockListCompleted(@NotNull AsyncActionStatus status) {
        isRemovingUserFromBlockList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (meProfile != null) {
                    isBlocked = false;
                    NeverListResultContainer.NeverListResult neverList = meProfile.getNeverListData();
                    if (neverList != null) {
                        isBlocked = neverList.contains(model.getXuid());
                        break;
                    }
                }
                break;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Messages_Error_FailedToUnblockUser);
                break;
        }
        updateAdapter();
    }

    public void onAddUserToMutedListCompleted(@NotNull AsyncActionStatus status) {
        isAddingUserToMutedList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                if (ProfileModel.getMeProfileModel() != null) {
                    isMuted = true;
                    break;
                }
                break;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Messages_Error_FailedToMuteUser);
                break;
        }
        updateAdapter();
    }

    public void onRemoveUserFromMutedListCompleted(@NotNull AsyncActionStatus status) {
        isRemovingUserFromMutedList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                if (ProfileModel.getMeProfileModel() != null) {
                    isMuted = false;
                    break;
                }
                break;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Messages_Error_FailedToUnmuteUser);
                break;
        }
        updateAdapter();
    }

    public enum ChangeFriendshipFormOptions {
        ShouldAddUserToFriendList,
        ShouldRemoveUserFromFriendList,
        ShouldAddUserToFavoriteList,
        ShouldRemoveUserFromFavoriteList,
        ShouldAddUserToShareIdentityList,
        ShouldRemoveUserFromShareIdentityList
    }

    private class LoadUserProfileAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserProfileAsyncTask(ProfileModel model2) {
            model = model2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return model.shouldRefresh() || model.shouldRefreshProfileSummary();
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isLoadingUserProfile = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onLoadUserProfileCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(model);
            AsyncActionStatus status = model.loadSync(forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? model.loadProfileSummary(forceLoad).getStatus() : status;
        }
    }

    private class LoadUserNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserNeverListAsyncTask(ProfileModel model2) {
            model = model2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return model.shouldRefresh();
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isLoadingUserNeverList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onLoadUserNeverListCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(model);
            AsyncActionStatus status = model.loadSync(forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? model.loadUserNeverList(true).getStatus() : status;
        }
    }

    private class LoadUserMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserMutedListAsyncTask(ProfileModel model2) {
            model = model2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return model.shouldRefresh();
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isLoadingUserMutedList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onLoadUserMutedListCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(model);
            AsyncActionStatus status = model.loadSync(forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? model.loadUserMutedList(true).getStatus() : status;
        }
    }

    private class AddUserToFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = false;

        public AddUserToFollowingListAsyncTask(String followingUserXuid2) {
            followingUserXuid = followingUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onAddUserToFollowingListCompleted(AsyncActionStatus.NO_CHANGE, isFollowingUser);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isAddingUserToFollowingList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onAddUserToFollowingListCompleted(result, isFollowingUser);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.addUserToFollowingList(forceLoad, followingUserXuid).getStatus();
            if (AsyncActionStatus.getIsFail(status)) {
                return status;
            }
            AddFollowingUserResponseContainer.AddFollowingUserResponse response = meProfile.getAddUserToFollowingResult();
            if (response != null && !response.getAddFollowingRequestStatus() && response.code == 1028) {
                return AsyncActionStatus.FAIL;
            }
            model.loadProfileSummary(true);
            meProfile.loadProfileSummary(true);
            ArrayList<FollowersData> followersList = meProfile.getFollowingData();
            if (followersList == null) {
                return status;
            }
            Iterator<FollowersData> it = followersList.iterator();
            while (it.hasNext()) {
                if (it.next().xuid.equals(followingUserXuid)) {
                    isFollowingUser = true;
                    return status;
                }
            }
            return status;
        }
    }

    private class AddUserToShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        public AddUserToShareIdentityListAsyncTask(ArrayList<String> users) {
            usersToAdd = users;
        }

        public boolean checkShouldExecute() {
            return true;
        }

        public void onNoAction() {
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isAddingUserToShareIdentityList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            onAddUseToShareIdentityListCompleted(asyncActionStatus);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.addUserToShareIdentity(forceLoad, usersToAdd).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }

    private class AddUserToNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String blockUserXuid;

        public AddUserToNeverListAsyncTask(String blockUserXuid2) {
            blockUserXuid = blockUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onAddUserToBlockListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isAddingUserToBlockList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onAddUserToBlockListCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.addUserToNeverList(forceLoad, blockUserXuid).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }

    private class RemoveUserToNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String unblockUserXuid;

        public RemoveUserToNeverListAsyncTask(String blockUserXuid) {
            unblockUserXuid = blockUserXuid;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onRemoveUserFromBlockListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isRemovingUserFromBlockList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onRemoveUserFromBlockListCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.removeUserFromNeverList(forceLoad, unblockUserXuid).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }

    private class AddUserToMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String mutedUserXuid;

        public AddUserToMutedListAsyncTask(String mutedUserXuid2) {
            mutedUserXuid = mutedUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onAddUserToMutedListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isAddingUserToMutedList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onAddUserToMutedListCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.addUserToMutedList(forceLoad, mutedUserXuid).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }

    private class RemoveUserFromMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String mutedUserXuid;

        public RemoveUserFromMutedListAsyncTask(String mutedUserXuid2) {
            mutedUserXuid = mutedUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onRemoveUserFromMutedListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isRemovingUserFromMutedList = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onRemoveUserFromMutedListCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.removeUserFromMutedList(forceLoad, mutedUserXuid).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }
}
