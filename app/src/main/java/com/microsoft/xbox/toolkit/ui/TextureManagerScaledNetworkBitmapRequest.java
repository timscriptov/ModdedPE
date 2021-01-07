package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLEFileCacheItemKey;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureManagerScaledNetworkBitmapRequest implements XLEFileCacheItemKey {
    public final TextureBindingOption bindingOption;
    public final String url;

    public TextureManagerScaledNetworkBitmapRequest(String str) {
        this(str, new TextureBindingOption());
    }

    public TextureManagerScaledNetworkBitmapRequest(String str, TextureBindingOption textureBindingOption) {
        this.url = str;
        this.bindingOption = textureBindingOption;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TextureManagerScaledNetworkBitmapRequest)) {
            return false;
        }
        TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest = (TextureManagerScaledNetworkBitmapRequest) obj;
        if (!this.url.equals(textureManagerScaledNetworkBitmapRequest.url) || !this.bindingOption.equals(textureManagerScaledNetworkBitmapRequest.bindingOption)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        String str = this.url;
        if (str == null) {
            return 0;
        }
        return str.hashCode();
    }

    public String getKeyString() {
        return this.url;
    }
}
