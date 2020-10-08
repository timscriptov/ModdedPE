package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class CustomTypefaceTextView extends AppCompatTextView {
    public CustomTypefaceTextView(Context context, String typeface) {
        super(context);
        applyCustomTypeface(context, typeface);
    }

    public CustomTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeface);
            String typeface = a.getString(R.styleable.CustomTypeface_typefaceSource);
            String uppercaseText = a.getString(R.styleable.CustomTypeface_uppercaseText);
            if (uppercaseText != null) {
                setText(uppercaseText.toUpperCase());
            }
            applyCustomTypeface(context, typeface);
            a.recycle();
        }*/
    }

    public CustomTypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /*if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeface);
            applyCustomTypeface(context, a.getString(R.styleable.CustomTypeface_typefaceSource));
            a.recycle();
        }*/
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
        setCursorVisible(false);
    }

    public void setOnClickListener(View.OnClickListener l) {
        throw new UnsupportedOperationException("If you want CustomTypefaceTextView to be clickable, use XLEButton instead.");
    }

    public void setClickable(boolean clickable) {
        if (clickable) {
            throw new UnsupportedOperationException("If you want CustomTypefaceTextView to be clickable, use XLEButton instead.");
        }
    }
}
