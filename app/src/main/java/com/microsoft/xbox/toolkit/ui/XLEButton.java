package com.microsoft.xbox.toolkit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.microsoft.xbox.toolkit.XLERValueHelper;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEButton extends AppCompatButton {
    protected boolean disableSound;
    protected ButtonStateHandler stateHandler;
    private boolean alwaysClickable;
    private int disabledTextColor;
    private int enabledTextColor;

    public XLEButton(Context context) {
        super(context);
        stateHandler = new ButtonStateHandler();
        disableSound = false;
        setSoundEffectsEnabled(false);
    }

    public XLEButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XLEButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        stateHandler = new ButtonStateHandler();
        disableSound = false;
        if (!isInEditMode()) {
            setSoundEffectsEnabled(false);
            TypedArray typefaceAttr = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("XLEButton"));
            try {
                stateHandler.setDisabled(typefaceAttr.getBoolean(XLERValueHelper.getStyleableRValue("XLEButton_disabled"), false));
                stateHandler.setDisabledImageHandle(typefaceAttr.getResourceId(XLERValueHelper.getStyleableRValue("XLEButton_disabledImage"), -1));
                stateHandler.setEnabledImageHandle(typefaceAttr.getResourceId(XLERValueHelper.getStyleableRValue("XLEButton_enabledImage"), -1));
                stateHandler.setPressedImageHandle(typefaceAttr.getResourceId(XLERValueHelper.getStyleableRValue("XLEButton_pressedImage"), -1));
                disableSound = typefaceAttr.getBoolean(XLERValueHelper.getStyleableRValue("XLEButton_disableSound"), false);
                setLayoutParams(new FrameLayout.LayoutParams(-2, -2));
                typefaceAttr = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("CustomTypeface"));
                String typeface = typefaceAttr.getString(XLERValueHelper.getStyleableRValue("CustomTypeface_typefaceSource"));
                if (typeface != null && typeface.length() > 0) {
                    applyCustomTypeface(context, typeface);
                }
                enabledTextColor = getCurrentTextColor();
                disabledTextColor = typefaceAttr.getColor(XLERValueHelper.getStyleableRValue("XLEButton_disabledTextColor"), enabledTextColor);
                alwaysClickable = typefaceAttr.getBoolean(XLERValueHelper.getStyleableRValue("XLEButton_alwaysClickable"), false);
                if (alwaysClickable) {
                    super.setEnabled(true);
                    super.setClickable(true);
                }
                typefaceAttr.recycle();
            } finally {
                typefaceAttr.recycle();
            }
        }
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
    }

    public void setTypeFace(String typeface) {
        applyCustomTypeface(getContext(), typeface);
    }

    public void setEnabled(boolean enabled) {
        if (!alwaysClickable) {
            super.setEnabled(enabled);
        }
        if (stateHandler == null) {
            stateHandler = new ButtonStateHandler();
        }
        stateHandler.setEnabled(enabled);
        updateImage();
        updateTextColor();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onFinishInflate() {
        super.onFinishInflate();
        updateImage();
        setOnTouchListener((v, event) -> {
            boolean handled = stateHandler.onTouch(event);
            updateImage();
            return handled;
        });
    }

    public void setOnClickListener(View.OnClickListener listener) {
        if (disableSound) {
            super.setOnClickListener(listener);
        } else {
            super.setOnClickListener(TouchUtil.createOnClickListener(listener));
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        if (disableSound) {
            super.setOnLongClickListener(listener);
        } else {
            super.setOnLongClickListener(TouchUtil.createOnLongClickListener(listener));
        }
    }

    public void setPressedStateRunnable(ButtonStateHandler.ButtonStateHandlerRunnable runnable) {
        stateHandler.setPressedStateRunnable(runnable);
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boolean loadedNewImage = false;
        if (hasSize()) {
            loadedNewImage = stateHandler.onSizeChanged(getWidth(), getHeight());
        }
        if (loadedNewImage) {
            updateImage();
        }
    }

    private boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    public void updateImage() {
        if (stateHandler.getImageDrawable() != null) {
            setBackgroundDrawable(stateHandler.getImageDrawable());
        }
    }

    public void updateTextColor() {
        if (enabledTextColor != disabledTextColor) {
            setTextColor(stateHandler.getDisabled() ? disabledTextColor : enabledTextColor);
        }
    }
}
