package com.microsoft.xbox.toolkit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;

import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEManagedDialog extends Dialog implements IXLEManagedDialog {
    protected static final String BODY_ANIMATION_NAME = "Dialog";
    final Runnable callAfterAnimationIn = () -> OnAnimationInEnd();
    protected String bodyAnimationName = BODY_ANIMATION_NAME;
    protected View dialogBody = null;
    protected Runnable onAnimateOutCompletedRunable = null;
    final Runnable callAfterAnimationOut = () -> OnAnimationOutEnd();
    private IXLEManagedDialog.DialogType dialogType = IXLEManagedDialog.DialogType.NORMAL;

    protected XLEManagedDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public XLEManagedDialog(Context context, int theme) {
        super(context, theme);
    }

    public XLEManagedDialog(Context context) {
        super(context);
    }

    protected static boolean isKindle() {
        return SystemUtil.isKindle();
    }

    public String getBodyAnimationName() {
        return bodyAnimationName;
    }

    public void setBodyAnimationName(String string) {
        bodyAnimationName = string;
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

    public void makeFullScreen() {
        getWindow().setLayout(-1, -2);
    }

    public void safeDismiss() {
        DialogManager.getInstance().dismissManagedDialog(this);
    }

    public void quickDismiss() {
        super.dismiss();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            safeDismiss();
        }
    }

    public XLEAnimationPackage getAnimateOut() {
        XLEAnimation screenBodyAnimation = getBodyAnimation(MAAS.MAASAnimationType.ANIMATE_OUT, true);
        if (screenBodyAnimation == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public XLEAnimationPackage getAnimateIn() {
        XLEAnimation screenBodyAnimation = getBodyAnimation(MAAS.MAASAnimationType.ANIMATE_IN, false);
        if (screenBodyAnimation == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public void OnAnimationInEnd() {
        NavigationManager.getInstance().setAnimationBlocking(false);
    }

    public void OnAnimationOutEnd() {
        NavigationManager.getInstance().setAnimationBlocking(false);
        super.dismiss();
        if (onAnimateOutCompletedRunable != null) {
            try {
                onAnimateOutCompletedRunable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setAnimateOutRunnable(Runnable postAnimateOutRunnable) {
        onAnimateOutCompletedRunable = postAnimateOutRunnable;
    }

    public View getDialogBody() {
        return dialogBody;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XLEAnimationPackage anim = getAnimateIn();
        if (getDialogBody() != null && anim != null) {
            NavigationManager.getInstance().setAnimationBlocking(true);
            anim.setOnAnimationEndRunnable(callAfterAnimationIn);
            anim.startAnimation();
        }
    }

    public void dismiss() {
        if (!isShowing()) {
            super.dismiss();
            return;
        }
        XLEAnimationPackage anim = getAnimateOut();
        if (getDialogBody() == null || anim == null) {
            if (onAnimateOutCompletedRunable != null) {
                onAnimateOutCompletedRunable.run();
            }
            super.dismiss();
            return;
        }
        NavigationManager.getInstance().setAnimationBlocking(true);
        anim.setOnAnimationEndRunnable(callAfterAnimationOut);
        anim.startAnimation();
    }

    public XLEAnimation getBodyAnimation(MAAS.MAASAnimationType animationType, boolean goingBack) {
        if (getDialogBody() != null) {
            return ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(bodyAnimationName)).compile(animationType, goingBack, getDialogBody());
        }
        return null;
    }

    public void forceKindleRespectDimOptions() {
        new Handler().postDelayed(() -> getWindow().addFlags(2), 100);
    }
}
