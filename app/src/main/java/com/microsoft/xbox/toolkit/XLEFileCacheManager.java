package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEFileCacheManager {
    public static XLEFileCache emptyFileCache = new XLEFileCache();
    private static HashMap<String, XLEFileCache> sAllCaches = new HashMap<>();
    private static HashMap<XLEFileCache, File> sCacheRootDirMap = new HashMap<>();

    public static synchronized XLEFileCache createCache(String subDirectory, int maxFileNumber) {
        XLEFileCache createCache;
        synchronized (XLEFileCacheManager.class) {
            createCache = createCache(subDirectory, maxFileNumber, true);
        }
        return createCache;
    }

    public static synchronized XLEFileCache createCache(String subDirectory, int maxFileNumber, boolean enabled) {
        XLEFileCache xLEFileCache;
        synchronized (XLEFileCacheManager.class) {
            if (maxFileNumber <= 0) {
                throw new IllegalArgumentException("maxFileNumber must be > 0");
            }
            if (subDirectory != null) {
                if (subDirectory.length() > 0) {
                    XLEFileCache fileCache = sAllCaches.get(subDirectory);
                    if (fileCache != null) {
                        if (fileCache.maxFileNumber != maxFileNumber) {
                            throw new IllegalArgumentException("The same subDirectory with different maxFileNumber already exist.");
                        }
                        XLEFileCache xLEFileCache2 = fileCache;
                        xLEFileCache = fileCache;
                    } else if (!enabled) {
                        xLEFileCache = emptyFileCache;
                        XLEFileCache xLEFileCache3 = fileCache;
                    } else if (!SystemUtil.isSDCardAvailable()) {
                        xLEFileCache = emptyFileCache;
                        XLEFileCache xLEFileCache4 = fileCache;
                    } else {
                        fileCache = new XLEFileCache(subDirectory, maxFileNumber);
                        File rootDir = new File(XboxTcuiSdk.getActivity().getCacheDir(), subDirectory);
                        if (!rootDir.exists()) {
                            rootDir.mkdirs();
                        }
                        fileCache.size = rootDir.list().length;
                        sAllCaches.put(subDirectory, fileCache);
                        sCacheRootDirMap.put(fileCache, rootDir);
                        XLEFileCache xLEFileCache22 = fileCache;
                        xLEFileCache = fileCache;
                    }
                }
            }
            throw new IllegalArgumentException("subDirectory must be not null and at least one character length");
        }
        //return xLEFileCache;
    }

    static File getCacheRootDir(XLEFileCache cache) {
        return sCacheRootDirMap.get(cache);
    }

    @NotNull
    public static String getCacheStatus() {
        return sAllCaches.values().toString();
    }
}
