package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Interpolator;

import com.microsoft.xbox.toolkit.ThreadManager;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class XLEAnimation {
    protected Runnable endRunnable;

    public abstract void clear();

    public abstract void setInterpolator(Interpolator interpolator);

    public abstract void setTargetView(View view);

    public abstract void start();

    public void setOnAnimationEnd(final Runnable runnable) {
        if (runnable != null) {
            endRunnable = () -> ThreadManager.UIThreadPost(runnable);
        } else {
            endRunnable = null;
        }
    }
}
