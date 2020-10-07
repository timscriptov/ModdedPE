package com.microsoft.xbox.xle.app.activity;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.telemetry.helpers.UTCReportUser;
import com.microsoft.xbox.xle.viewmodel.ReportUserScreenViewModel;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ReportUserScreen extends ActivityBase {
    private ReportUserScreenViewModel reportUserScreenViewModel;

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        viewModel = new ReportUserScreenViewModel(this);
        reportUserScreenViewModel = (ReportUserScreenViewModel) viewModel;
        UTCReportUser.trackReportView(getName(), reportUserScreenViewModel.getXUID());
    }

    public void onStart() {
        super.onStart();
        setBackgroundColor(reportUserScreenViewModel.getPreferredColor());
    }

    public String getActivityName() {
        return "Report user";
    }

    public void onCreateContentView() {
        setContentView(R.layout.report_user_screen);
    }
}
