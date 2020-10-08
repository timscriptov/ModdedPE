package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class ScreenLayout extends FrameLayout {
    private static ArrayList<View> badList = new ArrayList<>();
    protected boolean isTombstoned;
    private boolean allEventsEnabled;
    private boolean drawerEnabled;
    private boolean isActive;
    private boolean isEditable;
    private boolean isReady;
    private boolean isStarted;
    private Runnable onLayoutChangedListener;
    private int orientation;
    private int screenPercent;

    public ScreenLayout() {
        this(XboxTcuiSdk.getApplicationContext());
    }

    public ScreenLayout(Context context) {
        this(context, 0);
    }

    public ScreenLayout(Context context, int orientation2) {
        super(context);
        onLayoutChangedListener = null;
        isEditable = false;
        screenPercent = 100;
        drawerEnabled = true;
        allEventsEnabled = true;
        Initialize(orientation2);
    }

    public ScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onLayoutChangedListener = null;
        isEditable = false;
        screenPercent = 100;
        drawerEnabled = true;
        allEventsEnabled = true;
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("ScreenLayout"));
        if (a.hasValue(XLERValueHelper.getStyleableRValue("ScreenLayout_screenDIPs"))) {
            screenPercent = (int) ((((float) a.getDimensionPixelSize(XLERValueHelper.getStyleableRValue("ScreenLayout_screenDIPs"), SystemUtil.getScreenWidth())) / ((float) SystemUtil.getScreenWidth())) * 100.0f);
        } else {
            screenPercent = a.getInt(XLERValueHelper.getStyleableRValue("ScreenLayout_screenPercent"), -2);
        }
        a.recycle();
        Initialize(7);
    }

    public static void addViewThatCausesAndroidLeaks(View v) {
        badList.add(v);
    }

    public static void removeViewAndWorkaroundAndroidLeaks(View v) {
        boolean z;
        boolean z2 = true;
        if (v != null) {
            ViewParent viewparent = v.getParent();
            if (viewparent instanceof ViewGroup) {
                ((ViewGroup) viewparent).removeAllViews();
                if (v.getParent() == null) {
                    z = true;
                } else {
                    z = false;
                }
                XLEAssert.assertTrue(z);
            }
            if (v instanceof ViewGroup) {
                ViewGroup view = (ViewGroup) v;
                view.removeAllViews();
                view.destroyDrawingCache();
                if (view.getChildCount() != 0) {
                    z2 = false;
                }
                XLEAssert.assertTrue(z2);
            }
        }
    }

    public abstract void forceRefresh();

    public abstract void forceUpdateViewImmediately();

    public abstract String getName();

    public abstract void onAnimateInCompleted();

    public abstract void onAnimateInStarted();

    public abstract boolean onBackButtonPressed();

    public abstract void onRehydrateOverride();

    public void Initialize(int orientation2) {
        isReady = false;
        isActive = false;
        isStarted = false;
        orientation = orientation2;
    }

    public void setContentView(int screenLayoutId) {
        LayoutInflater.from(getContext()).inflate(screenLayoutId, this, true);
    }

    public void onRestart() {
    }

    public void onCreate() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public Boolean getTrackPage() {
        return true;
    }

    public void onStart() {
        isStarted = true;
    }

    public void onResume() {
        isReady = true;
    }

    public void onApplicationResume() {
    }

    public void onApplicationPause() {
    }

    public void onPause() {
        isReady = false;
    }

    public void onStop() {
        isStarted = false;
    }

    public void setScreenState(int state) {
    }

    public void onDestroy() {
        removeAllViewsAndWorkaroundAndroidLeaks();
    }

    public void onTombstone() {
        isTombstoned = true;
        removeAllViewsAndWorkaroundAndroidLeaks();
    }

    public void onRehydrate() {
        isTombstoned = false;
        onRehydrateOverride();
    }

    public String getLocalClassName() {
        return getClass().getName();
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public boolean getIsTombstoned() {
        return isTombstoned;
    }

    public boolean getIsReady() {
        return isReady;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        return null;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        return null;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getIsStarted() {
        return isStarted;
    }

    public void onSetActive() {
        isActive = true;
    }

    public void onSetInactive() {
        isActive = false;
    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (onLayoutChangedListener != null) {
            onLayoutChangedListener.run();
        }
    }

    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (allEventsEnabled) {
            return super.onInterceptHoverEvent(event);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (allEventsEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (allEventsEnabled) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    public boolean onHoverEvent(MotionEvent event) {
        if (allEventsEnabled) {
            return super.onHoverEvent(event);
        }
        return true;
    }

    public void setOnLayoutChangedListener(Runnable r) {
        onLayoutChangedListener = r;
    }

    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }

    public void adjustBottomMargin(int bottomMargin) {
    }

    public void resetBottomMargin() {
    }

    public void removeBottomMargin() {
    }

    public boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable2) {
        isEditable = isEditable2;
    }

    public boolean getCanAutoLaunch() {
        return !isEditable;
    }

    public boolean getShouldShowAppbar() {
        return !isEditable;
    }

    private void removeAllViewsAndWorkaroundAndroidLeaks() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        removeAllViews();
        Iterator<View> it = badList.iterator();
        while (it.hasNext()) {
            removeViewAndWorkaroundAndroidLeaks(it.next());
        }
        badList.clear();
    }

    public int getScreenPercent() {
        return screenPercent;
    }

    public ScreenLayout setScreenPercent(int val) {
        this.screenPercent = val;
        return this;
    }

    public String getContent() {
        return null;
    }

    public String getRelativeId() {
        return null;
    }

    public boolean isDrawerEnabled() {
        return drawerEnabled;
    }

    public void setDrawerEnabled(boolean drawerEnabled2) {
        drawerEnabled = drawerEnabled2;
    }

    public void leaveScreen(Runnable leaveHandler) {
        leaveHandler.run();
    }

    public boolean isAnimateOnPush() {
        return true;
    }

    public boolean isAnimateOnPop() {
        return true;
    }

    public boolean isKeepPreviousScreen() {
        return false;
    }

    public boolean isAllEventsEnabled() {
        return allEventsEnabled;
    }

    public void setAllEventsEnabled(boolean enabled) {
        allEventsEnabled = enabled;
    }

    public View xleFindViewId(int id) {
        return findViewById(id);
    }
}
