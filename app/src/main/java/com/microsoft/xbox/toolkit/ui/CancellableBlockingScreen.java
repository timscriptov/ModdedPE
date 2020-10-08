package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.appcompat.widget.AppCompatTextView;

import com.microsoft.xbox.toolkit.XLERValueHelper;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class CancellableBlockingScreen extends Dialog {
    private XLEButton cancelButton = ((XLEButton) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_cancel")));
    private View container = findViewById(XLERValueHelper.getIdRValue("blocking_dialog_container"));
    private AppCompatTextView statusText = ((AppCompatTextView) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_status_text")));

    public CancellableBlockingScreen(Context context) {
        super(context, XLERValueHelper.getStyleRValue("cancellable_dialog_style"));
        setCancelable(false);
        setOnCancelListener((DialogInterface.OnCancelListener) null);
        requestWindowFeature(1);
        setContentView(XLERValueHelper.getLayoutRValue("cancellable_blocking_dialog"));
    }

    public void show(Context context, CharSequence statusText2) {
        boolean previouslyVisible = isShowing();
        setMessage(statusText2);
        show();
        if (!previouslyVisible) {
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setFillAfter(true);
            animation.setStartOffset(1000);
            animation.setDuration(1000);
            container.startAnimation(animation);
        }
    }

    public void setMessage(CharSequence statusText2) {
        statusText.setText(statusText2);
    }

    public void setCancelButtonAction(View.OnClickListener listener) {
        if (listener != null) {
            cancelButton.setOnClickListener((View.OnClickListener) null);
        }
        cancelButton.setOnClickListener(listener);
    }
}
