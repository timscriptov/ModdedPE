package com.microsoft.xbox.toolkit;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MultiMap<K, V> {
    private Hashtable<K, HashSet<V>> data = new Hashtable<>();
    private Hashtable<V, K> dataInverse = new Hashtable<>();

    public HashSet<V> get(K key) {
        return data.get(key);
    }

    public int size() {
        return data.size();
    }

    public int TESTsizeDegenerate() {
        int count = 0;
        for (K key : data.keySet()) {
            if (data.get(key).size() == 0) {
                count++;
            }
        }
        return count;
    }

    public void clear() {
        data.clear();
        dataInverse.clear();
    }

    public boolean containsKey(K key) {
        return data.containsKey(key);
    }

    public boolean containsValue(V value) {
        return getKey(value) != null;
    }

    public K getKey(V value) {
        return dataInverse.get(value);
    }

    public void removeValue(V view) {
        K key = getKey(view);
        data.get(key).remove(view);
        dataInverse.remove(view);
        removeKeyIfEmpty(key);
    }

    public void removeKey(K key) {
        Iterator it = data.get(key).iterator();
        while (it.hasNext()) {
            V value = (V) it.next();
            XLEAssert.assertTrue(dataInverse.containsKey(value));
            dataInverse.remove(value);
        }
        data.remove(key);
    }

    public void put(K key, V value) {
        if (data.get(key) == null) {
            data.put(key, new HashSet());
        }
        XLEAssert.assertTrue(!dataInverse.containsKey(value));
        data.get(key).add(value);
        dataInverse.put(value, key);
    }

    public boolean keyValueMatches(K key, V value) {
        HashSet<V> vset = get(key);
        if (vset == null) {
            return false;
        }
        return vset.contains(value);
    }

    private void removeKeyIfEmpty(K key) {
        HashSet<V> vset = get(key);
        if (vset != null && vset.isEmpty()) {
            data.remove(key);
        }
    }
}
