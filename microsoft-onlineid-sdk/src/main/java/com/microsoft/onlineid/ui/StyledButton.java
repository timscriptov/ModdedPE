package com.microsoft.onlineid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.microsoft.onlineid.internal.ui.Fonts;

public class StyledButton extends Button {
    public StyledButton(Context context, AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
        initialize(context);
    }

    public StyledButton(Context context, AttributeSet attributes) {
        super(context, attributes);
        initialize(context);
    }

    public StyledButton(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        if (!isInEditMode()) {
            setTypeface(Fonts.SegoeUISemiBold.getTypeface(context));
        }
    }
}
