package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;

import com.microsoft.xbox.toolkit.XLEAssert;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEAnimationAbsListView extends XLEAnimation {
    private LayoutAnimationController layoutAnimationController = null;
    private AbsListView layoutView = null;

    public XLEAnimationAbsListView(LayoutAnimationController controller) {
        layoutAnimationController = controller;
        XLEAssert.assertTrue(layoutAnimationController != null);
    }

    public void start() {
        layoutView.setLayoutAnimation(layoutAnimationController);
        if (endRunnable != null) {
            endRunnable.run();
        }
    }

    public void clear() {
        layoutView.setLayoutAnimationListener(null);
        layoutView.clearAnimation();
    }

    public void setInterpolator(Interpolator interpolator) {
        layoutAnimationController.setInterpolator(interpolator);
    }

    public void setTargetView(View targetView) {
        XLEAssert.assertNotNull(targetView);
        XLEAssert.assertTrue(targetView instanceof AbsListView);
        layoutView = (AbsListView) targetView;
    }
}
