package com.microsoft.xbox.toolkit;

import android.app.Dialog;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface IXLEManagedDialog {

    Dialog getDialog();

    DialogType getDialogType();

    void setDialogType(DialogType dialogType);

    void quickDismiss();

    void safeDismiss();

    public enum DialogType {
        FATAL,
        NON_FATAL,
        NORMAL
    }
}
