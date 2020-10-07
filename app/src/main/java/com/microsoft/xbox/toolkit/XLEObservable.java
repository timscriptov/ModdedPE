package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class XLEObservable<T> {
    private HashSet<XLEObserver<T>> data = new HashSet<>();

    public synchronized void addUniqueObserver(XLEObserver<T> observer) {
        if (!data.contains(observer)) {
            addObserver(observer);
        }
    }

    public synchronized void addObserver(XLEObserver<T> observer) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        data.add(observer);
    }

    public synchronized void removeObserver(XLEObserver<T> observer) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        data.remove(observer);
    }

    public synchronized void notifyObservers(AsyncResult<T> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        for (XLEObserver<T> observer : new ArrayList<>(data)) {
            observer.update(asyncResult);
        }
    }

    public synchronized void clearObserver() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        data.clear();
    }

    public synchronized ArrayList<XLEObserver<T>> getObservers() {
        return new ArrayList<>(data);
    }
}
