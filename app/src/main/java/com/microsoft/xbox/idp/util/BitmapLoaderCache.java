package com.microsoft.xbox.idp.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.microsoft.xbox.idp.toolkit.BitmapLoader;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BitmapLoaderCache implements BitmapLoader.Cache {
    private final LruCache<Object, Bitmap> cache;

    public BitmapLoaderCache(int numOfEntries) {
        cache = new LruCache<>(numOfEntries);
    }

    public Bitmap get(Object key) {
        return cache.get(key);
    }

    public Bitmap put(Object key, Bitmap value) {
        return cache.put(key, value);
    }

    public Bitmap remove(Object key) {
        return cache.remove(key);
    }

    public void clear() {
        cache.evictAll();
    }
}
