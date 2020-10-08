package com.microsoft.xbox.toolkit.ui;

import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.xbox.toolkit.XLERValueHelper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureBindingOption {
    public static final int DO_NOT_SCALE = -1;
    public static final int DO_NOT_USE_PLACEHOLDER = -1;
    public static final TextureBindingOption DefaultBindingOption = new TextureBindingOption();
    public static final int DefaultResourceIdForEmpty = XLERValueHelper.getDrawableRValue("empty");
    public static final int DefaultResourceIdForError = XLERValueHelper.getDrawableRValue(AuthenticationConstants.OAuth2.ERROR);
    public static final int DefaultResourceIdForLoading = XLERValueHelper.getDrawableRValue("empty");
    public static final TextureBindingOption KeepAsIsBindingOption = new TextureBindingOption(-1, -1, -1, -1, false);
    public final int height;
    public final int resourceIdForError;
    public final int resourceIdForLoading;
    public final boolean useFileCache;
    public final int width;

    public TextureBindingOption() {
        this(-1, -1, DefaultResourceIdForLoading, DefaultResourceIdForError, false);
    }

    public TextureBindingOption(int width2, int height2) {
        this(width2, height2, true);
    }

    public TextureBindingOption(int width2, int height2, boolean useFileCache2) {
        this(width2, height2, DefaultResourceIdForLoading, DefaultResourceIdForError, useFileCache2);
    }

    public TextureBindingOption(int width2, int height2, int resourceForLoading, int resourceForError, boolean useFileCache2) {
        width = width2;
        height = height2;
        resourceIdForLoading = resourceForLoading;
        resourceIdForError = resourceForError;
        useFileCache = useFileCache2;
    }

    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static TextureBindingOption createDoNotScale(int resourceForLoading, int resourceForError, boolean useFileCache2) {
        return new TextureBindingOption(-1, -1, resourceForLoading, resourceForError, useFileCache2);
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureBindingOption)) {
            return false;
        }
        TextureBindingOption rhs = (TextureBindingOption) rhsuntyped;
        if (width == rhs.width && height == rhs.height && resourceIdForError == rhs.resourceIdForError && resourceIdForLoading == rhs.resourceIdForLoading) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}