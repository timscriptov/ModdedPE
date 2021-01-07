package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.microsoft.xbox.toolkit.XLERValueHelper;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BlockingScreen extends Dialog {
    public BlockingScreen(Context context) {
        super(context, XLERValueHelper.getStyleRValue("blocking_dialog_style"));
        requestWindowFeature(1);
    }

    public void show(Context context, CharSequence charSequence) {
        setCancelable(false);
        setOnCancelListener((DialogInterface.OnCancelListener) null);
        setContentView(XLERValueHelper.getLayoutRValue("blocking_dialog"));
        setMessage(charSequence);
        show();
    }

    public void setMessage(CharSequence charSequence) {
        ((TextView) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_status_text"))).setText(charSequence);
    }
}
