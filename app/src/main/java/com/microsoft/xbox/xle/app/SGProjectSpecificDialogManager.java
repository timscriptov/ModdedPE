package com.microsoft.xbox.xle.app;

import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.DialogManagerBase;
import com.microsoft.xbox.toolkit.IProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.dialog.ChangeFriendshipDialog;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SGProjectSpecificDialogManager extends DialogManagerBase {
    private static IProjectSpecificDialogManager instance = new SGProjectSpecificDialogManager();
    private ChangeFriendshipDialog changeFriendshipDialog;

    private SGProjectSpecificDialogManager() {
    }

    public static IProjectSpecificDialogManager getInstance() {
        return instance;
    }

    public static SGProjectSpecificDialogManager getProjectSpecificInstance() {
        return (SGProjectSpecificDialogManager) DialogManager.getInstance().getManager();
    }

    public void forceDismissAll() {
        super.forceDismissAll();
        dismissChangeFriendshipDialog();
    }

    public boolean shouldDismissAllBeforeOpeningADialog() {
        return false;
    }

    public void onApplicationPause() {
        forceDismissAll();
    }

    public void onApplicationResume() {
    }

    public void showChangeFriendshipDialog(ChangeFriendshipDialogViewModel vm, ViewModelBase previousVM) {
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.setVm(vm);
            changeFriendshipDialog.getDialog().show();
            return;
        }
        changeFriendshipDialog = new ChangeFriendshipDialog(XboxTcuiSdk.getActivity(), vm, previousVM);
        addManagedDialog(changeFriendshipDialog);
    }

    public void notifyChangeFriendshipDialogUpdateView() {
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.updateView();
        }
    }

    public void notifyChangeFriendshipDialogAsyncTaskCompleted() {
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.reportAsyncTaskCompleted();
        }
    }

    public void notifyChangeFriendshipDialogAsyncTaskFailed(String errorMessage) {
        if (changeFriendshipDialog != null) {
            changeFriendshipDialog.reportAsyncTaskFailed(errorMessage);
        }
    }

    public void dismissChangeFriendshipDialog() {
        if (changeFriendshipDialog != null) {
            dismissManagedDialog(changeFriendshipDialog);
            changeFriendshipDialog = null;
        }
    }
}
