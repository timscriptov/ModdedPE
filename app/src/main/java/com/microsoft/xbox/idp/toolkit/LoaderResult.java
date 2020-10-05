package com.microsoft.xbox.idp.toolkit;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class LoaderResult<T> {
    private final T data;
    private final HttpError error;
    private final Exception exception;

    protected LoaderResult(T data2, HttpError error2) {
        data = data2;
        error = error2;
        exception = null;
    }

    protected LoaderResult(Exception exception2) {
        data = null;
        error = null;
        exception = exception2;
    }

    public abstract boolean isReleased();

    public abstract void release();

    public T getData() {
        return data;
    }

    public HttpError getError() {
        return error;
    }

    public Exception getException() {
        return exception;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean hasException() {
        return exception != null;
    }
}
