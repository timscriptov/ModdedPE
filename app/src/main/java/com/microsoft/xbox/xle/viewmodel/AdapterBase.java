package com.microsoft.xbox.xle.viewmodel;

import android.view.View;

import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.XLEAllocationTracker;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.module.ScreenModuleLayout;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class AdapterBase {
    public static String ALLOCATION_TAG = "ADAPTERBASE";
    private static HashMap<String, Integer> adapterCounter = new HashMap<>();
    private final ViewModelBase viewModel;
    protected boolean isActive;
    private boolean isStarted;
    private ArrayList<ScreenModuleLayout> screenModules;

    public AdapterBase() {
        this(null);
    }

    public AdapterBase(ViewModelBase viewModel2) {
        isActive = false;
        isStarted = false;
        screenModules = new ArrayList<>();
        viewModel = viewModel2;
        XLEAllocationTracker.getInstance().debugIncrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public abstract void updateViewOverride();

    public boolean getIsStarted() {
        return isStarted;
    }

    public void finalize() {
        XLEAllocationTracker.getInstance().debugDecrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public void updateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            updateViewOverride();
            Iterator<ScreenModuleLayout> it = screenModules.iterator();
            while (it.hasNext()) {
                it.next().updateView();
            }
        }
    }

    public void invalidateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            invalidateViewOverride();
            Iterator<ScreenModuleLayout> it = screenModules.iterator();
            while (it.hasNext()) {
                it.next().invalidateView();
            }
        }
    }

    public void invalidateViewOverride() {
    }

    public void forceUpdateViewImmediately() {
        XLEAssert.assertIsUIThread();
        updateViewOverride();
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().updateView();
        }
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        return null;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        return null;
    }

    public void onPause() {
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onPause();
        }
    }

    public void onApplicationPause() {
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onApplicationPause();
        }
    }

    public void onApplicationResume() {
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onApplicationResume();
        }
    }

    public void onResume() {
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onResume();
        }
    }

    public void onDestroy() {
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onDestroy();
        }
        screenModules.clear();
    }

    public void onStart() {
        isStarted = true;
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onStart();
        }
    }

    public void onStop() {
        isStarted = false;
        Iterator<ScreenModuleLayout> it = screenModules.iterator();
        while (it.hasNext()) {
            it.next().onStop();
        }
    }

    @Deprecated
    public void onAppBarUpdated() {
    }

    public void onAppBarButtonsAdded() {
    }

    public void showKeyboard(View view, int delayMS) {
        XLEUtil.showKeyboard(view, delayMS);
    }

    public void onSetActive() {
        isActive = true;
        if (XboxTcuiSdk.getActivity() != null && isStarted) {
            updateView();
        }
    }

    public void onSetInactive() {
        isActive = false;
    }

    public void onAnimateInCompleted() {
    }

    public View findViewById(int id) {
        View view = null;
        if (viewModel != null) {
            view = viewModel.findViewById(id);
        }
        return view != null ? view : XboxTcuiSdk.getActivity().findViewById(id);
    }

    public void findAndInitializeModuleById(int id, ViewModelBase vm) {
        View view = findViewById(id);
        if (view != null && (view instanceof ScreenModuleLayout)) {
            ScreenModuleLayout module = (ScreenModuleLayout) findViewById(id);
            module.setViewModel(vm);
            screenModules.add(module);
        }
    }

    public void setBlocking(boolean visible, String blockingText) {
        DialogManager.getInstance().setBlocking(visible, blockingText);
    }

    public void setCancelableBlocking(boolean visible, String blockingText, Runnable cancelRunnable) {
        DialogManager.getInstance().setCancelableBlocking(visible, blockingText, cancelRunnable);
    }

    public void setScreenState(int state) {
    }
}
