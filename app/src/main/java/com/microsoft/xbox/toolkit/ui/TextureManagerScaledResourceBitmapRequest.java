package com.microsoft.xbox.toolkit.ui;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureManagerScaledResourceBitmapRequest {
    public final TextureBindingOption bindingOption;
    public final int resourceId;

    public TextureManagerScaledResourceBitmapRequest(int resourceId2) {
        this(resourceId2, new TextureBindingOption());
    }

    public TextureManagerScaledResourceBitmapRequest(int resourceId2, TextureBindingOption option) {
        resourceId = resourceId2;
        bindingOption = option;
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureManagerScaledResourceBitmapRequest)) {
            return false;
        }
        TextureManagerScaledResourceBitmapRequest rhs = (TextureManagerScaledResourceBitmapRequest) rhsuntyped;
        if (resourceId != rhs.resourceId || !bindingOption.equals(rhs.bindingOption)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return resourceId;
    }
}
