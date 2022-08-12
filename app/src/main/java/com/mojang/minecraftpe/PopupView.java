package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class PopupView {
    private View mContentView;
    private final Context mContext;
    private int mHeight;
    private int mOriginX;
    private int mOriginY;
    private View mParentView;
    private View mPopupView;
    private int mWidth;
    private final WindowManager mWindowManager;

    private int computeFlags(int curFlags) {
        return curFlags | 32;
    }

    private void preparePopup(WindowManager.LayoutParams p) {
    }

    public PopupView(@NonNull Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    public void setParentView(View parentView) {
        this.mParentView = parentView;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public void setRect(int minX, int maxX, int minY, int maxY) {
        this.mWidth = maxX - minX;
        this.mHeight = maxY - minY;
        this.mOriginX = minX;
        this.mOriginY = minY;
    }

    public void setVisible(boolean visible) {
        if (visible != getVisible()) {
            if (visible) {
                addPopupView();
            } else {
                removePopupView();
            }
        }
    }

    public boolean getVisible() {
        View view = this.mPopupView;
        return view != null && view.getParent() != null;
    }

    public void dismiss() {
        if (this.mPopupView != null) {
            removePopupView();
            View view = this.mPopupView;
            View view2 = this.mContentView;
            if (view != view2 && (view instanceof ViewGroup)) {
                ((ViewGroup) view).removeView(view2);
            }
            this.mPopupView = null;
        }
    }

    public void update() {
        if (getVisible()) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.mPopupView.getLayoutParams();
            int computeFlags = computeFlags(layoutParams.flags);
            if (computeFlags != layoutParams.flags) {
                layoutParams.flags = computeFlags;
            }
            setLayoutRect(layoutParams);
            this.mWindowManager.updateViewLayout(this.mPopupView, layoutParams);
        }
    }

    private void addPopupView() {
        this.mPopupView = this.mContentView;
        WindowManager.LayoutParams createPopupLayout = createPopupLayout(this.mParentView.getWindowToken());
        setLayoutRect(createPopupLayout);
        invokePopup(createPopupLayout);
    }

    private void removePopupView() {
        try {
            this.mParentView.requestFocus();
            this.mWindowManager.removeView(this.mPopupView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = -3;
        layoutParams.flags = computeFlags(layoutParams.flags);
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        layoutParams.token = token;
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
        layoutParams.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
        layoutParams.windowAnimations = -1;
        return layoutParams;
    }

    private void invokePopup(@NonNull WindowManager.LayoutParams p) {
        p.packageName = this.mContext.getPackageName();
        this.mWindowManager.addView(this.mPopupView, p);
        this.mParentView.requestFocus();
    }

    @SuppressLint("WrongConstant")
    private void setLayoutRect(@NonNull WindowManager.LayoutParams p) {
        p.width = this.mWidth;
        p.height = this.mHeight;
        p.x = this.mOriginX;
        p.y = this.mOriginY;
        p.gravity = 51;
    }
}