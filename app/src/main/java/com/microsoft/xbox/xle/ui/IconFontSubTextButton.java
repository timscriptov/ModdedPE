package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.XLEUtil;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class IconFontSubTextButton extends LinearLayout {
    private FrameLayout iconFrameLayout;
    private CustomTypefaceTextView iconTextView;
    private CustomTypefaceTextView subtitleTextView;
    private CustomTypefaceTextView titleTextView;

    public IconFontSubTextButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public IconFontSubTextButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconFontSubTextButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.icon_font_subtext_button, this, true);
        iconTextView = findViewById(R.id.icon_font_subtext_icon);
        iconFrameLayout = findViewById(R.id.icon_font_subtext_btn_icon_bg);
        titleTextView = findViewById(R.id.icon_font_subtext_btn_title);
        subtitleTextView = findViewById(R.id.icon_font_subtext_btn_subtitle);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("IconFontSubTextButton"));
        String iconUri = a.getString(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_icon_uri"));
        String title = a.getString(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_text_title"));
        String subtitle = a.getString(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_text_subtitle"));
        iconFrameLayout.setBackgroundColor(a.getColor(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_icon_bg"), 0));
        a.recycle();
        XLEUtil.updateTextAndVisibilityIfNotNull(iconTextView, iconUri, 0);
        XLEUtil.updateTextAndVisibilityIfNotNull(titleTextView, title, 0);
        XLEUtil.updateTextAndVisibilityIfNotNull(subtitleTextView, subtitle, 0);
        setFocusable(true);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClickable(true);
        info.setClassName(Button.class.getName());
    }
}
