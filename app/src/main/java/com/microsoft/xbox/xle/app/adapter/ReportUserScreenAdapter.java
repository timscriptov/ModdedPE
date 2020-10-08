package com.microsoft.xbox.xle.app.adapter;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatEditText;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.telemetry.helpers.UTCReportUser;
import com.microsoft.xbox.telemetry.helpers.UTCTelemetry;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.ReportUserScreenViewModel;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ReportUserScreenAdapter extends AdapterBase {
    public AppCompatEditText optionalText = ((AppCompatEditText) findViewById(R.id.report_user_text));
    public ReportUserScreenViewModel viewModel;
    private XLEButton cancelButton = ((XLEButton) findViewById(R.id.report_user_cancel));
    private Spinner reasonSpinner = ((Spinner) findViewById(R.id.report_user_reason));
    private ArrayAdapter<String> reasonSpinnerAdapter;
    private XLEButton submitButton = ((XLEButton) findViewById(R.id.report_user_submit));
    private CustomTypefaceTextView titleTextView = ((CustomTypefaceTextView) findViewById(R.id.report_user_title));

    public ReportUserScreenAdapter(ReportUserScreenViewModel viewModel2) {
        super(viewModel2);
        viewModel = viewModel2;
        XLEAssert.assertNotNull(titleTextView);
        XLEAssert.assertNotNull(reasonSpinner);
        XLEAssert.assertNotNull(optionalText);
        XLEAssert.assertNotNull(cancelButton);
        XLEAssert.assertNotNull(submitButton);
    }

    public void onStart() {
        super.onStart();
        reasonSpinnerAdapter = new ArrayAdapter<>(XboxTcuiSdk.getActivity(), R.layout.report_spinner_item, viewModel.getReasonTitles());
        reasonSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        reasonSpinner.setAdapter(reasonSpinnerAdapter);
        if (Build.VERSION.SDK_INT >= 16) {
            reasonSpinner.setPopupBackgroundDrawable(new ColorDrawable(viewModel.getPreferredColor()));
        }
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                viewModel.setReason(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        cancelButton.setOnClickListener(v -> viewModel.onBackButtonPressed());
        submitButton.setOnClickListener(v -> {
            UTCReportUser.trackReportDialogOK(viewModel.getReason() == null ? UTCTelemetry.UNKNOWNPAGE : viewModel.getReason().toString());
            viewModel.submitReport(optionalText.getText().toString());
        });
    }

    public void updateViewOverride() {
        if (titleTextView != null) {
            titleTextView.setText(viewModel.getTitle());
        }
        if (submitButton != null) {
            submitButton.setEnabled(viewModel.validReasonSelected());
        }
    }
}
