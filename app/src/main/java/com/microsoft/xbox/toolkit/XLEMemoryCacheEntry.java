package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEMemoryCacheEntry<V> {
    private int byteCount;
    private V data;

    public XLEMemoryCacheEntry(V data2, int byteCount2) {
        if (data2 == null) {
            throw new IllegalArgumentException("data");
        } else if (byteCount2 <= 0) {
            throw new IllegalArgumentException("byteCount");
        } else {
            data = data2;
            byteCount = byteCount2;
        }
    }

    public int getByteCount() {
        return byteCount;
    }

    public V getValue() {
        return data;
    }
}
