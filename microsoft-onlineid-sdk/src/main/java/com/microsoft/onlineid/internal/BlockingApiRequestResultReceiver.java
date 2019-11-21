package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import com.microsoft.onlineid.internal.exception.UserCancelledException;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BlockingApiRequestResultReceiver<ResultType> extends ApiRequestResultReceiver {
    private final BlockingQueue<Result> _queue = new LinkedBlockingQueue();

    public class Result {
        private final Exception _exception;
        private final SsoResponse<ResultType> _result;

        private Result(ResultType result) {
            this._result = new SsoResponse().setData(result);
            this._exception = null;
        }

        private Result(PendingIntent intent) {
            this._result = new SsoResponse().setPendingIntent(intent);
            this._exception = null;
        }

        private Result(Exception e) {
            this._result = null;
            this._exception = e;
        }

        public SsoResponse<ResultType> getSsoResponse() {
            return this._result;
        }

        private Exception getException() {
            return this._exception;
        }
    }

    public BlockingApiRequestResultReceiver() {
        super(null);
    }

    public SsoResponse<ResultType> blockForResult() throws Exception {
        Result result = (Result) this._queue.take();
        if (result == null) {
            throw new IllegalStateException("Expect a result to be available.");
        } else if (result.getException() == null) {
            return result.getSsoResponse();
        } else {
            throw result.getException();
        }
    }

    protected void setResult(ResultType result) {
        this._queue.add(new Result((Object) result));
    }

    protected void onUserCancel() {
        this._queue.add(new Result(new UserCancelledException()));
    }

    protected void onUINeeded(PendingIntent intent) {
        this._queue.add(new Result(intent));
    }

    protected void onFailure(Exception e) {
        this._queue.add(new Result(e));
    }
}
