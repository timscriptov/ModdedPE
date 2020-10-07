package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEAnimationPackage {
    public Runnable onAnimationEndRunnable;
    private LinkedList<XLEAnimationEntry> animations = new LinkedList<>();
    private boolean running = false;

    public void tryFinishAll() {
        if (getRemainingAnimations() == 0) {
            XLEAssert.assertTrue(running);
            running = false;
            onAnimationEndRunnable.run();
        }
    }

    private int getRemainingAnimations() {
        int rv = 0;
        Iterator it = animations.iterator();
        while (it.hasNext()) {
            if (!((XLEAnimationEntry) it.next()).done) {
                rv++;
            }
        }
        return rv;
    }

    public void setOnAnimationEndRunnable(Runnable runnable) {
        onAnimationEndRunnable = runnable;
    }

    public void startAnimation() {
        XLEAssert.assertTrue(!running);
        running = true;
        Iterator it = animations.iterator();
        while (it.hasNext()) {
            ((XLEAnimationEntry) it.next()).startAnimation();
        }
    }

    public void clearAnimation() {
        Iterator it = animations.iterator();
        while (it.hasNext()) {
            ((XLEAnimationEntry) it.next()).clearAnimation();
        }
    }

    public void add(XLEAnimation animation) {
        animations.add(new XLEAnimationEntry(animation));
    }

    public XLEAnimationPackage add(XLEAnimationPackage animationPackage) {
        if (animationPackage != null) {
            Iterator it = animationPackage.animations.iterator();
            while (it.hasNext()) {
                add(((XLEAnimationEntry) it.next()).animation);
            }
        }
        return this;
    }

    private class XLEAnimationEntry {
        public XLEAnimation animation;
        public boolean done = false;
        public int iterationID = 0;

        public XLEAnimationEntry(@NotNull XLEAnimation animation2) {
            animation = animation2;
            animation2.setOnAnimationEnd(() -> onAnimationEnded());
        }

        public void onAnimationEnded() {
            boolean z;
            boolean z2 = true;
            if (Thread.currentThread() == ThreadManager.UIThread) {
                z = true;
            } else {
                z = false;
            }
            XLEAssert.assertTrue(z);
            if (onAnimationEndRunnable == null) {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
            final int finishIterationID = iterationID;
            ThreadManager.UIThreadPost(() -> {
                if (finishIterationID == iterationID) {
                    finish();
                }
            });
        }

        public void finish() {
            done = true;
            tryFinishAll();
        }

        public void startAnimation() {
            animation.start();
        }

        public void clearAnimation() {
            iterationID++;
            animation.clear();
        }
    }
}
