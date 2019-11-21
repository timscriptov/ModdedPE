package com.microsoft.onlineid.internal.ui;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;

public final class Dimensions {
    private static final float MinimumTouchableTargetDp = 48.0f;

    public static int convertDipToPixels(float dip, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(1, dip, metrics));
    }

    public static void ensureMinimumTouchTarget(final View view, final View parentView, final DisplayMetrics metrics) {
        parentView.post(new Runnable() {
            public void run() {
                Rect bounds = new Rect();
                view.getHitRect(bounds);
                int width = bounds.width();
                int height = bounds.height();
                int minimumPixels = Dimensions.convertDipToPixels(Dimensions.MinimumTouchableTargetDp, metrics);
                if (width < minimumPixels) {
                    int extraPadding = ((minimumPixels - width) + 1) / 2;
                    bounds.left -= extraPadding;
                    bounds.right += extraPadding;
                }
                if (height < minimumPixels) {
                    extraPadding = ((minimumPixels - height) + 1) / 2;
                    bounds.top -= extraPadding;
                    bounds.bottom += extraPadding;
                }
                parentView.setTouchDelegate(new TouchDelegate(bounds, view));
            }
        });
    }
}
