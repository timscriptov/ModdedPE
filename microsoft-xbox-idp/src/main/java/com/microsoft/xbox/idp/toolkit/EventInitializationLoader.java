package com.microsoft.xbox.idp.toolkit;

import android.content.Context;

import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.Interop.EventInitializationCallback;
import com.microsoft.xbox.idp.toolkit.EventInitializationLoader.Result;
import com.microsoft.xbox.idp.util.ResultCache;

public class EventInitializationLoader extends WorkerLoader<Result> {

    private static class MyWorker implements Worker<Result> {
        public final ResultCache<Result> cache;
        public final Object resultKey;
        private final String rpsTicket;
        private final long userPtr;

        private MyWorker(long userPtr2, String rpsTicket2, ResultCache<Result> cache2, Object resultKey2) {
            this.userPtr = userPtr2;
            this.rpsTicket = rpsTicket2;
            this.cache = cache2;
            this.resultKey = resultKey2;
        }

        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void start(final ResultListener<Result> listener) {
            Result r;
            if (hasCache()) {
                synchronized (this.cache) {
                    r = (Result) this.cache.get(this.resultKey);
                }
                if (r != null) {
                    listener.onResult(r);
                    return;
                }
            }
            Interop.InvokeEventInitialization(this.userPtr, this.rpsTicket, new EventInitializationCallback() {
                public void onSuccess() {
                    Result result = new Result(null);
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    listener.onResult(result);
                }

                public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                    Result result = new Result(new HttpError(errorCode, httpStatusCode, errorMessage));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    listener.onResult(result);
                }
            });
        }

        public void cancel() {
        }
    }

    public static class Result extends LoaderResult<Void> {
        protected Result(HttpError error) {
            super(null, error);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public EventInitializationLoader(Context context, long userPtr, String rpsTicket, ResultCache<Result> cache, Object resultKey) {
        super(context, new MyWorker(userPtr, rpsTicket, cache, resultKey));
    }

    public boolean isDataReleased(Result data) {
        return true;
    }

    public void releaseData(Result data) {
    }
}
