package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.XLERValueHelper;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SwitchPanel extends LinearLayout {
    private static final int LAYOUT_BLOCK_TIMEOUT_MS = 150;
    private final int INVALID_STATE_ID = -1;
    private final int VALID_CONTENT_STATE = 0;
    private boolean active = false;
    private boolean blocking = false;
    private View newView = null;
    private AnimatorListenerAdapter AnimateInListener = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animation) {
            onAnimateInEnd();
        }

        public void onAnimationEnd(Animator animation) {
            onAnimateInEnd();
        }

        public void onAnimationStart(Animator animation) {
            onAnimateInStart();
        }
    };
    private View oldView = null;
    private AnimatorListenerAdapter AnimateOutListener = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animation) {
            onAnimateOutEnd();
        }

        public void onAnimationEnd(Animator animation) {
            onAnimateOutEnd();
        }

        public void onAnimationStart(Animator animation) {
            onAnimateOutStart();
        }
    };
    private int selectedState;
    private boolean shouldAnimate = true;

    public SwitchPanel(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public SwitchPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("SwitchPanel"));
        selectedState = a.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanel_selectedState"), -1);
        a.recycle();
        if (selectedState < 0) {
            throw new IllegalArgumentException("You must specify the selectedState attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        updateVisibility(-1, selectedState);
    }

    public void setActive(boolean active2) {
        active = active2;
    }

    public void setShouldAnimate(boolean value) {
        shouldAnimate = value;
    }

    public int getState() {
        return selectedState;
    }

    public void setState(int newState) {
        if (newState < 0) {
            throw new IllegalArgumentException("New state must be a positive value.");
        } else if (selectedState != newState) {
            int oldState = selectedState;
            selectedState = newState;
            updateVisibility(oldState, newState);
        }
    }

    @SuppressLint("WrongConstant")
    private void updateVisibility(int oldState, int newState) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (!(v instanceof SwitchPanelChild)) {
                throw new UnsupportedOperationException("All children of SwitchPanel must implement the SwitchPanelChild interface. All other types are not supported and should be removed.");
            }
            int switchPanelState = ((SwitchPanelChild) v).getState();
            if (switchPanelState == oldState) {
                oldView = v;
            } else if (switchPanelState == newState) {
                newView = v;
            } else {
                v.setVisibility(8);
            }
        }
        if (!shouldAnimate || newState != 0 || newView == null) {
            if (oldView != null) {
                oldView.setVisibility(8);
            }
            if (newView != null) {
                newView.setAlpha(1.0f);
                newView.setVisibility(0);
            }
            requestLayout();
            return;
        }
        newView.setAlpha(0.0f);
        newView.setVisibility(0);
        requestLayout();
        if (oldView != null) {
            oldView.animate().alpha(0.0f).setDuration(150).setListener(AnimateOutListener);
        }
        newView.animate().alpha(1.0f).setDuration(150).setListener(AnimateInListener);
    }

    public void setBlocking(boolean value) {
        if (blocking != value) {
            blocking = value;
            if (blocking) {
                BackgroundThreadWaitor.getInstance().setBlocking(BackgroundThreadWaitor.WaitType.ListLayout, 150);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(BackgroundThreadWaitor.WaitType.ListLayout);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void onAnimateInStart() {
        if (newView != null) {
            newView.setLayerType(2, (Paint) null);
            setBlocking(true);
        }
    }

    @SuppressLint("WrongConstant")
    public void onAnimateInEnd() {
        setBlocking(false);
        if (newView != null) {
            newView.setLayerType(0, (Paint) null);
        }
    }

    @SuppressLint("WrongConstant")
    public void onAnimateOutStart() {
        if (oldView != null) {
            oldView.setLayerType(2, (Paint) null);
            setBlocking(true);
        }
    }

    @SuppressLint("WrongConstant")
    public void onAnimateOutEnd() {
        if (oldView != null) {
            oldView.setVisibility(8);
            oldView.setLayerType(0, (Paint) null);
        }
    }

    public interface SwitchPanelChild {
        int getState();
    }
}