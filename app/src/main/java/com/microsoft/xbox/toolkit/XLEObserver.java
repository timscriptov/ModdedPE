package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface XLEObserver<T> {
    void update(AsyncResult<T> asyncResult);
}
