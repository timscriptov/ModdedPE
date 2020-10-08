package com.microsoft.xbox.toolkit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class FastProgressBar extends ProgressBar {
    private boolean isEnabled;
    private int visibility;

    @SuppressLint("WrongConstant")
    public FastProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEnabled(true);
        setVisibility(0);
    }

    public synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidateDelayed(33);
    }

    @SuppressLint("WrongConstant")
    public void setEnabled(boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;
            if (!isEnabled) {
                visibility = getVisibility();
                super.setVisibility(8);
                return;
            }
            super.setVisibility(visibility);
        }
    }

    public void setVisibility(int v) {
        if (isEnabled) {
            super.setVisibility(v);
        } else {
            visibility = v;
        }
    }
}
