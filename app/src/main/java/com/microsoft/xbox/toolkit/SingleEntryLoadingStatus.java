package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SingleEntryLoadingStatus {
    private boolean isLoading = false;
    private XLEException lastError = null;
    private Object syncObj = new Object();

    public boolean getIsLoading() {
        return isLoading;
    }

    public XLEException getLastError() {
        return lastError;
    }

    public void setSuccess() {
        setDone((XLEException) null);
    }

    public void setFailed(XLEException ex) {
        setDone(ex);
    }

    private void setDone(XLEException ex) {
        synchronized (syncObj) {
            isLoading = false;
            lastError = ex;
            syncObj.notifyAll();
        }
    }

    public WaitResult waitForNotLoading() {
        WaitResult waitResult;
        synchronized (syncObj) {
            if (isLoading) {
                try {
                    syncObj.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                waitResult = new WaitResult(true, lastError);
            } else {
                isLoading = true;
                waitResult = new WaitResult(false, (XLEException) null);
            }
        }
        return waitResult;
    }

    public void reset() {
        synchronized (syncObj) {
            isLoading = false;
            lastError = null;
            syncObj.notifyAll();
        }
    }

    public class WaitResult {
        public XLEException error;
        public boolean waited;

        public WaitResult(boolean waited2, XLEException error2) {
            waited = waited2;
            error = error2;
        }
    }
}
