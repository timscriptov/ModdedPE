package com.microsoft.xbox.toolkit;

import android.app.Dialog;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class DialogManager implements IProjectSpecificDialogManager {
    private static DialogManager instance = new DialogManager();
    private IProjectSpecificDialogManager manager;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        return instance;
    }

    public IProjectSpecificDialogManager getManager() {
        return manager;
    }

    public void setManager(IProjectSpecificDialogManager manager2) {
        manager = manager2;
    }

    public Dialog getVisibleDialog() {
        checkProvider();
        if (manager != null) {
            return manager.getVisibleDialog();
        }
        return null;
    }

    public boolean getIsBlocking() {
        checkProvider();
        if (manager != null) {
            return manager.getIsBlocking();
        }
        return false;
    }

    public void setEnabled(boolean value) {
        checkProvider();
        if (manager != null) {
            manager.setEnabled(value);
        }
    }

    public void showManagedDialog(IXLEManagedDialog dialog) {
        checkProvider();
        if (manager != null) {
            manager.showManagedDialog(dialog);
        }
    }

    public void dismissManagedDialog(IXLEManagedDialog dialog) {
        checkProvider();
        if (manager != null) {
            manager.dismissManagedDialog(dialog);
        }
    }

    public void onDialogStopped(IXLEManagedDialog dialog) {
        checkProvider();
        if (manager != null) {
            manager.onDialogStopped(dialog);
        }
    }

    public void showFatalAlertDialog(String title, String promptText, String okText, Runnable okHandler) {
        checkProvider();
        if (manager != null) {
            manager.showFatalAlertDialog(title, promptText, okText, okHandler);
        }
    }

    public void showNonFatalAlertDialog(String title, String promptText, String okText, Runnable okHandler) {
        checkProvider();
        if (manager != null) {
            manager.showNonFatalAlertDialog(title, promptText, okText, okHandler);
        }
    }

    public void showOkCancelDialog(String title, String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        checkProvider();
        if (manager != null) {
            manager.showOkCancelDialog(title, promptText, okText, okHandler, cancelText, cancelHandler);
        }
    }

    public void showToast(int contentResId) {
        checkProvider();
        if (manager != null) {
            manager.showToast(contentResId);
        }
    }

    public void setBlocking(boolean visible, String statusText) {
        checkProvider();
        if (manager != null) {
            manager.setBlocking(visible, statusText);
        }
    }

    public void setCancelableBlocking(boolean visible, String statusText, Runnable cancelRunnable) {
        checkProvider();
        if (manager != null) {
            manager.setCancelableBlocking(visible, statusText, cancelRunnable);
        }
    }

    public void forceDismissAll() {
        checkProvider();
        if (manager != null) {
            manager.forceDismissAll();
        }
    }

    public void dismissToast() {
        checkProvider();
        if (manager != null) {
            manager.dismissToast();
        }
    }

    public void forceDismissAlerts() {
        checkProvider();
        if (manager != null) {
            manager.forceDismissAlerts();
        }
    }

    public void dismissTopNonFatalAlert() {
        checkProvider();
        if (manager != null) {
            manager.dismissTopNonFatalAlert();
        }
    }

    public void dismissBlocking() {
        checkProvider();
        if (manager != null) {
            manager.dismissBlocking();
        }
    }

    private void checkProvider() {
    }

    public void addManagedDialog(IXLEManagedDialog dialog) {
        checkProvider();
        if (manager != null) {
            manager.addManagedDialog(dialog);
        }
    }

    public void onApplicationPause() {
        if (manager != null) {
            manager.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (manager != null) {
            manager.onApplicationResume();
        }
    }
}
