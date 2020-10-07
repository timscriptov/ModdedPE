package com.microsoft.xbox.xle.viewmodel;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.appboy.models.InAppMessageBase;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.XLEUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class ViewModelBase implements XLEObserver<UpdateData> {
    public static final String TAG_PAGE_LOADING_TIME = "performance_measure_page_loadingtime";
    protected static int LAUNCH_TIME_OUT = InAppMessageBase.INAPP_MESSAGE_DURATION_DEFAULT_MILLIS;
    private final ScreenLayout screen;
    protected int LifetimeInMinutes;
    protected AdapterBase adapter;
    protected boolean isActive;
    protected boolean isForeground;
    protected boolean isLaunching;
    protected Runnable launchTimeoutHandler;
    protected int listIndex;
    protected int offset;
    private NavigationData nextScreenData;
    private boolean onlyProcessExceptionsAndShowToastsWhenActive;
    private ViewModelBase parent;
    private boolean shouldHideScreen;
    private boolean showNoNetworkPopup;
    private HashMap<UpdateType, XLEException> updateExceptions;
    private EnumSet<UpdateType> updateTypesToCheck;
    private boolean updating;

    public ViewModelBase(ScreenLayout screen2) {
        this(screen2, true, false);
    }

    public ViewModelBase() {
        this((ScreenLayout) null, true, false);
    }

    public ViewModelBase(boolean showNoNetworkPopup2, boolean onlyProcessExceptionsAndShowToastsWhenActive2) {
        this(null, showNoNetworkPopup2, onlyProcessExceptionsAndShowToastsWhenActive2);
    }

    public ViewModelBase(ScreenLayout screen2, boolean showNoNetworkPopup2, boolean onlyProcessExceptionsAndShowToastsWhenActive2) {
        LifetimeInMinutes = 60;
        updateExceptions = new HashMap<>();
        showNoNetworkPopup = true;
        onlyProcessExceptionsAndShowToastsWhenActive = false;
        nextScreenData = null;
        updating = false;
        isLaunching = false;
        screen = screen2;
        showNoNetworkPopup = showNoNetworkPopup2;
        onlyProcessExceptionsAndShowToastsWhenActive = onlyProcessExceptionsAndShowToastsWhenActive2;
    }

    public abstract boolean isBusy();

    public abstract void load(boolean z);

    public abstract void onRehydrate();

    public abstract void onStartOverride();

    public abstract void onStopOverride();

    public View findViewById(int id) {
        if (screen != null) {
            return screen.xleFindViewId(id);
        }
        return null;
    }

    public ScreenLayout getScreen() {
        return screen;
    }

    public ViewModelBase getParent() {
        return parent;
    }

    public void setParent(ViewModelBase parent2) {
        parent = parent2;
    }

    public AdapterBase getAdapter() {
        return adapter;
    }

    public void onChildViewModelChanged(ViewModelBase child) {
    }

    public void updateAdapter() {
        updateAdapter(true);
    }

    public void updateAdapter(boolean notifyParent) {
        if (adapter != null) {
            adapter.updateView();
        }
        if (parent != null && notifyParent) {
            parent.onChildViewModelChanged(this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onStart() {
        isForeground = true;
        onStartOverride();
        if (adapter != null) {
            adapter.onStart();
        }
    }

    public boolean getShouldHideScreen() {
        return shouldHideScreen;
    }

    public void setShouldHideScreen(boolean shouldHide) {
        shouldHideScreen = shouldHide;
    }

    public void setListPosition(int index, int offset2) {
        listIndex = index;
        offset = offset2;
    }

    public int getAndResetListPosition() {
        int value = listIndex;
        listIndex = 0;
        return value;
    }

    public int getAndResetListOffset() {
        int offset2 = offset;
        offset = 0;
        return offset2;
    }

    public void onStop() {
        isForeground = false;
        if (adapter != null) {
            adapter.onStop();
        }
        DialogManager.getInstance().dismissBlocking();
        if (shouldDismissTopNoFatalAlert()) {
            DialogManager.getInstance().dismissTopNonFatalAlert();
        }
        DialogManager.getInstance().dismissToast();
        onStopOverride();
    }

    public boolean shouldDismissTopNoFatalAlert() {
        return true;
    }

    public void onPause() {
        cancelLaunchTimeout();
        if (adapter != null) {
            adapter.onPause();
        }
    }

    public void onApplicationPause() {
        if (adapter != null) {
            adapter.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (adapter != null) {
            adapter.onApplicationResume();
        }
    }

    public void onResume() {
        if (adapter != null) {
            adapter.onResume();
            adapter.updateView();
        }
    }

    public void onDestroy() {
        if (adapter != null) {
            adapter.onDestroy();
        }
        adapter = null;
    }

    public void onTombstone() {
        if (adapter != null) {
            adapter.onDestroy();
        }
        adapter = null;
    }

    public void forceUpdateViewImmediately() {
        if (adapter != null) {
            adapter.forceUpdateViewImmediately();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public boolean onBackButtonPressed() {
        return false;
    }

    public boolean isBlockingBusy() {
        return false;
    }

    public String getBlockingStatusText() {
        return null;
    }

    public void load() {
        load(XLEGlobalData.getInstance().CheckDrainShouldRefresh(getClass()));
    }

    public void forceRefresh() {
        load(true);
        if (adapter != null) {
            adapter.updateView();
        }
    }

    public final void update(@NotNull AsyncResult<UpdateData> asyncResult) {
        updating = true;
        XLEAssert.assertTrue(nextScreenData == null);
        nextScreenData = null;
        if (asyncResult.getException() != null) {
            long errorCode = asyncResult.getException().getErrorCode();
            if (!asyncResult.getException().getIsHandled() && errorCode == XLEErrorCode.INVALID_ACCESS_TOKEN) {
                asyncResult.getException().setIsHandled(true);
            }
        }
        if (nextScreenData == null && (adapter != null || updateWithoutAdapter())) {
            updateOverride(asyncResult);
        }
        updating = false;
        if (nextScreenData != null) {
            try {
                switch (nextScreenData.getNavigationType()) {
                    case Push:
                        NavigationManager.getInstance().NavigateTo(nextScreenData.getScreenClass(), true);
                        break;
                    case PopReplace:
                        NavigationManager.getInstance().NavigateTo(nextScreenData.getScreenClass(), false);
                        break;
                    case PopAll:
                        NavigationManager.getInstance().GotoScreenWithPop(nextScreenData.getScreenClass());
                        break;
                }
            } catch (XLEException e) {
                e.printStackTrace();
            }
        } else if (shouldProcessErrors()) {
            if (asyncResult.getException() != null && !asyncResult.getException().getIsHandled() && updateTypesToCheck != null && updateTypesToCheck.contains(asyncResult.getResult().getUpdateType())) {
                updateExceptions.put(asyncResult.getResult().getUpdateType(), asyncResult.getException());
            }
            if (asyncResult.getResult().getIsFinal()) {
                if (updateTypesToCheck != null) {
                    updateTypesToCheck.remove(asyncResult.getResult().getUpdateType());
                }
                if (updateTypesToCheck == null || updateTypesToCheck.isEmpty()) {
                    onUpdateFinished();
                    updateTypesToCheck = null;
                }
            }
        }
        nextScreenData = null;
    }

    public boolean updateWithoutAdapter() {
        return false;
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
    }

    public void logOut(boolean clearEverything) {
    }

    public void setUpdateTypesToCheck(EnumSet<UpdateType> checkList) {
        updateTypesToCheck = checkList;
        updateExceptions.clear();
    }

    public boolean checkErrorCode(UpdateType updateType, long errorCode) {
        if (!updateExceptions.containsKey(updateType) || updateExceptions.get(updateType).getErrorCode() != errorCode) {
            return false;
        }
        if (updateExceptions.get(updateType).getIsHandled()) {
            return false;
        }
        return true;
    }

    public boolean updateTypesToCheckIsEmpty() {
        return updateTypesToCheck == null || updateTypesToCheck.isEmpty();
    }

    public boolean updateTypesToCheckHadAnyErrors() {
        return !updateExceptions.isEmpty();
    }

    public void onUpdateFinished() {
        updateTypesToCheck = null;
        updateExceptions.clear();
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = adapter.getAnimateOut(goingBack);
        if (animations == null || animations.size() <= 0) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        Iterator<XLEAnimation> it = animations.iterator();
        while (it.hasNext()) {
            animationPackage.add(it.next());
        }
        return animationPackage;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = adapter.getAnimateIn(goingBack);
        if (animations == null || animations.size() <= 0) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        Iterator<XLEAnimation> it = animations.iterator();
        while (it.hasNext()) {
            animationPackage.add(it.next());
        }
        return animationPackage;
    }

    public void TEST_induceGoBack() {
    }

    public void onAnimateInCompleted() {
        if (adapter != null) {
            adapter.onAnimateInCompleted();
        }
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass, ActivityParameters activityParameters) {
        NavigateTo(screenClass, true, activityParameters);
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass) {
        NavigateTo(screenClass, null);
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack, ActivityParameters activityParameters) {
        cancelLaunchTimeout();
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        if (updating) {
            nextScreenData = new NavigationData(screenClass, addToStack ? NavigationType.Push : NavigationType.PopReplace);
            return;
        }
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        NavigationManager.getInstance().NavigateTo(screenClass, addToStack, activityParameters);
    }

    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack) {
        NavigateTo(screenClass, addToStack, null);
    }

    public void showMustActDialog(String title, String promptText, String okText, Runnable okHandler, boolean isFatal) {
    }

    public void showOkCancelDialog(String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        showOkCancelDialog(null, promptText, okText, okHandler, cancelText, cancelHandler);
    }

    public void showOkCancelDialog(String title, String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        if (shouldProcessErrors()) {
            XLEUtil.showOkCancelDialog(title, promptText, okText, okHandler, cancelText, cancelHandler);
        }
    }

    public void showError(int contentResId) {
        DialogManager.getInstance().showToast(contentResId);
    }

    public void onSetActive() {
        isActive = true;
        if (adapter != null) {
            adapter.onSetActive();
        }
    }

    public void onSetInactive() {
        DialogManager.getInstance().dismissToast();
        isActive = false;
        if (adapter != null) {
            adapter.onSetInactive();
        }
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getShowNoNetworkPopup() {
        return showNoNetworkPopup;
    }

    private boolean shouldProcessErrors() {
        if (onlyProcessExceptionsAndShowToastsWhenActive) {
            return isActive;
        }
        return true;
    }

    public void setAsPivotPane() {
        showNoNetworkPopup = true;
        onlyProcessExceptionsAndShowToastsWhenActive = true;
    }

    public void cancelLaunchTimeout() {
        isLaunching = false;
        if (launchTimeoutHandler != null) {
            ThreadManager.Handler.removeCallbacks(launchTimeoutHandler);
        }
    }

    public void cancelLaunch() {
        isLaunching = false;
    }

    /* access modifiers changed from: protected */
    public void adapterUpdateView() {
        if (adapter != null) {
            adapter.updateView();
        }
    }

    public void setScreenState(int state) {
        if (adapter != null) {
            adapter.setScreenState(state);
        }
    }

    public void leaveViewModel(@NotNull Runnable leaveHandler) {
        leaveHandler.run();
    }

    public boolean shouldRefreshAsPivotHeader() {
        return false;
    }

    private enum NavigationType {
        Push,
        PopReplace,
        PopAll
    }

    private class NavigationData {
        private NavigationType navigationType;
        private Class<? extends ScreenLayout> screenClass;

        protected NavigationData(Class<? extends ScreenLayout> screen, NavigationType type) {
            screenClass = screen;
            navigationType = type;
        }

        public Class<? extends ScreenLayout> getScreenClass() {
            return screenClass;
        }

        public NavigationType getNavigationType() {
            return navigationType;
        }
    }
}
