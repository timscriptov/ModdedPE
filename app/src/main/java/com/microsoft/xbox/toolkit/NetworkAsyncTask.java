package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class NetworkAsyncTask<T> extends XLEAsyncTask<T> {
    protected boolean forceLoad = true;
    private boolean shouldExecute = true;

    public NetworkAsyncTask() {
        super(XLEThreadPool.networkOperationsThreadPool);
    }

    public NetworkAsyncTask(XLEThreadPool threadPool) {
        super(XLEThreadPool.networkOperationsThreadPool);
    }

    public abstract boolean checkShouldExecute();

    public abstract T loadDataInBackground();

    public abstract T onError();

    public abstract void onNoAction();

    public void load(boolean forceLoad2) {
        forceLoad = forceLoad2;
        execute();
    }

    public final T doInBackground() {
        try {
            return loadDataInBackground();
        } catch (Exception e) {
            return onError();
        }
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (cancelled) {
        }
        shouldExecute = checkShouldExecute();
        if (shouldExecute || forceLoad) {
            isBusy = true;
            onPreExecute();
            super.executeBackground();
            return;
        }
        onNoAction();
        isBusy = false;
    }
}
