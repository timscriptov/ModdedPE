package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * 07.01.2021
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

    public ThreadSafeFixedSizeHashtable(int i) {
        this.maxSize = i;
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public void put(K k, V v) {
        if (k != null && v != null) {
            synchronized (this.syncObject) {
                if (!this.hashtable.containsKey(k)) {
                    this.count++;
                    this.fifo.add(new KeyTuple(k, this.count));
                    this.hashtable.put(k, v);
                    cleanupIfNecessary();
                }
            }
        }
    }

    public V get(K k) {
        V v;
        if (k == null) {
            return null;
        }
        synchronized (this.syncObject) {
            v = this.hashtable.get(k);
        }
        return v;
    }

    public void remove(K k) {
        if (k != null) {
            synchronized (this.syncObject) {
                if (this.hashtable.containsKey(k)) {
                    this.hashtable.remove(k);
                    KeyTuple keyTuple = null;
                    Iterator<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> it = this.fifo.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        KeyTuple next = it.next();
                        if (next.key.equals(k)) {
                            keyTuple = next;
                            break;
                        }
                    }
                    if (keyTuple != null) {
                        this.fifo.remove(keyTuple);
                    }
                }
            }
        }
    }

    public Enumeration<V> elements() {
        return this.hashtable.elements();
    }

    public Enumeration<K> keys() {
        return this.hashtable.keys();
    }

    private void cleanupIfNecessary() {
        XLEAssert.assertTrue(this.hashtable.size() == this.fifo.size());
        while (this.hashtable.size() > this.maxSize) {
            this.hashtable.remove(((KeyTuple) this.fifo.remove()).getKey());
            XLEAssert.assertTrue(this.hashtable.size() == this.fifo.size());
        }
    }

    private class KeyTuple implements Comparable<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> {
        public K key;
        private int index = 0;

        public KeyTuple(K k, int i) {
            this.key = k;
            this.index = i;
        }

        public int compareTo(ThreadSafeFixedSizeHashtable<K, V>.@NotNull KeyTuple keyTuple) {
            return this.index - keyTuple.index;
        }

        public K getKey() {
            return this.key;
        }
    }
}
