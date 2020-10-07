package com.microsoft.xbox.xle.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import java.lang.ref.WeakReference;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class ActivityBase extends ScreenLayout {
    protected ViewModelBase viewModel;
    private boolean showRightPane;
    private boolean showUtilityBar;

    public ActivityBase() {
        this(0);
    }

    public ActivityBase(int orientation) {
        super(XboxTcuiSdk.getApplicationContext(), orientation);
        showUtilityBar = true;
        showRightPane = true;
    }

    public ActivityBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        showUtilityBar = true;
        showRightPane = true;
    }

    public abstract String getActivityName();

    public abstract void onCreateContentView();

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (viewModel != null) {
            viewModel.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onStop() {
        if (getIsStarted()) {
            super.onStop();
            if (viewModel != null) {
                viewModel.onSetInactive();
            }
            if (viewModel != null) {
                viewModel.onStop();
            }
        }
    }

    public void forceRefresh() {
        if (viewModel != null) {
            viewModel.forceRefresh();
        }
    }

    public void onStart() {
        if (!getIsStarted()) {
            super.onStart();
            if (viewModel != null) {
                viewModel.onStart();
            }
            if (viewModel != null) {
                viewModel.load();
            }
        }
        if (!delayAppbarAnimation()) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    public void onAnimateInStarted() {
        if (viewModel != null) {
            viewModel.forceUpdateViewImmediately();
        }
    }

    public void onAnimateInCompleted() {
        if (viewModel != null) {
            final WeakReference<ViewModelBase> viewModelWeakPtr = new WeakReference<>(viewModel);
            BackgroundThreadWaitor.getInstance().postRunnableAfterReady(() -> {
                ViewModelBase viewModelPtr = viewModelWeakPtr.get();
                if (viewModelPtr != null) {
                    viewModelPtr.forceUpdateViewImmediately();
                }
            });
        }
        if (viewModel != null) {
            viewModel.onAnimateInCompleted();
        }
    }

    public void forceUpdateViewImmediately() {
        if (viewModel != null) {
            viewModel.forceUpdateViewImmediately();
        }
    }

    public int computeBottomMargin() {
        return 0;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        MAASAnimation animation;
        XLEAnimation screenBodyAnimation;
        View root = getChildAt(0);
        if (root == null || (animation = MAAS.getInstance().getAnimation("Screen")) == null || (screenBodyAnimation = ((XLEMAASAnimationPackageNavigationManager) animation).compile(MAAS.MAASAnimationType.ANIMATE_OUT, goingBack, root)) == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        MAASAnimation animation;
        XLEAnimation screenBodyAnimation;
        View root = getChildAt(0);
        if (root == null || (animation = MAAS.getInstance().getAnimation("Screen")) == null || (screenBodyAnimation = ((XLEMAASAnimationPackageNavigationManager) animation).compile(MAAS.MAASAnimationType.ANIMATE_IN, goingBack, root)) == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewModel != null) {
            viewModel.onRestoreInstanceState(savedInstanceState);
        }
    }

    public boolean onBackButtonPressed() {
        if (viewModel != null) {
            return viewModel.onBackButtonPressed();
        }
        return false;
    }

    public void onSetActive() {
        super.onSetActive();
        if (viewModel != null) {
            viewModel.onSetActive();
        }
    }

    public boolean getShouldShowAppbar() {
        return false;
    }

    @SuppressLint("WrongConstant")
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != 8 || getXLERootView() == null || getXLERootView().getContentDescription() == null) {
            return super.dispatchPopulateAccessibilityEvent(event);
        }
        event.getText().clear();
        event.getText().add(getXLERootView().getContentDescription());
        return true;
    }

    public void onSetInactive() {
        super.onSetInactive();
        if (viewModel != null) {
            viewModel.onSetInactive();
        }
    }

    public void onPause() {
        super.onPause();
        if (viewModel != null) {
            viewModel.onPause();
        }
    }

    public void onApplicationPause() {
        super.onApplicationPause();
        if (viewModel != null) {
            viewModel.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        super.onApplicationResume();
        if (viewModel != null) {
            viewModel.onApplicationResume();
        }
    }

    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.onResume();
        }
    }

    public void onDestroy() {
        if (viewModel != null) {
            viewModel.onDestroy();
        }
        viewModel = null;
        super.onDestroy();
    }

    public void onTombstone() {
        if (viewModel != null) {
            viewModel.onTombstone();
        }
        super.onTombstone();
    }

    public void onRehydrate() {
        super.onRehydrate();
        if (viewModel != null) {
            viewModel.onRehydrate();
        }
    }

    public void onRehydrateOverride() {
        onCreateContentView();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (viewModel != null) {
            viewModel.onConfigurationChanged(newConfig);
        }
    }

    private XLERootView getXLERootView() {
        if (getChildAt(0) instanceof XLERootView) {
            return (XLERootView) getChildAt(0);
        }
        return null;
    }

    public void adjustBottomMargin(int bottomMargin) {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(bottomMargin);
        }
    }

    public void removeBottomMargin() {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(0);
        }
    }

    public void resetBottomMargin() {
        if (getXLERootView() != null) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    public boolean delayAppbarAnimation() {
        return false;
    }

    public void setHeaderName(String headerName) {
    }

    public String getName() {
        return getActivityName();
    }

    public String getRelativeId() {
        return null;
    }

    public void setScreenState(int state) {
        if (viewModel != null) {
            viewModel.setScreenState(state);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearDisappearingChildren();
    }
}
