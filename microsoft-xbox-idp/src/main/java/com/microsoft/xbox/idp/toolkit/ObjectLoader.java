package com.microsoft.xbox.idp.toolkit;

import android.content.Context;

import com.google.gson.Gson;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Result;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpCall.Callback;
import com.microsoft.xbox.idp.util.HttpHeaders;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public class ObjectLoader<T> extends WorkerLoader<Result<T>> {
    private static final String TAG = ObjectLoader.class.getSimpleName();

    public interface Cache {
        void clear();

        <T> Result<T> get(Object obj);

        <T> Result<T> put(Object obj, Result<T> result);

        <T> Result<T> remove(Object obj);
    }

    private static class MyWorker<T> implements Worker<Result<T>> {
        public final Cache cache;
        public final Class<T> cls;
        public final Gson gson;
        private final HttpCall httpCall;
        public final Object resultKey;

        private MyWorker(Cache cache2, Object resultKey2, Class<T> cls2, Gson gson2, HttpCall httpCall2) {
            this.cache = cache2;
            this.resultKey = resultKey2;
            this.cls = cls2;
            this.gson = gson2;
            this.httpCall = httpCall2;
        }

        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void start(final ResultListener<Result<T>> listener) {
            Result<T> r;
            if (hasCache()) {
                synchronized (this.cache) {
                    r = this.cache.get(this.resultKey);
                }
                if (r != null) {
                    listener.onResult(r);
                    return;
                }
            }
            this.httpCall.getResponseAsync(new Callback() {
                public void processResponse(int httpStatus, InputStream stream, HttpHeaders httpHeaders) throws Exception {
                    if (httpStatus < 200 || httpStatus > 299) {
                        Result<T> result = new Result<>(new HttpError(httpStatus, httpStatus, stream));
                        if (MyWorker.this.hasCache()) {
                            synchronized (MyWorker.this.cache) {
                                MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                            }
                        }
                        listener.onResult(result);
                    } else if (MyWorker.this.cls == Void.class) {
                        listener.onResult(new Result(null));
                    } else {
                        StringWriter sw = new StringWriter();
                        try {
                            InputStreamReader r = new InputStreamReader(new BufferedInputStream(stream));
                            try {
                                Result<T> result2 = new Result<>(MyWorker.this.gson.fromJson((Reader) r, MyWorker.this.cls));
                                if (MyWorker.this.hasCache()) {
                                    synchronized (MyWorker.this.cache) {
                                        MyWorker.this.cache.put(MyWorker.this.resultKey, result2);
                                    }
                                }
                                listener.onResult(result2);
                            } finally {
                                r.close();
                            }
                        } finally {
                            sw.close();
                        }
                    }
                }
            });
        }

        public void cancel() {
        }
    }

    public static class Result<T> extends LoaderResult<T> {
        protected Result(T data) {
            super(data, null);
        }

        protected Result(HttpError error) {
            super(null, error);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public ObjectLoader(Context context, Class<T> cls, Gson gson, HttpCall httpCall) {
        this(context, null, null, cls, gson, httpCall);
    }

    public ObjectLoader(Context context, Cache cache, Object resultKey, Class<T> cls, Gson gson, HttpCall httpCall) {
        super(context, new MyWorker(cache, resultKey, cls, gson, httpCall));
    }

    public boolean isDataReleased(Result<T> result) {
        return result.isReleased();
    }

    public void releaseData(Result<T> result) {
        result.release();
    }
}
