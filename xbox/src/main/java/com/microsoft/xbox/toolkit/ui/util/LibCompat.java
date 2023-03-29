package com.microsoft.xbox.toolkit.ui.util;

import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class LibCompat {
    private LibCompat() {
    }

    public static void setTextAppearance(@NotNull TextView textView, int i) {
        textView.setTextAppearance(textView.getContext(), i);
    }
}
