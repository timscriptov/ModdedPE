package com.microsoft.xbox.idp.toolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Loader;
import android.os.Build.VERSION;
import android.os.Handler;

public abstract class WorkerLoader<D> extends Loader<D> {
    public final Handler dispatcher = new Handler();
    public final Object lock = new Object();
    private D result;
    public ResultListener<D> resultListener;
    private final Worker<D> worker;

    public interface ResultListener<D> {
        void onResult(D d);
    }

    private class ResultListenerImpl implements ResultListener<D> {
        private ResultListenerImpl() {
        }

        public void onResult(final D result) {
            synchronized (WorkerLoader.this.lock) {
                final boolean canceled = this != WorkerLoader.this.resultListener;
                WorkerLoader.this.resultListener = null;
                WorkerLoader.this.dispatcher.post(new Runnable() {
                    public void run() {
                        if (canceled) {
                            WorkerLoader.this.onCanceled(result);
                        } else {
                            WorkerLoader.this.deliverResult(result);
                        }
                    }
                });
            }
        }
    }

    public interface Worker<D> {
        void cancel();

        void start(ResultListener<D> resultListener);
    }

    public abstract boolean isDataReleased(D d);

    public abstract void releaseData(D d);

    public WorkerLoader(Context context, Worker<D> worker2) {
        super(context);
        this.worker = worker2;
    }

    public void onStartLoading() {
        if (this.result != null) {
            deliverResult(this.result);
        }
        if (takeContentChanged() || this.result == null) {
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
        synchronized (this.lock) {
            this.resultListener = new ResultListenerImpl();
            this.worker.start(this.resultListener);
        }
    }

    public boolean onCancelLoad() {
        boolean z;
        synchronized (this.lock) {
            if (this.resultListener != null) {
                this.worker.cancel();
                this.resultListener = null;
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public void deliverResult(D data) {
        if (!isReset()) {
            D oldResult = this.result;
            this.result = data;
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
        if (this.result != null && !isDataReleased(this.result)) {
            releaseData(this.result);
        }
        this.result = null;
    }

    @SuppressLint({"NewApi"})
    private boolean cancelLoadCompat() {
        return VERSION.SDK_INT < 16 ? onCancelLoad() : cancelLoad();
    }
}
