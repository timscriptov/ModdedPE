package com.microsoft.xbox.toolkit.ui.util;

import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class LibCompat {
    private LibCompat() {
    }

    public static void setTextAppearance(@NotNull TextView textView, int resId) {
        textView.setTextAppearance(textView.getContext(), resId);
    }
}
