package com.microsoft.xbox.idp.util;

import android.app.LoaderManager.LoaderCallbacks;

import com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;

public class ObjectLoaderInfo implements LoaderInfo {
    private final LoaderCallbacks<?> callbacks;

    public ObjectLoaderInfo(LoaderCallbacks<?> callbacks2) {
        this.callbacks = callbacks2;
    }

    public LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public void clearCache(Object key) {
        Cache cache = CacheUtil.getObjectLoaderCache();
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public boolean hasCachedData(Object key) {
        boolean z;
        Cache cache = CacheUtil.getObjectLoaderCache();
        synchronized (cache) {
            z = cache.get(key) != null;
        }
        return z;
    }
}
