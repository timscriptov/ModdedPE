package com.microsoft.xbox.toolkit;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BackgroundThreadWaitor {
    private static BackgroundThreadWaitor instance = new BackgroundThreadWaitor();
    private BackgroundThreadWaitorChangedCallback blockingChangedCallback = null;
    private Hashtable<WaitType, WaitObject> blockingTable = new Hashtable<>();
    private Ready waitReady = new Ready();
    private ArrayList<Runnable> waitingRunnables = new ArrayList<>();

    public static BackgroundThreadWaitor getInstance() {
        if (instance == null) {
            instance = new BackgroundThreadWaitor();
        }
        return instance;
    }

    public void waitForReady(int timeoutMs) {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        ThreadManager.UIThreadPost(() -> updateWaitReady());
        waitReady.waitForReady(timeoutMs);
    }

    public void setBlocking(WaitType type, int expireMs) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        blockingTable.put(type, new WaitObject(type, expireMs));
        updateWaitReady();
    }

    public void clearBlocking(WaitType type) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        blockingTable.remove(type);
        updateWaitReady();
    }

    public void setChangedCallback(BackgroundThreadWaitorChangedCallback callback) {
        blockingChangedCallback = callback;
    }

    public boolean isBlocking() {
        return !waitReady.getIsReady();
    }

    public void updateWaitReady() {
        boolean blocking;
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        HashSet<WaitType> waitTypesToRemove = new HashSet<>();
        EnumSet<WaitType> blockingTypes = EnumSet.noneOf(WaitType.class);
        Enumeration<WaitObject> e = blockingTable.elements();
        while (e.hasMoreElements()) {
            WaitObject waitObject = e.nextElement();
            if (waitObject.isExpired()) {
                waitTypesToRemove.add(waitObject.type);
            } else {
                blockingTypes.add(waitObject.type);
            }
        }
        Iterator<WaitType> it = waitTypesToRemove.iterator();
        while (it.hasNext()) {
            blockingTable.remove(it.next());
        }
        if (blockingTable.size() == 0) {
            waitReady.setReady();
            drainWaitingRunnables();
            blocking = false;
        } else {
            waitReady.reset();
            blocking = true;
        }
        if (blockingChangedCallback != null) {
            blockingChangedCallback.run(blockingTypes, blocking);
        }
    }

    public void postRunnableAfterReady(Runnable r) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        if (r != null) {
            if (!isBlocking()) {
                r.run();
            } else {
                waitingRunnables.add(r);
            }
        }
    }

    private void drainWaitingRunnables() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        Iterator<Runnable> it = waitingRunnables.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        waitingRunnables.clear();
    }

    public enum WaitType {
        Navigation,
        ApplicationBar,
        ListScroll,
        ListLayout,
        PivotScroll
    }

    public interface BackgroundThreadWaitorChangedCallback {
        void run(EnumSet<WaitType> enumSet, boolean z);
    }

    private class WaitObject {
        public WaitType type;
        private long expires;

        public WaitObject(WaitType type2, long expireMs) {
            type = type2;
            expires = SystemClock.uptimeMillis() + expireMs;
        }

        public boolean isExpired() {
            return expires < SystemClock.uptimeMillis();
        }
    }
}
