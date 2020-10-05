package com.microsoft.xbox.idp.util;

import android.app.LoaderManager;

import com.microsoft.xbox.idp.toolkit.BitmapLoader;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BitmapLoaderInfo implements ErrorHelper.LoaderInfo {
    private final LoaderManager.LoaderCallbacks<?> callbacks;

    public BitmapLoaderInfo(LoaderManager.LoaderCallbacks<?> callbacks2) {
        callbacks = callbacks2;
    }

    public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks() {
        return callbacks;
    }

    public void clearCache(Object key) {
        BitmapLoader.Cache cache = CacheUtil.getBitmapCache();
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public boolean hasCachedData(Object key) {
        boolean z;
        BitmapLoader.Cache cache = CacheUtil.getBitmapCache();
        synchronized (cache) {
            z = cache.get(key) != null;
        }
        return z;
    }
}
