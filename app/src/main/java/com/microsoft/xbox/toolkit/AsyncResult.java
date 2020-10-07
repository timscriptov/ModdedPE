package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class AsyncResult<T> {
    private final XLEException exception;
    private final T result;
    private final Object sender;
    private AsyncActionStatus status;

    public AsyncResult(T result2, Object sender2, XLEException exception2) {
        this(result2, sender2, exception2, exception2 == null ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL);
    }

    public AsyncResult(T result2, Object sender2, XLEException exception2, AsyncActionStatus status2) {
        sender = sender2;
        exception = exception2;
        result = result2;
        status = status2;
    }

    public Object getSender() {
        return sender;
    }

    public XLEException getException() {
        return exception;
    }

    public T getResult() {
        return result;
    }

    public AsyncActionStatus getStatus() {
        return status;
    }
}
