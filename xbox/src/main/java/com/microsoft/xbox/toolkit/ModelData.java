package com.microsoft.xbox.toolkit;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface ModelData<T> {
    void updateWithNewData(AsyncResult<T> asyncResult);
}
