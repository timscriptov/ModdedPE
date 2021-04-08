package com.microsoft.xbox.toolkit;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SingleEntryLoadingStatus {
    private boolean isLoading = false;
    private XLEException lastError = null;
    private final Object syncObj = new Object();

    public boolean getIsLoading() {
        return this.isLoading;
    }

    public XLEException getLastError() {
        return this.lastError;
    }

    public void setSuccess() {
        setDone(null);
    }

    public void setFailed(XLEException xLEException) {
        setDone(xLEException);
    }

    private void setDone(XLEException xLEException) {
        synchronized (this.syncObj) {
            this.isLoading = false;
            this.lastError = xLEException;
            this.syncObj.notifyAll();
        }
    }

    public WaitResult waitForNotLoading() {
        synchronized (this.syncObj) {
            if (this.isLoading) {
                try {
                    this.syncObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().interrupt();
                WaitResult waitResult = new WaitResult(true, this.lastError);
                return waitResult;
            }
            this.isLoading = true;
            WaitResult waitResult2 = new WaitResult(false, null);
            return waitResult2;
        }
    }

    public void reset() {
        synchronized (this.syncObj) {
            this.isLoading = false;
            this.lastError = null;
            this.syncObj.notifyAll();
        }
    }

    public class WaitResult {
        public XLEException error;
        public boolean waited;

        public WaitResult(boolean z, XLEException xLEException) {
            this.waited = z;
            this.error = xLEException;
        }
    }
}
