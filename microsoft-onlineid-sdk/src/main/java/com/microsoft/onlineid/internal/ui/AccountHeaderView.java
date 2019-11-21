package com.microsoft.onlineid.internal.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class AccountHeaderView {
    public static final float MarginLargeDip = 16.0f;
    public static final float MarginLogoDip = 9.3f;
    public static final float MarginMediumDip = 8.0f;
    public static final float SizeLogoDip = 32.0f;
    public static final int TextColorTitle = Color.rgb(88, 88, 88);
    public static final float TextSizeLargeSP = 16.0f;

    private static class CustomTypefaceSpan extends MetricAffectingSpan {
        private Typeface _typeface;

        public CustomTypefaceSpan(Typeface typeface) {
            this._typeface = typeface;
        }

        public void updateMeasureState(TextPaint p) {
            p.setTypeface(this._typeface);
            p.setFlags(p.getFlags() | 128);
        }

        public void updateDrawState(TextPaint p) {
            p.setTypeface(this._typeface);
            p.setFlags(p.getFlags() | 128);
        }
    }

    public static void applyStyle(Activity activity, CharSequence title) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            String baseTitle = title.toString();
            SpannableString titleSpan = new SpannableString(baseTitle);
            titleSpan.setSpan(new CustomTypefaceSpan(Fonts.SegoeUISemiBold.getTypeface(activity.getApplicationContext())), TextColorTitle, baseTitle.length(), 18);
            actionBar.setTitle(titleSpan);
            View home = activity.findViewById(16908332);
            home.setPadding(TextColorTitle, TextColorTitle, TextColorTitle, TextColorTitle);
            int logoSizePixels = Dimensions.convertDipToPixels(SizeLogoDip, activity.getResources().getDisplayMetrics());
            LayoutParams params = home.getLayoutParams();
            params.height = logoSizePixels;
            params.width = logoSizePixels;
        }
    }
}
