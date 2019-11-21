package com.microsoft.xbox.idp.toolkit;

import android.content.Context;

import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.Interop.XBLogoutCallback;
import com.microsoft.xbox.idp.toolkit.XBLogoutLoader.Result;

public class XBLogoutLoader extends WorkerLoader<Result> {

    private static class MyWorker implements Worker<Result> {
        private final long userPtr;

        private MyWorker(long userPtr2) {
            this.userPtr = userPtr2;
        }

        public void start(final ResultListener<Result> listener) {
            Interop.InvokeXBLogout(this.userPtr, new XBLogoutCallback() {
                public void onLoggedOut() {
                    listener.onResult(new Result());
                }
            });
        }

        public void cancel() {
        }
    }

    public static class Result extends LoaderResult<Void> {
        protected Result() {
            super(null, null);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public XBLogoutLoader(Context context, long userPtr) {
        super(context, new MyWorker(userPtr));
    }

    public boolean isDataReleased(Result data) {
        return data.isReleased();
    }

    public void releaseData(Result data) {
        data.release();
    }
}
