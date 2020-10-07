package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Ready {
    private boolean ready = false;
    private Object syncObj = new Object();

    public boolean getIsReady() {
        boolean z;
        synchronized (syncObj) {
            z = ready;
        }
        return z;
    }

    public void setReady() {
        synchronized (syncObj) {
            ready = true;
            syncObj.notifyAll();
        }
    }

    public void waitForReady() {
        waitForReady(0);
    }

    public void waitForReady(int timeoutMs) {
        synchronized (syncObj) {
            if (!ready) {
                if (timeoutMs > 0) {
                    try {
                        syncObj.wait((long) timeoutMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    try {
                        syncObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void reset() {
        synchronized (syncObj) {
            ready = false;
        }
    }
}
