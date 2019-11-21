package com.microsoft.xbox.idp.toolkit;

import android.content.Context;

import com.microsoft.xbox.idp.interop.XsapiUser;
import com.microsoft.xbox.idp.interop.XsapiUser.StartSignInCallback;
import com.microsoft.xbox.idp.toolkit.StartSignInLoader.Result;
import com.microsoft.xbox.idp.util.ResultCache;

public class StartSignInLoader extends WorkerLoader<Result> {

    public static class Data {
    }

    private static class MyWorker implements Worker<Result> {
        public final ResultCache<Result> cache;
        public final Object resultKey;

        private MyWorker(ResultCache<Result> cache2, Object resultKey2) {
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
            XsapiUser.getInstance().startSignIn(new StartSignInCallback() {
                public void onSuccess() {
                    Result result = new Result(new Data(), null);
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    listener.onResult(result);
                }

                public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                    Result result = new Result(null, new HttpError(errorCode, httpStatusCode, errorMessage));
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

    public static class Result extends LoaderResult<Data> {
        protected Result(Data data, HttpError error) {
            super(data, error);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public StartSignInLoader(Context context, ResultCache<Result> cache, Object resultKey) {
        super(context, new MyWorker(cache, resultKey));
    }

    public boolean isDataReleased(Result data) {
        return data.isReleased();
    }

    public void releaseData(Result data) {
        data.release();
    }
}
