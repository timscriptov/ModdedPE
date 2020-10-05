package com.microsoft.xbox.idp.util;

import android.app.LoaderManager;

import com.microsoft.xbox.idp.toolkit.ObjectLoader;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ObjectLoaderInfo implements ErrorHelper.LoaderInfo {
    private final LoaderManager.LoaderCallbacks<?> callbacks;

    public ObjectLoaderInfo(LoaderManager.LoaderCallbacks<?> callbacks2) {
        callbacks = callbacks2;
    }

    public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks() {
        return callbacks;
    }

    public void clearCache(Object key) {
        ObjectLoader.Cache cache = CacheUtil.getObjectLoaderCache();
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public boolean hasCachedData(Object key) {
        boolean z;
        ObjectLoader.Cache cache = CacheUtil.getObjectLoaderCache();
        synchronized (cache) {
            z = cache.get(key) != null;
        }
        return z;
    }
}
