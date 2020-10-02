package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class PopupView {
    private View mContentView;
    private Context mContext;
    private int mHeight;
    private int mOriginX;
    private int mOriginY;
    private View mParentView;
    private View mPopupView;
    private int mWidth;
    @SuppressLint("WrongConstant")
    private WindowManager mWindowManager = ((WindowManager) mContext.getSystemService("window"));

    public PopupView(Context context) {
        mContext = context;
    }

    public void setContentView(View contentView) {
        mContentView = contentView;
    }

    public void setParentView(View parentView) {
        mParentView = parentView;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setRect(int minX, int maxX, int minY, int maxY) {
        mWidth = maxX - minX;
        mHeight = maxY - minY;
        mOriginX = minX;
        mOriginY = minY;
    }

    public boolean getVisible() {
        return mPopupView != null && mPopupView.getParent() != null;
    }

    public void setVisible(boolean visible) {
        if (visible == getVisible()) {
            return;
        }
        if (visible) {
            addPopupView();
        } else {
            removePopupView();
        }
    }

    public void dismiss() {
        if (mPopupView != null) {
            removePopupView();
            if (mPopupView != mContentView && (mPopupView instanceof ViewGroup)) {
                ((ViewGroup) mPopupView).removeView(mContentView);
            }
            mPopupView = null;
        }
    }

    public void update() {
        if (getVisible()) {
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) mPopupView.getLayoutParams();
            int newFlags = computeFlags(p.flags);
            if (newFlags != p.flags) {
                p.flags = newFlags;
            }
            setLayoutRect(p);
            mWindowManager.updateViewLayout(mPopupView, p);
        }
    }

    private void addPopupView() {
        mPopupView = mContentView;
        WindowManager.LayoutParams p = createPopupLayout(mParentView.getWindowToken());
        setLayoutRect(p);
        invokePopup(p);
    }

    private void removePopupView() {
        try {
            mWindowManager.removeView(mPopupView);
        } catch (Exception e) {
        }
    }

    @NotNull
    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.format = -3;
        p.flags = computeFlags(p.flags);
        p.type = 1000;
        p.token = token;
        p.softInputMode = 1;
        p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
        p.windowAnimations = -1;
        return p;
    }

    private void preparePopup(WindowManager.LayoutParams p) {
    }

    private void invokePopup(@NotNull WindowManager.LayoutParams p) {
        p.packageName = mContext.getPackageName();
        mWindowManager.addView(mPopupView, p);
    }

    private int computeFlags(int curFlags) {
        return curFlags | 32;
    }

    private void setLayoutRect(@NotNull WindowManager.LayoutParams p) {
        p.width = mWidth;
        p.height = mHeight;
        p.x = mOriginX;
        p.y = mOriginY;
        p.gravity = 51;
    }
}