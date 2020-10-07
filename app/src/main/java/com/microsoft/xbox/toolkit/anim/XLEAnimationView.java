package com.microsoft.xbox.toolkit.anim;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEAnimationView extends XLEAnimation {
    public View animtarget;
    private Animation anim;

    public XLEAnimationView(Animation anim2) {
        anim = anim2;
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                onViewAnimationStart();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                onViewAnimationEnd();
                if (endRunnable != null) {
                    endRunnable.run();
                }
            }
        });
    }

    public void start() {
        animtarget.startAnimation(anim);
    }

    public void clear() {
        anim.setAnimationListener(null);
        animtarget.clearAnimation();
    }

    public void setTargetView(View targetView) {
        XLEAssert.assertNotNull(targetView);
        animtarget = targetView;
        if (anim instanceof AnimationSet) {
            for (Animation animation : ((AnimationSet) anim).getAnimations()) {
                if (animation instanceof HeightAnimation) {
                    ((HeightAnimation) animation).setTargetView(targetView);
                }
            }
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        anim.setInterpolator(interpolator);
    }

    public void setFillAfter(boolean fillAfter) {
        anim.setFillAfter(fillAfter);
    }

    @SuppressLint("WrongConstant")
    public void onViewAnimationStart() {
        animtarget.setLayerType(2, null);
    }

    @SuppressLint("WrongConstant")
    public void onViewAnimationEnd() {
        ThreadManager.UIThreadPost((Runnable) () -> animtarget.setLayerType(0, null));
    }
}
