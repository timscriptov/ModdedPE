package com.microsoft.xbox.toolkit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;

import org.jetbrains.annotations.NotNull;
import org.spongycastle.asn1.cmp.PKIFailureInfo;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLECheckBox extends ViewGroup {
    public final AppCompatCheckBox checkBox;
    private final AppCompatTextView subText;
    private final AppCompatTextView text;

    public XLECheckBox(Context context) {
        super(context);
        checkBox = new AppCompatCheckBox(context);
        text = new AppCompatTextView(context);
        subText = new AppCompatTextView(context);
    }

    public XLECheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkBox = new AppCompatCheckBox(context, attrs);
        text = new AppCompatTextView(context, attrs);
        subText = new AppCompatTextView(context, attrs);
        initialize(context, attrs);
    }

    public XLECheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        checkBox = new AppCompatCheckBox(context, attrs);
        text = new AppCompatTextView(context, attrs);
        subText = new AppCompatTextView(context, attrs);
        initialize(context, attrs);
    }

    public CharSequence getText() {
        return text.getText();
    }

    public void setText(CharSequence text2) {
        text.setText(text2);
    }

    public CharSequence getSubText() {
        return subText.getText();
    }

    public void setSubText(CharSequence subText2) {
        subText.setText(subText2);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public void toggle() {
        checkBox.toggle();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
        text.setEnabled(enabled);
        subText.setEnabled(enabled);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        checkBox.setOnCheckedChangeListener(listener);
    }

    private void initialize(@NotNull Context context, AttributeSet attrs) {
        checkBox.setButtonDrawable(R.drawable.apptheme_btn_check_holo_light);
        addView(checkBox, new ViewGroup.LayoutParams(-2, -2));
        text.setOnClickListener(view -> checkBox.toggle());
        addView(text, new ViewGroup.LayoutParams(-2, -2));
        addView(subText, new ViewGroup.LayoutParams(-2, -2));
        /*TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLECheckBox);
        try {
            if (!isInEditMode()) {
                LibCompat.setTextAppearance(text, a.getResourceId(R.styleable.XLECheckBox_textStyle, -1));
                text.setTypeface(FontManager.Instance().getTypeface(context, a.getString(R.styleable.XLECheckBox_textTypefaceSource)));
                LibCompat.setTextAppearance(subText, a.getResourceId(R.styleable.XLECheckBox_subTextStyle, -1));
                subText.setTypeface(FontManager.Instance().getTypeface(context, a.getString(R.styleable.XLECheckBox_subTextTypefaceSource)));
            }
            text.setText(a.getString(R.styleable.XLECheckBox_text));
            subText.setText(a.getString(R.styleable.XLECheckBox_subText));
        } finally {
            a.recycle();
        }*/
    }

    @SuppressLint("WrongConstant")
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int wMyMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int wChildMode = wMyMode == 0 ? 0 : PKIFailureInfo.systemUnavail;
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        int hMyMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int hChildMode = hMyMode == 0 ? 0 : PKIFailureInfo.systemUnavail;
        int xCur = getPaddingLeft();
        int yCur = getPaddingTop();
        checkBox.measure(View.MeasureSpec.makeMeasureSpec(Math.max((w - xCur) - getPaddingRight(), 0), wChildMode), View.MeasureSpec.makeMeasureSpec(Math.max((h - yCur) - getPaddingBottom(), 0), hChildMode));
        int xCur2 = xCur + checkBox.getMeasuredWidth();
        text.measure(View.MeasureSpec.makeMeasureSpec(Math.max((w - xCur2) - getPaddingRight(), 0), wChildMode), View.MeasureSpec.makeMeasureSpec(Math.max((h - yCur) - getPaddingBottom(), 0), hChildMode));
        int yCur2 = yCur + Math.max(checkBox.getMeasuredHeight(), text.getMeasuredHeight());
        subText.measure(View.MeasureSpec.makeMeasureSpec(Math.max((w - xCur2) - getPaddingRight(), 0), wChildMode), View.MeasureSpec.makeMeasureSpec(Math.max((h - yCur2) - getPaddingBottom(), 0), hChildMode));
        int xCur3 = xCur2 + Math.max(text.getMeasuredWidth(), subText.getMeasuredWidth());
        int yCur3 = yCur2 + subText.getMeasuredHeight();
        int xCur4 = xCur3 + getPaddingRight();
        int yCur4 = yCur3 + getPaddingBottom();
        setMeasuredDimension(wMyMode == 0 ? xCur4 : Math.min(xCur4, w), hMyMode == 0 ? yCur4 : Math.min(yCur4, h));
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int lCheckbox = getPaddingLeft();
        int cCheckbox = getPaddingTop() + Math.max(checkBox.getMeasuredHeight() / 2, text.getMeasuredHeight() / 2);
        int tCheckbox = cCheckbox - (checkBox.getMeasuredWidth() / 2);
        this.checkBox.layout(lCheckbox, tCheckbox, checkBox.getMeasuredWidth() + lCheckbox, checkBox.getMeasuredHeight() + tCheckbox);
        int lText = lCheckbox + checkBox.getMeasuredWidth();
        int tText = cCheckbox - (text.getMeasuredHeight() / 2);
        this.text.layout(lText, tText, text.getMeasuredWidth() + lText, text.getMeasuredHeight() + tText);
        int lSubText = lText;
        int tSubText = tText + text.getMeasuredHeight();
        subText.layout(lSubText, tSubText, subText.getMeasuredWidth() + lSubText, subText.getMeasuredHeight() + tSubText);
    }
}
