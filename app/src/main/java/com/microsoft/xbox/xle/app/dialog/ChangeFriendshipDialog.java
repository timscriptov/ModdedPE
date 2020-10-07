package com.microsoft.xbox.xle.app.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEManagedDialog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.FastProgressBar;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLECheckBox;
import com.microsoft.xbox.toolkit.ui.XLEClickableLayout;
import com.microsoft.xbox.toolkit.ui.XLEUniversalImageView;
import com.microsoft.xbox.xle.app.ImageUtil;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ChangeFriendshipDialog extends XLEManagedDialog {
    public SwitchPanel changeFriendshipSwitchPanel;
    public ChangeFriendshipDialogViewModel vm;
    private RadioButton addFavorite;
    private RadioButton addFriend;
    private XLEButton cancelButton;
    private XLEButton confirmButton;
    private CustomTypefaceTextView favoriteIconView;
    private CustomTypefaceTextView gamertag;
    private FastProgressBar overlayLoadingIndicator;
    private ViewModelBase previousVM;
    private TextView profileAccountTier;
    private CustomTypefaceTextView profileGamerScore;
    private XLEUniversalImageView profilePic;
    private CustomTypefaceTextView realName;
    private XLEClickableLayout removeFriendLayout;
    private XLECheckBox shareRealNameCheckbox;

    public ChangeFriendshipDialog(Context context, ChangeFriendshipDialogViewModel vm2, ViewModelBase previousVM2) {
        super(context, R.style.TcuiDialog);
        previousVM = previousVM2;
        vm = vm2;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setLayout(-1, -1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.change_friendship_dialog);
        profilePic = findViewById(R.id.change_friendship_profile_pic);
        gamertag = findViewById(R.id.gamertag_text);
        realName = findViewById(R.id.realname_text);
        profileAccountTier = findViewById(R.id.peoplehub_info_gamerscore_icon);
        profileGamerScore = findViewById(R.id.peoplehub_info_gamerscore);
        addFriend = findViewById(R.id.add_as_friend);
        addFavorite = findViewById(R.id.add_as_favorite);
        shareRealNameCheckbox = findViewById(R.id.share_real_name_checkbox);
        confirmButton = findViewById(R.id.submit_button);
        cancelButton = findViewById(R.id.cancel_button);
        changeFriendshipSwitchPanel = findViewById(R.id.change_friendship_switch_panel);
        removeFriendLayout = findViewById(R.id.remove_friend_btn_layout);
        favoriteIconView = findViewById(R.id.people_favorites_icon);
        overlayLoadingIndicator = findViewById(R.id.overlay_loading_indicator);
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 5;
        XLEButton closeButton = new XLEButton(getContext());
        closeButton.setPadding(60, 0, 0, 0);
        closeButton.setBackgroundResource(R.drawable.common_button_background);
        closeButton.setText(R.string.ic_Close);
        closeButton.setTextColor(-1);
        closeButton.setTextSize(2, 14.0f);
        closeButton.setTypeFace("fonts/SegXboxSymbol.ttf");
        closeButton.setContentDescription(getContext().getResources().getString(R.string.TextInput_Confirm));
        closeButton.setOnClickListener(v -> {
            try {
                dismiss();
                NavigationManager.getInstance().PopAllScreens();
            } catch (XLEException e) {
            }
        });
        closeButton.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode != 4 || event.getAction() != 1) {
                return false;
            }
            dismiss();
            return true;
        });
        frameLayout.addView(closeButton);
        addContentView(frameLayout, layoutParams);
    }

    public void onStart() {
        vm.load();
        updateView();
        changeFriendshipSwitchPanel.setBackgroundColor(vm.getPreferredColor());
        UTCChangeRelationship.trackChangeRelationshipView(getActivityName(), vm.getXuid());
    }

    @SuppressLint("WrongConstant")
    public void updateView() {
        if (vm.getViewModelState() == ListState.ValidContentState) {
            setDialogValidContentView();
            XLEUtil.updateAndShowTextViewUnlessEmpty(gamertag, vm.getGamerTag());
            if (profilePic != null) {
                profilePic.setImageURI2(ImageUtil.getMedium(vm.getGamerPicUrl()), R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
            }
            XLEUtil.updateAndShowTextViewUnlessEmpty(realName, vm.getRealName());
            XLEUtil.updateVisibilityIfNotNull(favoriteIconView, vm.getIsFavorite() ? 0 : 4);
            if (vm.getIsFavorite()) {
                favoriteIconView.setTextColor(getContext().getResources().getColor(R.color.XboxGreen));
            }
            String gamerScore = vm.getGamerScore();
            if (gamerScore != null && !gamerScore.equalsIgnoreCase("0")) {
                XLEUtil.updateAndShowTextViewUnlessEmpty(profileGamerScore, vm.getGamerScore());
                XLEUtil.updateVisibilityIfNotNull(profileAccountTier, 0);
            }
            if (addFriend != null) {
                if (vm.getIsFollowing()) {
                    addFriend.setChecked(true);
                } else {
                    vm.setShouldAddUserToFriendList(true);
                    addFriend.setChecked(true);
                }
                addFriend.setOnClickListener(v -> {
                    if (!vm.getIsFollowing()) {
                        vm.setShouldAddUserToFriendList(true);
                    }
                    if (vm.getIsFavorite()) {
                        vm.setShouldRemoveUserFromFavoriteList(true);
                    }
                    vm.setShouldAddUserToFavoriteList(false);
                });
            }
            if (addFavorite != null) {
                if (vm.getIsFavorite()) {
                    addFavorite.setChecked(true);
                }
                addFavorite.setOnClickListener(v -> {
                    if (!vm.getIsFavorite()) {
                        vm.setShouldAddUserToFavoriteList(true);
                    }
                    vm.setShouldRemoveUserFromFavoriteList(false);
                });
            }
            if (confirmButton != null) {
                confirmButton.setOnClickListener(v -> {
                    changeFriendshipSwitchPanel.setState(1);
                    vm.onChangeRelationshipCompleted();
                });
            }
            if (cancelButton != null) {
                cancelButton.setOnClickListener(v -> {
                    dismissSelf();
                    vm.clearChangeFriendshipForm();
                });
            }
            if (shareRealNameCheckbox != null) {
                shareRealNameCheckbox.setChecked(vm.getCallerMarkedTargetAsIdentityShared());
                shareRealNameCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    vm.setIsSharingRealNameEnd(isChecked);
                    if (isChecked) {
                        if (!vm.getCallerMarkedTargetAsIdentityShared()) {
                            vm.setShouldAddUserToShareIdentityList(true);
                        }
                        vm.setShouldRemoveUserFroShareIdentityList(false);
                        return;
                    }
                    if (vm.getCallerMarkedTargetAsIdentityShared()) {
                        vm.setShouldRemoveUserFroShareIdentityList(true);
                    }
                    vm.setShouldAddUserToShareIdentityList(false);
                });
                updateShareIdentityCheckboxStatus();
            }
            if (removeFriendLayout != null) {
                if (vm.getIsFollowing()) {
                    removeFriendLayout.setVisibility(0);
                    removeFriendLayout.setOnClickListener(v -> {
                        UTCChangeRelationship.trackChangeRelationshipRemoveFriend();
                        changeFriendshipSwitchPanel.setState(1);
                        vm.removeFollowingUser();
                    });
                } else {
                    removeFriendLayout.setEnabled(false);
                    removeFriendLayout.setVisibility(8);
                }
                confirmButton.setText(vm.getDialogButtonText());
            }
            updateShareIdentityCheckboxStatus();
        } else if (vm.getViewModelState() == ListState.LoadingState) {
            setDialogLoadingView();
        }
    }

    private void setDialogValidContentView() {
        XLEUtil.updateVisibilityIfNotNull(overlayLoadingIndicator, 8);
        if (confirmButton != null) {
            confirmButton.setEnabled(true);
        }
        if (cancelButton != null) {
            cancelButton.setEnabled(true);
        }
    }

    private void setDialogLoadingView() {
        XLEUtil.updateVisibilityIfNotNull(overlayLoadingIndicator, 0);
        if (confirmButton != null) {
            confirmButton.setEnabled(false);
        }
        if (cancelButton != null) {
            cancelButton.setEnabled(false);
        }
    }

    public void closeDialog() {
        dismissSelf();
        previousVM.load(true);
    }

    public void onStop() {
    }

    public void onBackPressed() {
        dismissSelf();
    }

    public void dismissSelf() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).dismissChangeFriendshipDialog();
    }

    public void setVm(ChangeFriendshipDialogViewModel vm2) {
        vm = vm2;
    }

    public void updateShareIdentityCheckboxStatus() {
        int i;
        boolean isTargetSharingRealName;
        String callerShareRealNameStatus = vm.getCallerShareRealNameStatus();
        if (callerShareRealNameStatus != null) {
            boolean isBlocked = callerShareRealNameStatus.equalsIgnoreCase("Blocked");
            XLECheckBox xLECheckBox = shareRealNameCheckbox;
            if (isBlocked) {
                i = 8;
            } else {
                i = 0;
            }
            xLECheckBox.setVisibility(i);
            if (!isBlocked) {
                if (!JavaUtil.isNullOrEmpty(vm.getRealName())) {
                    isTargetSharingRealName = true;
                } else {
                    isTargetSharingRealName = false;
                }
                if (callerShareRealNameStatus.compareToIgnoreCase("Everyone") == 0) {
                    shareRealNameCheckbox.setChecked(true);
                    vm.setInitialRealNameSharingState(true);
                    shareRealNameCheckbox.setEnabled(false);
                    shareRealNameCheckbox.setSubText(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName_Everyone));
                }
                if (callerShareRealNameStatus.compareToIgnoreCase("PeopleOnMyList") == 0) {
                    shareRealNameCheckbox.setChecked(true);
                    vm.setInitialRealNameSharingState(true);
                    shareRealNameCheckbox.setEnabled(false);
                    shareRealNameCheckbox.setSubText(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName_Friends));
                }
                if (callerShareRealNameStatus.compareToIgnoreCase("FriendCategoryShareIdentity") == 0) {
                    if (vm.getIsFollowing()) {
                        if (vm.getCallerMarkedTargetAsIdentityShared()) {
                            shareRealNameCheckbox.setChecked(true);
                            vm.setInitialRealNameSharingState(true);
                        } else {
                            shareRealNameCheckbox.setChecked(false);
                            vm.setInitialRealNameSharingState(false);
                        }
                    } else if (isTargetSharingRealName) {
                        shareRealNameCheckbox.setChecked(true);
                        vm.setInitialRealNameSharingState(true);
                        vm.setShouldAddUserToShareIdentityList(true);
                    } else {
                        shareRealNameCheckbox.setChecked(false);
                        vm.setInitialRealNameSharingState(false);
                    }
                    shareRealNameCheckbox.setSubText(String.format(XboxTcuiSdk.getResources().getString(R.string.ChangeRelationship_Checkbox_Subtext_ShareRealName), new Object[]{vm.getGamerTag()}));
                    shareRealNameCheckbox.setEnabled(true);
                }
            }
        }
    }

    public void reportAsyncTaskCompleted() {
        if (!vm.isBusy() && changeFriendshipSwitchPanel.getState() == 1) {
            closeDialog();
        }
    }

    @SuppressLint("WrongConstant")
    public void reportAsyncTaskFailed(String errorMessage) {
        if (changeFriendshipSwitchPanel.getState() == 1) {
            changeFriendshipSwitchPanel.setState(0);
            Toast.makeText(XboxTcuiSdk.getActivity(), errorMessage, 0).show();
        }
        updateView();
    }

    public String getActivityName() {
        return "ChangeRelationship Info";
    }
}
