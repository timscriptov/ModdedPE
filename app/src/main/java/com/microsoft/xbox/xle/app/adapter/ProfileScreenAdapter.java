package com.microsoft.xbox.xle.app.adapter;

import android.annotation.SuppressLint;
import android.widget.ScrollView;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.FastProgressBar;
import com.microsoft.xbox.toolkit.ui.XLERoundedUniversalImageView;
import com.microsoft.xbox.xle.app.ImageUtil;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel;
import com.microsoft.xbox.xle.ui.IconFontToggleButton;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.XboxAppDeepLinker;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProfileScreenAdapter extends AdapterBase {
    public IconFontToggleButton blockButton = ((IconFontToggleButton) findViewById(R.id.profile_block));
    public IconFontToggleButton muteButton = ((IconFontToggleButton) findViewById(R.id.profile_mute));
    public ProfileScreenViewModel viewModel;
    private ScrollView contentScrollView = ((ScrollView) findViewById(R.id.profile_screen_content_list));
    private IconFontToggleButton followButton = ((IconFontToggleButton) findViewById(R.id.profile_follow));
    private XLERoundedUniversalImageView gamerPicImageView = ((XLERoundedUniversalImageView) findViewById(R.id.profile_gamerpic));
    private CustomTypefaceTextView gamerscoreIconTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamerscore_icon));
    private CustomTypefaceTextView gamerscoreTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamerscore));
    private CustomTypefaceTextView gamertagTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamertag));
    private FastProgressBar loadingProgressBar = ((FastProgressBar) findViewById(R.id.profile_screen_loading));
    private CustomTypefaceTextView realNameTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_realname));
    private IconFontToggleButton reportButton = ((IconFontToggleButton) findViewById(R.id.profile_report));
    private XLERootView rootView = ((XLERootView) findViewById(R.id.profile_root));
    private IconFontToggleButton viewInXboxAppButton = ((IconFontToggleButton) findViewById(R.id.profile_view_in_xbox_app));
    private CustomTypefaceTextView viewInXboxAppSubTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_view_in_xbox_app_subtext));

    @SuppressLint("WrongConstant")
    public ProfileScreenAdapter(ProfileScreenViewModel viewModel2) {
        super(viewModel2);
        viewModel = viewModel2;
        viewInXboxAppButton.setVisibility(0);
        viewInXboxAppButton.setEnabled(true);
        viewInXboxAppButton.setChecked(true);
        if (viewModel.isMeProfile()) {
            followButton.setVisibility(8);
            muteButton.setVisibility(8);
            blockButton.setVisibility(8);
            reportButton.setVisibility(8);
            viewInXboxAppSubTextView.setText(R.string.Profile_ViewInXboxApp_Details_MeProfile);
            return;
        }
        followButton.setVisibility(0);
        followButton.setEnabled(true);
        muteButton.setVisibility(0);
        muteButton.setEnabled(true);
        muteButton.setChecked(false);
        blockButton.setVisibility(0);
        blockButton.setEnabled(false);
        reportButton.setVisibility(0);
        reportButton.setEnabled(true);
        reportButton.setChecked(false);
        viewInXboxAppSubTextView.setText(R.string.Profile_ViewInXboxApp_Details_YouProfile);
    }

    @SuppressLint("WrongConstant")
    public void onStart() {
        super.onStart();
        if (followButton != null) {
            followButton.setOnClickListener(v -> viewModel.navigateToChangeRelationship());
        }
        if (muteButton != null) {
            muteButton.setOnClickListener(v -> {
                muteButton.toggle();
                muteButton.setEnabled(false);
                if (muteButton.isChecked()) {
                    UTCPeopleHub.trackMute(true);
                    viewModel.muteUser();
                    return;
                }
                UTCPeopleHub.trackMute(false);
                viewModel.unmuteUser();
            });
        }
        if (blockButton != null) {
            blockButton.setOnClickListener(v -> {
                blockButton.toggle();
                blockButton.setEnabled(false);
                if (blockButton.isChecked()) {
                    UTCPeopleHub.trackBlock();
                    viewModel.blockUser();
                    return;
                }
                UTCPeopleHub.trackUnblock();
                viewModel.unblockUser();
            });
        }
        if (reportButton != null) {
            reportButton.setOnClickListener(v -> {
                UTCPeopleHub.trackReport();
                viewModel.showReportDialog();
            });
        }
        if (viewInXboxAppButton == null) {
            return;
        }
        if (XboxAppDeepLinker.appDeeplinkingSupported()) {
            viewInXboxAppButton.setOnClickListener(v -> {
                UTCPeopleHub.trackViewInXboxApp();
                viewModel.launchXboxApp();
            });
            return;
        }
        viewInXboxAppButton.setVisibility(8);
        viewInXboxAppSubTextView.setVisibility(8);
    }

    @SuppressLint("WrongConstant")
    public void updateViewOverride() {
        int i;
        boolean pendingBlockChange;
        boolean z;
        boolean z2;
        boolean z3 = true;
        if (rootView != null) {
            rootView.setBackgroundColor(viewModel.getPreferredColor());
        }
        loadingProgressBar.setVisibility(viewModel.isBusy() ? 0 : 8);
        ScrollView scrollView = contentScrollView;
        if (viewModel.isBusy()) {
            i = 8;
        } else {
            i = 0;
        }
        scrollView.setVisibility(i);
        if (gamerPicImageView != null) {
            gamerPicImageView.setImageURI2(ImageUtil.getMedium(viewModel.getGamerPicUrl()), R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
        }
        if (realNameTextView != null) {
            String realName = viewModel.getRealName();
            if (!JavaUtil.isNullOrEmpty(realName)) {
                realNameTextView.setText(realName);
                realNameTextView.setVisibility(0);
            } else {
                realNameTextView.setVisibility(8);
            }
        }
        if (!(gamerscoreTextView == null || gamerscoreIconTextView == null)) {
            String gamerScore = viewModel.getGamerScore();
            if (!JavaUtil.isNullOrEmpty(gamerScore)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(gamerscoreTextView, gamerScore, 0);
                XLEUtil.updateVisibilityIfNotNull(gamerscoreIconTextView, 0);
            }
        }
        if (gamertagTextView != null) {
            String gamerTag = viewModel.getGamerTag();
            if (!JavaUtil.isNullOrEmpty(gamerTag)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(gamertagTextView, gamerTag, 0);
            }
        }
        if (!viewModel.isMeProfile()) {
            if (viewModel.getIsAddingUserToBlockList() || viewModel.getIsRemovingUserFromBlockList()) {
                pendingBlockChange = true;
            } else {
                pendingBlockChange = false;
            }
            followButton.setChecked(viewModel.isCallerFollowingTarget());
            IconFontToggleButton iconFontToggleButton = followButton;
            if (pendingBlockChange || viewModel.getIsBlocked()) {
                z = false;
            } else {
                z = true;
            }
            iconFontToggleButton.setEnabled(z);
            muteButton.setChecked(viewModel.getIsMuted());
            IconFontToggleButton iconFontToggleButton2 = muteButton;
            if (viewModel.getIsAddingUserToMutedList() || viewModel.getIsRemovingUserFromMutedList()) {
                z2 = false;
            } else {
                z2 = true;
            }
            iconFontToggleButton2.setEnabled(z2);
            blockButton.setChecked(viewModel.getIsBlocked());
            IconFontToggleButton iconFontToggleButton3 = blockButton;
            if (pendingBlockChange) {
                z3 = false;
            }
            iconFontToggleButton3.setEnabled(z3);
        }
    }
}
