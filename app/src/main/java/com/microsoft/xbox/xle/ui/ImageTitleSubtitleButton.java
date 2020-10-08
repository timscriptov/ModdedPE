package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEUniversalImageView;
import com.microsoft.xbox.xle.app.XLEUtil;

import java.net.URI;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ImageTitleSubtitleButton extends LinearLayout {
    private XLEUniversalImageView iconImageView;
    private CustomTypefaceTextView subtitleTextView;
    private CustomTypefaceTextView titleTextView;

    public ImageTitleSubtitleButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public ImageTitleSubtitleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTitleSubtitleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.image_title_subtitle_button, this, true);
        iconImageView = findViewById(R.id.image_title_subtitle_button_image);
        titleTextView = findViewById(R.id.image_title_subtitle_button_title);
        subtitleTextView = findViewById(R.id.image_title_subtitle_button_subtitle);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("ImageTitleSubtitleButton"));
        String iconUri = a.getString(XLERValueHelper.getStyleableRValue("ImageTitleSubtitleButton_image_uri"));
        String title = a.getString(XLERValueHelper.getStyleableRValue("ImageTitleSubtitleButton_text_title"));
        String subtitle = a.getString(XLERValueHelper.getStyleableRValue("ImageTitleSubtitleButton_text_subtitle"));
        a.recycle();
        setImageUri(iconUri);
        XLEUtil.updateTextAndVisibilityIfNotNull(titleTextView, title, 0);
        XLEUtil.updateTextAndVisibilityIfNotNull(subtitleTextView, subtitle, 0);
        setFocusable(true);
    }

    public void setImageUri(String iconUri) {
        if (!JavaUtil.isNullOrEmpty(iconUri)) {
            iconImageView.setImageURI2(URI.create(iconUri));
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClickable(true);
        info.setClassName(AppCompatButton.class.getName());
    }
}
