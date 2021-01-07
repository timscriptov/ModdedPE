package com.microsoft.xbox.toolkit.ui;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureManagerDownloadRequest implements Comparable<TextureManagerDownloadRequest> {
    private static AtomicInteger nextIndex = new AtomicInteger(0);
    public int index = nextIndex.incrementAndGet();
    public TextureManagerScaledNetworkBitmapRequest key;
    public InputStream stream;

    public TextureManagerDownloadRequest(TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest) {
        this.key = textureManagerScaledNetworkBitmapRequest;
    }

    public int compareTo(@NotNull TextureManagerDownloadRequest textureManagerDownloadRequest) {
        return this.index - textureManagerDownloadRequest.index;
    }
}
