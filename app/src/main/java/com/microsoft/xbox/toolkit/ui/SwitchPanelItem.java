package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.microsoft.xbox.toolkit.XLERValueHelper;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SwitchPanelItem extends FrameLayout implements SwitchPanel.SwitchPanelChild {
    private final int INVALID_STATE_ID = -1;
    private int state;

    public SwitchPanelItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("SwitchPanelItem"));
        state = a.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanelItem_state"), -1);
        a.recycle();
        if (state < 0) {
            throw new IllegalArgumentException("You must specify the state attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
    }

    public int getState() {
        return state;
    }
}
