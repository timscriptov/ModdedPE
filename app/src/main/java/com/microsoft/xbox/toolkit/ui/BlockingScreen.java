package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.widget.AppCompatTextView;

import com.microsoft.xbox.toolkit.XLERValueHelper;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BlockingScreen extends Dialog {
    public BlockingScreen(Context context) {
        super(context, XLERValueHelper.getStyleRValue("blocking_dialog_style"));
        requestWindowFeature(1);
    }

    public void show(Context context, CharSequence statusText) {
        setCancelable(false);
        setOnCancelListener((DialogInterface.OnCancelListener) null);
        setContentView(XLERValueHelper.getLayoutRValue("blocking_dialog"));
        setMessage(statusText);
        show();
    }

    public void setMessage(CharSequence statusText) {
        ((AppCompatTextView) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_status_text"))).setText(statusText);
    }
}
