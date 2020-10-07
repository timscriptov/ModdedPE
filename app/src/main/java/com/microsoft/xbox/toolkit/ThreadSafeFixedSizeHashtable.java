package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ThreadSafeFixedSizeHashtable<K, V> {
    private final int maxSize;
    private int count = 0;
    private PriorityQueue<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> fifo = new PriorityQueue<>();
    private Hashtable<K, V> hashtable = new Hashtable<>();
    private Object syncObject = new Object();

    public ThreadSafeFixedSizeHashtable(int maxSize2) {
        maxSize = maxSize2;
        if (maxSize2 <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public void put(K key, V value) {
        if (key != null && value != null) {
            synchronized (syncObject) {
                if (!hashtable.containsKey(key)) {
                    count++;
                    fifo.add(new KeyTuple(key, count));
                    hashtable.put(key, value);
                    cleanupIfNecessary();
                }
            }
        }
    }

    public V get(K key) {
        V v;
        if (key == null) {
            return null;
        }
        synchronized (syncObject) {
            v = hashtable.get(key);
        }
        return v;
    }

    public void remove(K key) {
        if (key != null) {
            synchronized (syncObject) {
                if (hashtable.containsKey(key)) {
                    hashtable.remove(key);
                    ThreadSafeFixedSizeHashtable<K, V>.KeyTuple matchKeyTuple = null;
                    Iterator<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> queueIterator = fifo.iterator();
                    while (true) {
                        if (!queueIterator.hasNext()) {
                            break;
                        }
                        ThreadSafeFixedSizeHashtable<K, V>.KeyTuple keyTuple = queueIterator.next();
                        if (keyTuple.key.equals(key)) {
                            matchKeyTuple = keyTuple;
                            break;
                        }
                    }
                    if (matchKeyTuple != null) {
                        fifo.remove(matchKeyTuple);
                    }
                }
            }
        }
    }

    public Enumeration<V> elements() {
        return hashtable.elements();
    }

    public Enumeration<K> keys() {
        return hashtable.keys();
    }

    private void cleanupIfNecessary() {
        boolean z;
        boolean z2;
        if (hashtable.size() == fifo.size()) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        while (hashtable.size() > maxSize) {
            hashtable.remove(((KeyTuple) fifo.remove()).getKey());
            if (hashtable.size() == fifo.size()) {
                z2 = true;
            } else {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
        }
    }

    private class KeyTuple implements Comparable<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> {
        public K key;
        private int index = 0;

        public KeyTuple(K key2, int index2) {
            key = key2;
            index = index2;
        }

        public int compareTo(@NotNull ThreadSafeFixedSizeHashtable<K, V>.KeyTuple rhs) {
            return index - rhs.index;
        }

        public K getKey() {
            return key;
        }
    }
}
