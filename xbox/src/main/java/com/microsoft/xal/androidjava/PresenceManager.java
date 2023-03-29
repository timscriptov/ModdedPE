package com.microsoft.xal.androidjava;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class PresenceManager implements LifecycleObserver {
    private static boolean isAttached = false;
    private boolean m_paused = false;

    private static native void pausePresence();

    private static native void resumePresence();

    static void attach() {
        if (!isAttached) {
            isAttached = true;
            ProcessLifecycleOwner.get().getLifecycle().addObserver(new PresenceManager());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public synchronized void onForeground() {
        if (m_paused) {
            try {
                resumePresence();
                m_paused = false;
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public synchronized void onBackground() {
        if (!m_paused) {
            try {
                pausePresence();
                m_paused = true;
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }
        }
    }
}