package com.microsoft.xbox.toolkit.anim;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;

import org.jetbrains.annotations.NotNull;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEAnimationView extends XLEAnimation {
    public View animtarget;
    private Animation anim;

    public XLEAnimationView(@NotNull Animation animation) {
        this.anim = animation;
        animation.setFillAfter(true);
        this.anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                XLEAnimationView.this.onViewAnimationStart();
            }

            public void onAnimationEnd(Animation animation) {
                XLEAnimationView.this.onViewAnimationEnd();
                if (XLEAnimationView.this.endRunnable != null) {
                    XLEAnimationView.this.endRunnable.run();
                }
            }
        });
    }

    public void start() {
        this.animtarget.startAnimation(this.anim);
    }

    public void clear() {
        this.anim.setAnimationListener((Animation.AnimationListener) null);
        this.animtarget.clearAnimation();
    }

    public void setTargetView(View view) {
        XLEAssert.assertNotNull(view);
        this.animtarget = view;
        Animation animation = this.anim;
        if (animation instanceof AnimationSet) {
            for (Animation next : ((AnimationSet) animation).getAnimations()) {
                if (next instanceof HeightAnimation) {
                    ((HeightAnimation) next).setTargetView(view);
                }
            }
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        this.anim.setInterpolator(interpolator);
    }

    public void setFillAfter(boolean z) {
        this.anim.setFillAfter(z);
    }

    @SuppressLint("WrongConstant")
    public void onViewAnimationStart() {
        this.animtarget.setLayerType(2, (Paint) null);
    }

    public void onViewAnimationEnd() {
        ThreadManager.UIThreadPost((Runnable) () -> XLEAnimationView.this.animtarget.setLayerType(0, (Paint) null));
    }
}
