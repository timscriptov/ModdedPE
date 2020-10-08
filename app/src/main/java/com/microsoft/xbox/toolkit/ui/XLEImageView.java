package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEImageView extends AppCompatImageView {
    public static final int IMAGE_ERROR = 2;
    public static final int IMAGE_FINAL = 0;
    public static final int IMAGE_LOADING = 1;
    public String TEST_loadingOrLoadedImageUrl;
    protected boolean isFinal;
    protected boolean shouldAnimate;

    public XLEImageView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public XLEImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XLEImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        shouldAnimate = true;
        isFinal = false;
        setSoundEffectsEnabled(false);
    }

    public boolean getShouldAnimate() {
        return shouldAnimate && !isFinal;
    }

    public void setShouldAnimate(boolean value) {
        shouldAnimate = value;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            super.setImageBitmap(bitmap);
        }
    }

    public void setImageSource(Bitmap bitmap, int source) {
        if (bitmap != null) {
            super.setImageBitmap(bitmap);
        }
    }

    public void setFinal(boolean value) {
        isFinal = value;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}
