package com.microsoft.xbox.toolkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEManagedAlertDialog extends AlertDialog implements IXLEManagedDialog {
    private IXLEManagedDialog.DialogType dialogType = IXLEManagedDialog.DialogType.NORMAL;

    protected XLEManagedAlertDialog(Context context) {
        super(context);
    }

    public IXLEManagedDialog.DialogType getDialogType() {
        return dialogType;
    }

    public void setDialogType(IXLEManagedDialog.DialogType type) {
        dialogType = type;
    }

    public Dialog getDialog() {
        return this;
    }

    public void safeDismiss() {
        DialogManager.getInstance().dismissManagedDialog(this);
    }

    public void quickDismiss() {
        super.dismiss();
    }

    public void onStop() {
        super.onStop();
        DialogManager.getInstance().onDialogStopped(this);
    }
}
