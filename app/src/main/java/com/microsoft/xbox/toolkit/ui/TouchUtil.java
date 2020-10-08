package com.microsoft.xbox.toolkit.ui;

import android.view.View;
import android.widget.AdapterView;

import com.microsoft.xbox.toolkit.XLEAssert;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TouchUtil {
    public static View.OnClickListener createOnClickListener(View.OnClickListener listener) {
        if (listener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", listener);
        return listener;
    }

    public static AdapterView.OnItemClickListener createOnItemClickListener(AdapterView.OnItemClickListener listener) {
        if (listener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", listener);
        return listener;
    }

    public static View.OnLongClickListener createOnLongClickListener(View.OnLongClickListener listener) {
        if (listener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", listener);
        return listener;
    }
}
