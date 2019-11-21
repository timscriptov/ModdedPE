package com.microsoft.xbox.idp.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.microsoft.xbox.idp.toolkit.BitmapLoader.Cache;

public class BitmapLoaderCache implements Cache {
    private final LruCache<Object, Bitmap> cache;

    public BitmapLoaderCache(int numOfEntries) {
        this.cache = new LruCache<>(numOfEntries);
    }

    public Bitmap get(Object key) {
        return (Bitmap) this.cache.get(key);
    }

    public Bitmap put(Object key, Bitmap value) {
        return (Bitmap) this.cache.put(key, value);
    }

    public Bitmap remove(Object key) {
        return (Bitmap) this.cache.remove(key);
    }

    public void clear() {
        this.cache.evictAll();
    }
}
