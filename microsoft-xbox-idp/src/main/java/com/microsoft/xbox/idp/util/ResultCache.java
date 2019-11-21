package com.microsoft.xbox.idp.util;

import java.util.HashMap;

public class ResultCache<R> {
    private final HashMap<Object, R> map = new HashMap<>();

    public R get(Object key) {
        return this.map.get(key);
    }

    public R put(Object key, R value) {
        return this.map.put(key, value);
    }

    public R remove(Object key) {
        return this.map.remove(key);
    }

    public void clear() {
        this.map.clear();
    }
}
