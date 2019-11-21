package com.microsoft.xbox.idp.util;

import com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Result;

import java.util.HashMap;

public class ObjectLoaderCache implements Cache {
    private final HashMap<Object, Result<?>> map = new HashMap<>();

    public <T> Result<T> get(Object key) {
        return (Result) this.map.get(key);
    }

    public <T> Result<T> put(Object key, Result<T> value) {
        return (Result) this.map.put(key, value);
    }

    public <T> Result<T> remove(Object key) {
        return (Result) this.map.remove(key);
    }

    public void clear() {
        this.map.clear();
    }
}
