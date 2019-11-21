package com.microsoft.xbox.idp.util;

import android.app.LoaderManager.LoaderCallbacks;

import com.microsoft.xbox.idp.toolkit.BitmapLoader.Cache;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;

public class BitmapLoaderInfo implements LoaderInfo {
    private final LoaderCallbacks<?> callbacks;

    public BitmapLoaderInfo(LoaderCallbacks<?> callbacks2) {
        this.callbacks = callbacks2;
    }

    public LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public void clearCache(Object key) {
        Cache cache = CacheUtil.getBitmapCache();
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public boolean hasCachedData(Object key) {
        boolean z;
        Cache cache = CacheUtil.getBitmapCache();
        synchronized (cache) {
            z = cache.get(key) != null;
        }
        return z;
    }
}
