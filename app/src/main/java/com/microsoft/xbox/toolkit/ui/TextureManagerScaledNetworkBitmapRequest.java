package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLEFileCacheItemKey;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureManagerScaledNetworkBitmapRequest implements XLEFileCacheItemKey {
    public final TextureBindingOption bindingOption;
    public final String url;

    public TextureManagerScaledNetworkBitmapRequest(String url2) {
        this(url2, new TextureBindingOption());
    }

    public TextureManagerScaledNetworkBitmapRequest(String url2, TextureBindingOption option) {
        url = url2;
        bindingOption = option;
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureManagerScaledNetworkBitmapRequest)) {
            return false;
        }
        TextureManagerScaledNetworkBitmapRequest rhs = (TextureManagerScaledNetworkBitmapRequest) rhsuntyped;
        if (!url.equals(rhs.url) || !bindingOption.equals(rhs.bindingOption)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (url == null) {
            return 0;
        }
        return url.hashCode();
    }

    public String getKeyString() {
        return url;
    }
}
