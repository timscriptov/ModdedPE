package com.microsoft.xbox.toolkit;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ThreadSafePriorityQueue<T> {
    private HashSet<T> hashSet = new HashSet<>();
    private PriorityQueue<T> queue = new PriorityQueue<>();
    private Object syncObject = new Object();

    public void push(T v) {
        synchronized (syncObject) {
            if (!hashSet.contains(v)) {
                queue.add(v);
                hashSet.add(v);
                syncObject.notifyAll();
            }
        }
    }

    public T pop() {
        T rv = null;
        try {
            synchronized (syncObject) {
                while (queue.isEmpty()) {
                    syncObject.wait();
                }
                rv = queue.remove();
                hashSet.remove(rv);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rv;
    }
}
