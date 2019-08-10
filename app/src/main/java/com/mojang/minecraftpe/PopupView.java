package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

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
    private WindowManager mWindowManager = ((WindowManager) this.mContext.getSystemService("window"));

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

    private boolean getVisible() {
        return (mPopupView == null || mPopupView.getParent() == null) ? false : true;
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
            LayoutParams p = (LayoutParams) mPopupView.getLayoutParams();
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
        LayoutParams p = createPopupLayout(mParentView.getWindowToken());
        setLayoutRect(p);
        invokePopup(p);
    }

    private void removePopupView() {
        try {
            mWindowManager.removeView(mPopupView);
        } catch (Exception ignored) {
        }
    }

    private LayoutParams createPopupLayout(IBinder token) {
        LayoutParams p = new LayoutParams();
        p.format = -3;
        p.flags = computeFlags(p.flags);
        p.type = 0x3e8;
        p.token = token;
        p.softInputMode = 1;
        p.setTitle("PopupWindow:" + Integer.toHexString(hashCode()));
        p.windowAnimations = -1;
        return p;
    }

    private void preparePopup(LayoutParams p) {
    }

    private void invokePopup(LayoutParams p) {
        p.packageName = mContext.getPackageName();
        mWindowManager.addView(mPopupView, p);
    }

    private int computeFlags(int curFlags) {
        return curFlags | 32;
    }

    private void setLayoutRect(LayoutParams p) {
        p.width = mWidth;
        p.height = mHeight;
        p.x = mOriginX;
        p.y = mOriginY;
        p.gravity = 51;
    }
}
