package com.microsoft.xbox.toolkit.ui;

import android.graphics.Typeface;

import com.microsoft.xbox.toolkit.system.SystemUtil;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLETextArg {
    private final Params params;
    private final String text;

    public XLETextArg(String text2, Params params2) {
        text = text2;
        params = params2;
    }

    public XLETextArg(Params params2) {
        this((String) null, params2);
    }

    public String getText() {
        return text;
    }

    public boolean hasText() {
        return text != null;
    }

    public Params getParams() {
        return params;
    }

    public static class Params {
        private final boolean adjustForImageSize;
        private final int color;
        private final int eraseColor;
        private final Float textAspectRatio;
        private final float textSize;
        private final Typeface typeface;

        public Params() {
            this((float) SystemUtil.SPtoPixels(8.0f), -1, Typeface.DEFAULT, 0, false, (Float) null);
        }

        public Params(float textSize2, int color2, Typeface typeface2, int eraseColor2, boolean adjustForImageSize2, Float textAspectRatio2) {
            textSize = textSize2;
            color = color2;
            typeface = typeface2;
            eraseColor = eraseColor2;
            adjustForImageSize = adjustForImageSize2;
            textAspectRatio = textAspectRatio2;
        }

        public float getTextSize() {
            return textSize;
        }

        public int getColor() {
            return color;
        }

        public Typeface getTypeface() {
            return typeface;
        }

        public boolean hasEraseColor() {
            return eraseColor != 0;
        }

        public int getEraseColor() {
            return eraseColor;
        }

        public boolean isAdjustForImageSize() {
            return adjustForImageSize;
        }

        public Float getTextAspectRatio() {
            return textAspectRatio;
        }

        public boolean hasTextAspectRatio() {
            return textAspectRatio != null;
        }
    }
}
