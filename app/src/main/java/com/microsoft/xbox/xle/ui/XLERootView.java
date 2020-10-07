package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLERootView extends RelativeLayout {
    private static final int UNASSIGNED_ACTIVITY_BODY_ID = -1;
    private View activityBody;
    private int activityBodyIndex;
    private String headerName;
    private boolean isTopLevel = false;
    private long lastFps = 0;
    private long lastMs = 0;
    private int origPaddingBottom;
    private boolean showTitleBar = true;

    public XLERootView(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public XLERootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLERootView);
        if (a != null) {
            try {
                activityBodyIndex = a.getResourceId(R.styleable.XLERootView_activityBody, -1);
                isTopLevel = a.getBoolean(R.styleable.XLERootView_isTopLevel, false);
                showTitleBar = a.getBoolean(R.styleable.XLERootView_showTitleBar, true);
                int minScreenPercent = a.getInt(R.styleable.XLERootView_minScreenPercent, PKIFailureInfo.systemUnavail);
                if (minScreenPercent != Integer.MIN_VALUE) {
                    setMinimumWidth((Math.max(0, minScreenPercent) * SystemUtil.getScreenWidth()) / 100);
                }
                headerName = a.getString(R.styleable.XLERootView_headerName);
            } finally {
                a.recycle();
            }
        }*/
    }

    public boolean getIsTopLevel() {
        return isTopLevel;
    }

    public boolean getShowTitleBar() {
        return showTitleBar;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        initialize();
    }

    private void initialize() {
        if (activityBodyIndex != -1) {
            activityBody = findViewById(activityBodyIndex);
        } else {
            activityBody = this;
        }
        origPaddingBottom = getPaddingBottom();
        if (activityBody != null && activityBody != this) {
            ViewGroup.LayoutParams lpActivityBody = activityBody.getLayoutParams();
            RelativeLayout.LayoutParams activityParams = new RelativeLayout.LayoutParams(lpActivityBody);
            activityParams.width = -1;
            activityParams.height = -1;
            activityParams.addRule(10);
            if (lpActivityBody instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lpActivityBody;
                setPadding(getPaddingLeft() + mlp.leftMargin, getPaddingTop() + mlp.topMargin, getPaddingRight() + mlp.rightMargin, origPaddingBottom + mlp.bottomMargin);
                activityParams.setMargins(0, 0, 0, 0);
            }
            removeView(activityBody);
            addView(activityBody, activityParams);
        }
    }

    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void setBottomMargin(int marginBottom) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), origPaddingBottom + marginBottom);
    }
}
