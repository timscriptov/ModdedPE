package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ModelData;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.XLEUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class ModelBase<T> extends XLEObservable<UpdateData> implements ModelData<T> {
    protected static final long MilliSecondsInADay = 86400000;
    protected static final long MilliSecondsInAnHour = 3600000;
    protected static final long MilliSecondsInHalfHour = 1800000;
    protected boolean isLoading = false;
    protected long lastInvalidatedTick = 0;
    protected Date lastRefreshTime;
    protected long lifetime = MilliSecondsInADay;
    protected IDataLoaderRunnable<T> loaderRunnable;
    private SingleEntryLoadingStatus loadingStatus = new SingleEntryLoadingStatus();

    public boolean shouldRefresh() {
        return shouldRefresh(lastRefreshTime);
    }

    public boolean hasValidData() {
        return lastRefreshTime != null;
    }

    public boolean shouldRefresh(Date lastRefreshTime2) {
        return XLEUtil.shouldRefresh(lastRefreshTime2, lifetime);
    }

    public boolean isLoaded() {
        return lastRefreshTime != null;
    }

    public void updateWithNewData(@NotNull AsyncResult<T> result) {
        isLoading = false;
        if (result.getException() == null && result.getStatus() == AsyncActionStatus.SUCCESS) {
            lastRefreshTime = new Date();
        }
    }

    public boolean getIsLoading() {
        return loadingStatus.getIsLoading();
    }

    public void invalidateData() {
        lastRefreshTime = null;
    }

    public AsyncResult<T> loadData(boolean forceRefresh, IDataLoaderRunnable<T> runnable) {
        XLEAssert.assertIsNotUIThread();
        return DataLoadUtil.Load(forceRefresh, lifetime, lastRefreshTime, loadingStatus, runnable);
    }

    public void loadInternal(boolean forceRefresh, UpdateType updateType, IDataLoaderRunnable<T> runnable) {
        loadInternal(forceRefresh, updateType, runnable, lastRefreshTime);
    }

    public void loadInternal(boolean forceRefresh, UpdateType updateType, IDataLoaderRunnable<T> runnable, Date lastRefreshTime2) {
        boolean z = false;
        XLEAssert.assertIsUIThread();
        if (getIsLoading() || (!forceRefresh && !shouldRefresh(lastRefreshTime2))) {
            if (!getIsLoading()) {
                z = true;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
            return;
        }
        DataLoadUtil.StartLoadFromUI(forceRefresh, lifetime, lastRefreshTime, loadingStatus, runnable);
        notifyObservers(new AsyncResult(new UpdateData(updateType, false), this, null));
    }
}