package com.microsoft.xbox.idp.toolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Loader;
import android.os.Handler;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class WorkerLoader<D> extends Loader<D> {
    public final Handler dispatcher = new Handler();
    public final Object lock = new Object();
    private final Worker<D> worker;
    public ResultListener<D> resultListener;
    private D result;

    public WorkerLoader(Context context, Worker<D> worker2) {
        super(context);
        worker = worker2;
    }

    public abstract boolean isDataReleased(D d);

    public abstract void releaseData(D d);

    public void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        }
        if (takeContentChanged() || result == null) {
            forceLoad();
        }
    }

    public void onStopLoading() {
        cancelLoadCompat();
    }

    public void onCanceled(D data) {
        if (data != null && !isDataReleased(data)) {
            releaseData(data);
        }
    }

    public void onForceLoad() {
        super.onForceLoad();
        cancelLoadCompat();
        synchronized (lock) {
            resultListener = new ResultListenerImpl();
            worker.start(resultListener);
        }
    }

    public boolean onCancelLoad() {
        boolean z;
        synchronized (lock) {
            if (resultListener != null) {
                worker.cancel();
                resultListener = null;
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public void deliverResult(D data) {
        if (!isReset()) {
            D oldResult = result;
            result = data;
            if (isStarted()) {
                super.deliverResult(data);
            }
            if (oldResult != null && oldResult != data && !isDataReleased(oldResult)) {
                releaseData(oldResult);
            }
        } else if (data != null) {
            releaseData(data);
        }
    }

    public void onReset() {
        cancelLoadCompat();
        if (result != null && !isDataReleased(result)) {
            releaseData(result);
        }
        result = null;
    }

    @SuppressLint({"NewApi"})
    private boolean cancelLoadCompat() {
        return cancelLoad();
    }

    public interface ResultListener<D> {
        void onResult(D d);
    }

    public interface Worker<D> {
        void cancel();

        void start(ResultListener<D> resultListener);
    }

    private class ResultListenerImpl implements ResultListener<D> {
        private ResultListenerImpl() {
        }

        public void onResult(final D result) {
            synchronized (lock) {
                final boolean canceled = this != resultListener;
                dispatcher.post(() -> {
                    if (canceled) {
                        onCanceled(result);
                    } else {
                        deliverResult(result);
                    }
                });
            }
        }
    }
}
