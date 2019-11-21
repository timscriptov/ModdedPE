package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.microsoft.xbox.idp.toolkit.BitmapLoader.Result;

public class BitmapLoader extends WorkerLoader<Result> {
    public static final String TAG = BitmapLoader.class.getSimpleName();

    public interface Cache {
        void clear();

        Bitmap get(Object obj);

        Bitmap put(Object obj, Bitmap bitmap);

        Bitmap remove(Object obj);
    }

    private static class MyWorker implements Worker<Result> {
        static final boolean $assertionsDisabled = (!BitmapLoader.class.desiredAssertionStatus());
        public final Cache cache;
        public final Object resultKey;
        public final String urlString;

        private MyWorker(Cache cache2, Object resultKey2, String urlString2) {
            if ($assertionsDisabled || urlString2 != null) {
                this.cache = cache2;
                this.resultKey = resultKey2;
                this.urlString = urlString2;
                return;
            }
            throw new AssertionError();
        }

        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void start(final ResultListener<Result> listener) {
            final Bitmap data;
            if (hasCache()) {
                synchronized (this.cache) {
                    data = this.cache.get(this.resultKey);
                }
                if (data != null) {
                    Log.d(BitmapLoader.TAG, "Successfully retrieved Bitmap from BitmapLoader.Cache");
                    new Thread(new Runnable() {
                        public void run() {
                            listener.onResult(new Result(data));
                        }
                    }).start();
                    return;
                }
            }
            new Thread(new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:22:0x0072, code lost:
                    r0 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:23:0x0073, code lost:
                    android.util.Log.e(com.microsoft.xbox.idp.toolkit.BitmapLoader.access$100(), "Received malformed URL: " + com.microsoft.xbox.idp.toolkit.BitmapLoader.MyWorker.access$200(r7.this$0));
                    r5.onResult(new com.microsoft.xbox.idp.toolkit.BitmapLoader.Result((java.lang.Exception) r0));
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:24:0x009e, code lost:
                    r0 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:25:0x009f, code lost:
                    r5.onResult(new com.microsoft.xbox.idp.toolkit.BitmapLoader.Result(r0));
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
                    return;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
                    return;
                 */
                /* JADX WARNING: Failed to process nested try/catch */
                /* JADX WARNING: Removed duplicated region for block: B:22:0x0072 A[ExcHandler: MalformedURLException (r0v1 'e' java.net.MalformedURLException A[CUSTOM_DECLARE]), Splitter:B:0:0x0000] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    //Not Decompile
                    throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.toolkit.BitmapLoader.MyWorker.AnonymousClass2.run():void");
                }
            }).start();
        }

        public void cancel() {
        }
    }

    public static class Result extends LoaderResult<Bitmap> {
        protected Result(Bitmap data) {
            super(data, null);
        }

        protected Result(Exception exception) {
            super(exception);
        }

        public boolean isReleased() {
            return hasData() && ((Bitmap) getData()).isRecycled();
        }

        public void release() {
            if (hasData()) {
                ((Bitmap) getData()).recycle();
            }
        }
    }

    public BitmapLoader(Context context, String urlString) {
        this(context, null, null, urlString);
    }

    public BitmapLoader(Context context, Cache cache, Object resultKey, String urlString) {
        super(context, new MyWorker(cache, resultKey, urlString));
    }

    public boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    public void releaseData(Result result) {
        result.release();
    }
}
