package com.microsoft.onlineid.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import com.microsoft.onlineid.internal.ui.Fonts;
import com.microsoft.onlineid.sdk.R;

public class StyledTextView extends TextView {
    public StyledTextView(Context context, AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
        applyAttributes(context, attributes);
    }

    public StyledTextView(Context context, AttributeSet attributes) {
        super(context, attributes);
        applyAttributes(context, attributes);
    }

    public StyledTextView(Context context) {
        super(context);
    }

    private void applyAttributes(Context context, AttributeSet attributes) {
        TypedArray styleAttributes = context.getTheme().obtainStyledAttributes(attributes, R.styleable.StyledTextView, 0, 0);
        for (int idx = 0; idx < styleAttributes.getIndexCount(); idx++) {
            int attr = styleAttributes.getIndex(idx);
            if (attr == R.styleable.StyledTextView_font) {
                if (!isInEditMode()) {
                    String fontName = styleAttributes.getString(attr);
                    if (fontName != null) {
                        setTypeface(Fonts.valueOf(fontName).getTypeface(context));
                    }
                }
            } else if (attr == R.styleable.StyledTextView_isUnderlined) {
                if (styleAttributes.getBoolean(attr, false)) {
                    setPaintFlags(getPaintFlags() | 8);
                } else {
                    setPaintFlags(getPaintFlags() & -9);
                }
            }
        }
        styleAttributes.recycle();
    }
}
