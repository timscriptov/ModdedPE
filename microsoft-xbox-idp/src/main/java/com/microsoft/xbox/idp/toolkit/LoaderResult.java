package com.microsoft.xbox.idp.toolkit;

public abstract class LoaderResult<T> {
    private final T data;
    private final HttpError error;
    private final Exception exception;

    public abstract boolean isReleased();

    public abstract void release();

    protected LoaderResult(T data2, HttpError error2) {
        this.data = data2;
        this.error = error2;
        this.exception = null;
    }

    protected LoaderResult(Exception exception2) {
        this.data = null;
        this.error = null;
        this.exception = exception2;
    }

    public T getData() {
        return this.data;
    }

    public HttpError getError() {
        return this.error;
    }

    public Exception getException() {
        return this.exception;
    }

    public boolean hasData() {
        return this.data != null;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public boolean hasException() {
        return this.exception != null;
    }
}
