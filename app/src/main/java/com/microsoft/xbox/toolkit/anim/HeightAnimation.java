package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HeightAnimation extends Animation {
    private int fromValue;
    private int toValue;
    private View view;

    public HeightAnimation(int from, int to) {
        fromValue = from;
        toValue = to;
    }

    public void setTargetView(@NotNull View targetView) {
        view = targetView;
        fromValue = targetView.getHeight();
    }

    public void applyTransformation(float interpolatedTime, Transformation t) {
        int newDelta = (int) (((float) (toValue - fromValue)) * interpolatedTime);
        view.getLayoutParams().height = fromValue + newDelta;
        view.requestLayout();
    }

    public boolean willChangeBounds() {
        return true;
    }
}
