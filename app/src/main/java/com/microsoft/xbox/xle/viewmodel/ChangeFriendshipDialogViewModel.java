package com.microsoft.xbox.xle.viewmodel;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ChangeFriendshipDialogViewModel {
    private static final String TAG = ChangeFriendshipDialogViewModel.class.getSimpleName();
    public boolean isAddingUserToFavoriteList;
    public boolean isAddingUserToFollowingList;
    public boolean isAddingUserToShareIdentityList;
    public boolean isLoadingUserProfile;
    public boolean isRemovingUserFromFavoriteList;
    public boolean isRemovingUserFromFollowingList;
    public boolean isRemovingUserFromShareIdentityList;
    public ProfileModel model;
    private AddUserToFavoriteListAsyncTask addUserToFavoriteListAsyncTask;
    private AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask;
    private AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask;
    private HashSet<ProfileScreenViewModel.ChangeFriendshipFormOptions> changeFriendshipForm = new HashSet<>();
    private boolean isFavorite = false;
    private boolean isFollowing = false;
    private boolean isSharingRealNameEnd;
    private boolean isSharingRealNameStart;
    private LoadPersonDataAsyncTask loadProfileAsyncTask;
    private RemoveUserFromFavoriteListAsyncTask removeUserFromFavoriteListAsyncTask;
    private RemoveUserFromFollowingListAsyncTask removeUserFromFollowingListAsyncTask;
    private RemoveUserFromShareIdentityListAsyncTask removeUserFromShareIdentityListAsyncTask;
    private ListState viewModelState = ListState.LoadingState;

    public ChangeFriendshipDialogViewModel(@NotNull ProfileModel model2) {
        boolean z = false;
        XLEAssert.assertTrue(!ProfileModel.isMeXuid(model2.getXuid()) ? true : z);
        model = model2;
    }

    public ListState getViewModelState() {
        return viewModelState;
    }

    public String getGamerTag() {
        return model.getGamerTag();
    }

    public String getGamerPicUrl() {
        return model.getGamerPicImageUrl();
    }

    public String getRealName() {
        return model.getRealName();
    }

    public String getGamerScore() {
        return model.getGamerScore();
    }

    public int getPreferredColor() {
        return model.getPreferedColor();
    }

    public boolean getIsFollowing() {
        return model.isCallerFollowingTarget();
    }

    public boolean getIsFavorite() {
        return model.hasCallerMarkedTargetAsFavorite();
    }

    public String getXuid() {
        return model.getXuid();
    }

    public boolean getCallerMarkedTargetAsIdentityShared() {
        return model.hasCallerMarkedTargetAsIdentityShared();
    }

    public String getCallerShareRealNameStatus() {
        ProfileModel meProfile = ProfileModel.getMeProfileModel();
        if (meProfile != null) {
            return meProfile.getShareRealNameStatus();
        }
        return "";
    }

    public String getCallerGamerTag() {
        ProfileModel meProfile = ProfileModel.getMeProfileModel();
        if (meProfile != null) {
            return meProfile.getGamerTag();
        }
        return "";
    }

    public void setShouldAddUserToFriendList(boolean shouldAddUserToFriendList) {
        if (shouldAddUserToFriendList) {
            changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFriendList);
        } else {
            changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFriendList);
        }
    }

    public void setShouldAddUserToFavoriteList(boolean shouldAddUserToFavoriteList) {
        if (shouldAddUserToFavoriteList) {
            changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList);
        } else {
            changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList);
        }
    }

    public void setShouldRemoveUserFromFavoriteList(boolean shouldRemoveUserFromFavoriteList) {
        if (shouldRemoveUserFromFavoriteList) {
            changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList);
        } else {
            changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList);
        }
    }

    public void setShouldAddUserToShareIdentityList(boolean shouldAddUserToShareIdentityList) {
        if (shouldAddUserToShareIdentityList) {
            changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList);
        } else {
            changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList);
        }
    }

    public void setShouldRemoveUserFroShareIdentityList(boolean shouldRemoveUserFroShareIdentityList) {
        if (shouldRemoveUserFroShareIdentityList) {
            changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList);
        } else {
            changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList);
        }
    }

    public void onChangeRelationshipCompleted() {
        boolean willPerformAsyncAction = false;
        UTCChangeRelationship.Relationship relationship = model.isCallerFollowingTarget() ? UTCChangeRelationship.Relationship.EXISTINGFRIEND : UTCChangeRelationship.Relationship.NOTCHANGED;
        UTCChangeRelationship.FavoriteStatus favoriteStatus = model.hasCallerMarkedTargetAsFavorite() ? UTCChangeRelationship.FavoriteStatus.EXISTINGFAVORITE : UTCChangeRelationship.FavoriteStatus.EXISTINGNOTFAVORITED;
        UTCChangeRelationship.RealNameStatus realNameStatus = model.hasCallerMarkedTargetAsIdentityShared() ? UTCChangeRelationship.RealNameStatus.EXISTINGSHARED : UTCChangeRelationship.RealNameStatus.EXISTINGNOTSHARED;
        UTCChangeRelationship.GamerType gamerType = UTCChangeRelationship.GamerType.NORMAL;
        if (changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFriendList)) {
            relationship = UTCChangeRelationship.Relationship.ADDFRIEND;
            addFollowingUser();
            willPerformAsyncAction = true;
        }
        if (changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFriendList)) {
            relationship = UTCChangeRelationship.Relationship.REMOVEFRIEND;
            removeFollowingUser();
            willPerformAsyncAction = true;
        }
        if (changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList)) {
            favoriteStatus = UTCChangeRelationship.FavoriteStatus.FAVORITED;
            addFavoriteUser();
            willPerformAsyncAction = true;
        }
        if (changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList)) {
            favoriteStatus = UTCChangeRelationship.FavoriteStatus.UNFAVORITED;
            removeFavoriteUser();
            willPerformAsyncAction = true;
        }
        if (changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList)) {
            realNameStatus = UTCChangeRelationship.RealNameStatus.SHARINGON;
            addUserToShareIdentityList();
            willPerformAsyncAction = true;
        }
        if (changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList)) {
            realNameStatus = UTCChangeRelationship.RealNameStatus.SHARINGOFF;
            removeUserFromShareIdentityList();
            willPerformAsyncAction = true;
        }
        if (!willPerformAsyncAction) {
            notifyDialogAsyncTaskCompleted();
        } else {
            UTCChangeRelationship.trackChangeRelationshipDone(relationship, realNameStatus, favoriteStatus, gamerType);
        }
    }

    public void clearChangeFriendshipForm() {
        changeFriendshipForm.clear();
    }

    public void setInitialRealNameSharingState(boolean state) {
        isSharingRealNameStart = state;
        isSharingRealNameEnd = state;
    }

    public boolean getIsSharingRealNameStart() {
        return isSharingRealNameStart;
    }

    public boolean getIsSharingRealNameEnd() {
        return isSharingRealNameEnd;
    }

    public void setIsSharingRealNameEnd(boolean state) {
        isSharingRealNameEnd = state;
    }

    private void showError(int contentResId) {
        DialogManager.getInstance().showToast(contentResId);
    }

    public String getDialogButtonText() {
        if (isFollowing) {
            return XboxTcuiSdk.getResources().getString(R.string.TextInput_Confirm);
        }
        return XboxTcuiSdk.getResources().getString(R.string.OK_Text);
    }

    public boolean isBusy() {
        return isLoadingUserProfile || isAddingUserToFavoriteList || isRemovingUserFromFavoriteList || isAddingUserToFollowingList || isRemovingUserFromFollowingList || isAddingUserToShareIdentityList || isRemovingUserFromShareIdentityList;
    }

    public void load() {
        if (loadProfileAsyncTask != null) {
            loadProfileAsyncTask.cancel();
        }
        loadProfileAsyncTask = new LoadPersonDataAsyncTask();
        loadProfileAsyncTask.load(true);
    }

    public void addFavoriteUser() {
        if (addUserToFavoriteListAsyncTask != null) {
            addUserToFavoriteListAsyncTask.cancel();
        }
        addUserToFavoriteListAsyncTask = new AddUserToFavoriteListAsyncTask(model.getXuid());
        addUserToFavoriteListAsyncTask.load(true);
    }

    public void removeFavoriteUser() {
        if (removeUserFromFavoriteListAsyncTask != null) {
            removeUserFromFavoriteListAsyncTask.cancel();
        }
        removeUserFromFavoriteListAsyncTask = new RemoveUserFromFavoriteListAsyncTask(model.getXuid());
        removeUserFromFavoriteListAsyncTask.load(true);
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

    public void removeUserFromShareIdentityList() {
        if (removeUserFromFollowingListAsyncTask != null) {
            removeUserFromFavoriteListAsyncTask.cancel();
        }
        ArrayList<String> users = new ArrayList<>();
        users.add(model.getXuid());
        removeUserFromShareIdentityListAsyncTask = new RemoveUserFromShareIdentityListAsyncTask(users);
        removeUserFromShareIdentityListAsyncTask.load(true);
    }

    private void notifyDialogUpdateView() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogUpdateView();
    }

    private void notifyDialogAsyncTaskCompleted() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskCompleted();
    }

    private void notifyDialogAsyncTaskFailed(String errorMessage) {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskFailed(errorMessage);
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

    public void removeFollowingUser() {
        if (removeUserFromFollowingListAsyncTask != null) {
            removeUserFromFollowingListAsyncTask.cancel();
        }
        removeUserFromFollowingListAsyncTask = new RemoveUserFromFollowingListAsyncTask(model.getXuid());
        removeUserFromFollowingListAsyncTask.load(true);
    }

    public void onLoadPersonDataCompleted(@NotNull AsyncActionStatus status) {
        isLoadingUserProfile = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                if (model.getProfileSummaryData() == null) {
                    viewModelState = ListState.ErrorState;
                    break;
                } else {
                    viewModelState = ListState.ValidContentState;
                    break;
                }
            case FAIL:
            case NO_OP_FAIL:
                viewModelState = ListState.ErrorState;
                break;
        }
        notifyDialogUpdateView();
    }

    public void onAddUseToShareIdentityListCompleted(@NotNull AsyncActionStatus status) {
        isAddingUserToShareIdentityList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    public void onRemoveUserFromShareIdentityListCompleted(@NotNull AsyncActionStatus status) {
        isRemovingUserFromShareIdentityList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    public void onAddUserToFavoriteListCompleted(@NotNull AsyncActionStatus status, boolean isFavorite2) {
        isAddingUserToFavoriteList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                isFavorite = isFavorite2;
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    public void onRemoveUserFromFavoriteListCompleted(@NotNull AsyncActionStatus status, boolean isFavorite2) {
        isRemovingUserFromFavoriteList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                isFavorite = isFavorite2;
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    public void onAddUserToFollowingListCompleted(@NotNull AsyncActionStatus status, boolean isFollowing2) {
        isAddingUserToFollowingList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                isFollowing = isFollowing2;
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                AddFollowingUserResponseContainer.AddFollowingUserResponse result = null;
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (meProfile != null) {
                    result = meProfile.getAddUserToFollowingResult();
                }
                if (result == null || result.getAddFollowingRequestStatus() || result.code != 1028) {
                    notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorAddingFriend));
                    return;
                } else {
                    notifyDialogAsyncTaskFailed(result.description);
                    return;
                }
            default:
                return;
        }
    }

    public void onRemoveUserFromFollowingListCompleted(@NotNull AsyncActionStatus status, boolean isFollowing2) {
        isRemovingUserFromFollowingList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                isFollowing = isFollowing2;
                if (isFavorite && !isFollowing) {
                    isFavorite = false;
                }
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private class LoadPersonDataAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private LoadPersonDataAsyncTask() {
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return false;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onLoadPersonDataCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isLoadingUserProfile = true;
        }

        public void onPostExecute(AsyncActionStatus result) {
            onLoadPersonDataCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(model);
            return model.loadProfileSummary(forceLoad).getStatus();
        }
    }

    private class RemoveUserFromShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        public RemoveUserFromShareIdentityListAsyncTask(ArrayList<String> users) {
            usersToAdd = users;
        }

        public boolean checkShouldExecute() {
            return true;
        }

        public void onNoAction() {
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isRemovingUserFromShareIdentityList = true;
        }

        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            onRemoveUserFromShareIdentityListCompleted(asyncActionStatus);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.removeUserFromShareIdentity(forceLoad, usersToAdd).getStatus();
            }
            return AsyncActionStatus.FAIL;
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

    private class AddUserToFavoriteListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private boolean favoriteUser = false;
        private String favoriteUserXuid;

        public AddUserToFavoriteListAsyncTask(String favoriteUserXuid2) {
            favoriteUserXuid = favoriteUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onAddUserToFavoriteListCompleted(AsyncActionStatus.NO_CHANGE, favoriteUser);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isAddingUserToFavoriteList = true;
        }

        public void onPostExecute(AsyncActionStatus result) {
            onAddUserToFavoriteListCompleted(result, favoriteUser);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ArrayList<FollowersData> favoriteList;
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.addUserToFavoriteList(forceLoad, favoriteUserXuid).getStatus();
            if ((status != AsyncActionStatus.SUCCESS && status != AsyncActionStatus.NO_CHANGE && status != AsyncActionStatus.NO_OP_SUCCESS) || (favoriteList = meProfile.getFavorites()) == null) {
                return status;
            }
            Iterator<FollowersData> it = favoriteList.iterator();
            while (it.hasNext()) {
                FollowersData fData = it.next();
                if (fData.xuid.equals(favoriteUserXuid)) {
                    favoriteUser = fData.isFavorite;
                    return status;
                }
            }
            return status;
        }
    }

    private class RemoveUserFromFavoriteListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private boolean favoriteUser = false;
        private String favoriteUserXuid;

        public RemoveUserFromFavoriteListAsyncTask(String favoriteUserXuid2) {
            favoriteUserXuid = favoriteUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onRemoveUserFromFavoriteListCompleted(AsyncActionStatus.NO_CHANGE, favoriteUser);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isRemovingUserFromFavoriteList = true;
        }

        public void onPostExecute(AsyncActionStatus result) {
            onRemoveUserFromFavoriteListCompleted(result, favoriteUser);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ArrayList<FollowersData> favoriteList;
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.removeUserFromFavoriteList(forceLoad, favoriteUserXuid).getStatus();
            if ((status != AsyncActionStatus.SUCCESS && status != AsyncActionStatus.NO_CHANGE && status != AsyncActionStatus.NO_OP_SUCCESS) || (favoriteList = meProfile.getFavorites()) == null) {
                return status;
            }
            Iterator<FollowersData> it = favoriteList.iterator();
            while (it.hasNext()) {
                FollowersData fData = it.next();
                if (fData.xuid.equals(favoriteUserXuid)) {
                    favoriteUser = fData.isFavorite;
                    return status;
                }
            }
            return status;
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

    private class RemoveUserFromFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = true;

        public RemoveUserFromFollowingListAsyncTask(String followingUserXuid2) {
            followingUserXuid = followingUserXuid2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onRemoveUserFromFollowingListCompleted(AsyncActionStatus.NO_CHANGE, isFollowingUser);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isRemovingUserFromFollowingList = true;
        }

        public void onPostExecute(AsyncActionStatus result) {
            onRemoveUserFromFollowingListCompleted(result, isFollowingUser);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.removeUserFromFollowingList(forceLoad, followingUserXuid).getStatus();
            if (AsyncActionStatus.getIsFail(status)) {
                return status;
            }
            model.loadProfileSummary(true);
            meProfile.loadProfileSummary(true);
            isFollowingUser = false;
            return status;
        }
    }
}