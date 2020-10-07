package com.microsoft.xbox.xle.viewmodel;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.sls.FeedbackType;
import com.microsoft.xbox.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.ReportUserScreenAdapter;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ReportUserScreenViewModel extends ViewModelBase {
    public boolean isSubmittingReport;
    private FeedbackType[] feedbackReasons;
    private ProfileModel model;
    private FeedbackType selectedReason;
    private SubmitReportAsyncTask submitReportAsyncTask;

    public ReportUserScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        boolean z;
        String profileXuid = NavigationManager.getInstance().getActivityParameters().getSelectedProfile();
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(profileXuid));
        if (JavaUtil.isNullOrEmpty(profileXuid)) {
            popScreenWithXuidError();
        }
        model = ProfileModel.getProfileModel(profileXuid);
        if (!JavaUtil.isNullOrEmpty(model.getGamerTag())) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        adapter = new ReportUserScreenAdapter(this);
        FeedbackType[] feedbackTypeArr = new FeedbackType[7];
        feedbackTypeArr[0] = FeedbackType.UserContentPersonalInfo;
        feedbackTypeArr[1] = FeedbackType.FairPlayCheater;
        feedbackTypeArr[2] = JavaUtil.isNullOrEmpty(model.getRealName()) ? FeedbackType.UserContentGamertag : FeedbackType.UserContentRealName;
        feedbackTypeArr[3] = FeedbackType.UserContentGamerpic;
        feedbackTypeArr[4] = FeedbackType.FairPlayQuitter;
        feedbackTypeArr[5] = FeedbackType.FairplayUnsporting;
        feedbackTypeArr[6] = FeedbackType.CommsAbusiveVoice;
        feedbackReasons = feedbackTypeArr;
    }

    public boolean onBackButtonPressed() {
        UTCPageView.removePage();
        try {
            NavigationManager.getInstance().PopScreensAndReplace(1, null, false, false, false, NavigationManager.getInstance().getActivityParameters());
            return true;
        } catch (XLEException e) {
            return false;
        }
    }

    public void onStartOverride() {
    }

    public void onRehydrate() {
    }

    public void onStopOverride() {
        if (submitReportAsyncTask != null) {
            submitReportAsyncTask.cancel();
        }
    }

    public boolean isBusy() {
        return isSubmittingReport;
    }

    public void load(boolean forceRefresh) {
    }

    private void popScreenWithXuidError() {
        try {
            showError(R.string.Service_ErrorText);
            NavigationManager.getInstance().PopScreen();
        } catch (XLEException e) {
        }
    }

    public int getPreferredColor() {
        return model.getPreferedColor();
    }

    public String getTitle() {
        return String.format(XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_InfoString_Android), new Object[]{model.getGamerTag()});
    }

    public ArrayList<String> getReasonTitles() {
        ArrayList<String> titles = new ArrayList<>(feedbackReasons.length);
        titles.add(XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_SelectReason));
        for (FeedbackType feedbackType : feedbackReasons) {
            titles.add(feedbackType.getTitle());
        }
        return titles;
    }

    public boolean validReasonSelected() {
        return selectedReason != null;
    }

    public FeedbackType getReason() {
        return selectedReason;
    }

    public void setReason(int position) {
        selectedReason = position != 0 && position + -1 < feedbackReasons.length ? feedbackReasons[position - 1] : null;
        updateAdapter();
    }

    public void submitReport(String textReason) {
        if (submitReportAsyncTask != null) {
            submitReportAsyncTask.cancel();
        }
        if (selectedReason != null) {
            submitReportAsyncTask = new SubmitReportAsyncTask(model, selectedReason, textReason);
            submitReportAsyncTask.load(true);
        }
    }

    public void onSubmitReportCompleted(@NotNull AsyncActionStatus status) {
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                DialogManager.getInstance().showToast(R.string.ProfileCard_Report_SuccessSubtext);
                onBackButtonPressed();
                return;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.ProfileCard_Report_Error);
                return;
            default:
                return;
        }
    }

    public String getXUID() {
        return model.getXuid();
    }

    private class SubmitReportAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private FeedbackType feedbackType;
        private ProfileModel model;
        private String textReason;

        private SubmitReportAsyncTask(ProfileModel model2, FeedbackType feedbackType2, String textReason2) {
            model = model2;
            feedbackType = feedbackType2;
            textReason = textReason2;
        }

        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            onSubmitReportCompleted(AsyncActionStatus.NO_CHANGE);
        }

        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = isSubmittingReport = true;
            updateAdapter();
        }

        public void onPostExecute(AsyncActionStatus result) {
            onSubmitReportCompleted(result);
        }

        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(model);
            return model.submitFeedbackForUser(forceLoad, feedbackType, textReason).getStatus();
        }
    }
}
