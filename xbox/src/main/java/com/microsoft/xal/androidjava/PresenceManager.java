package com.microsoft.xal.androidjava;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.microsoft.xal.logging.XalLogger;

public class PresenceManager implements LifecycleObserver {

    private static boolean isAttached;
    private final XalLogger m_logger = new XalLogger("PresenceManager");
    private boolean m_paused = false;

    private static native void pausePresence();

    private static native void resumePresence();

    static void attach() {
        if (isAttached) {
            return;
        }
        isAttached = true;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new PresenceManager());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    synchronized void onForeground() {
        if (m_paused) {
            try {
                m_logger.Important("Resuming presence on paused app resume");
                m_logger.Flush();
                resumePresence();
                m_paused = false;
            } catch (UnsatisfiedLinkError e) {
                m_logger.Error("Failed to resume presence: " + e.toString());
                m_logger.Flush();
            }
        } else {
            m_logger.Important("Ignoring resume, not currently paused");
            m_logger.Flush();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    synchronized void onBackground() {
        if (m_paused) {
            m_logger.Important("Ignoring pause, already paused");
            m_logger.Flush();
        } else {
            try {
                m_logger.Important("Pausing presence on app pause");
                m_logger.Flush();
                pausePresence();
                m_paused = true;
            } catch (UnsatisfiedLinkError e) {
                m_logger.Error("Failed to pause presence: " + e.toString());
                m_logger.Flush();
            }
        }
    }
}
