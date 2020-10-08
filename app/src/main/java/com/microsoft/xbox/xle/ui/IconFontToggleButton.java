package com.microsoft.xbox.xle.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.FontManager;

import org.jetbrains.annotations.NotNull;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class IconFontToggleButton extends LinearLayout implements Checkable {
    private boolean checked;
    private String checkedIcon;
    private String checkedText;
    private AppCompatTextView iconTextView;
    private AppCompatTextView labelTextView;
    private String uncheckedIcon;
    private String uncheckedText;

    public IconFontToggleButton(Context context) {
        super(context);
    }

    public IconFontToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public IconFontToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    @SuppressLint("WrongConstant")
    private void initViews(@NotNull Context context, AttributeSet attrs) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.iconfont_toggle_btn_view, this, true);
        iconTextView = (AppCompatTextView) findViewById(R.id.iconfont_toggle_btn_icon);
        labelTextView = (AppCompatTextView) findViewById(R.id.iconfont_toggle_btn_text);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("CustomTypeface"));
        String typeface = a.getString(XLERValueHelper.getStyleableRValue("CustomTypeface_typefaceSource"));
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("IconFontToggleButton"));
        checkedText = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_text_checked"));
        uncheckedText = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_text_unchecked"));
        checkedIcon = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_checked"));
        uncheckedIcon = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_unchecked"));
        float iconSize = (float) a2.getDimensionPixelSize(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_size"), -1);
        if (iconSize != -1.0f) {
            iconTextView.setTextSize(0, iconSize);
        }
        a2.recycle();
        applyCustomTypeface(context, typeface);
        setFocusable(true);
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null && labelTextView != null) {
            labelTextView.setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
    }

    public boolean isChecked() {
        return checked;
    }

    @SuppressLint("WrongConstant")
    public void setChecked(boolean checked2) {
        checked = checked2;
        sendAccessibilityEvent(1);
        if (labelTextView != null) {
            labelTextView.setText(checked ? checkedText : uncheckedText);
            labelTextView.setVisibility(0);
        }
        if (iconTextView != null) {
            iconTextView.setText(checked ? checkedIcon : uncheckedIcon);
            iconTextView.setVisibility(0);
        }
        invalidate();
    }

    public void toggle() {
        setChecked(!checked);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClickable(true);
        info.setClassName(AppCompatButton.class.getName());
    }

    public void setCheckedText(String text) {
        checkedText = text;
    }

    public void setUncheckedText(String text) {
        uncheckedText = text;
    }
}
