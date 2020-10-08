package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.microsoft.xbox.toolkit.XLERValueHelper;

import java.net.URI;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEImageViewFast extends XLEImageView {
    protected int pendingBitmapResourceId = -1;
    protected URI pendingUri = null;
    private TextureBindingOption option;
    private String pendingFilePath = null;
    private boolean useFileCache = true;

    public XLEImageViewFast(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEImageViewFast(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("XLEImageViewFast"));
            setImageResource(a.getResourceId(XLERValueHelper.getStyleableRValue("XLEImageViewFast_src"), -1));
            a.recycle();
            setSoundEffectsEnabled(false);
        }
    }

    public void setImageResource(int resourceId) {
        if (hasSize()) {
            bindToResourceId(resourceId);
        } else {
            pendingBitmapResourceId = resourceId;
        }
    }

    public void setImageURI2(URI uri) {
        if (hasSize()) {
            bindToUri(uri);
        } else {
            pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, boolean useFilaCache) {
        useFileCache = useFilaCache;
        option = new TextureBindingOption(getWidth(), getHeight(), useFileCache);
        if (hasSize()) {
            bindToUri(uri, option);
        } else {
            pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, int loadingResourceId, int errorResourceId) {
        option = new TextureBindingOption(getWidth(), getHeight(), loadingResourceId, errorResourceId, useFileCache);
        if (hasSize()) {
            bindToUri(uri, option);
        } else {
            pendingUri = uri;
        }
    }

    public void setImageFilePath(String filePath) {
        if (hasSize()) {
            bindToFilePath(filePath);
        } else {
            pendingFilePath = filePath;
        }
    }

    public void setImageURI(Uri uri) {
        throw new UnsupportedOperationException();
    }

    public boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    private void bindToResourceId(int resourceId) {
        this.pendingBitmapResourceId = -1;
        TextureManager.Instance().bindToView(resourceId, (AppCompatImageView) this, getWidth(), getHeight());
    }

    public void bindToUri(URI uri) {
        pendingUri = null;
        bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), useFileCache));
    }

    private void bindToUri(URI uri, TextureBindingOption option2) {
        pendingUri = null;
        option = null;
        TextureManager.Instance().bindToView(uri, this, option2);
    }

    private void bindToFilePath(String filePath) {
        pendingFilePath = null;
        TextureManager.Instance().bindToViewFromFile(filePath, this, getWidth(), getHeight());
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(0, widthMeasureSpec), resolveSize(0, heightMeasureSpec));
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasSize()) {
            if (pendingBitmapResourceId >= 0) {
                bindToResourceId(pendingBitmapResourceId);
            }
            if (pendingUri != null || (pendingUri == null && option != null)) {
                if (option != null) {
                    bindToUri(pendingUri, new TextureBindingOption(getWidth(), getHeight(), option.resourceIdForLoading, option.resourceIdForError, option.useFileCache));
                } else {
                    bindToUri(pendingUri);
                }
            }
            if (pendingFilePath != null) {
                bindToFilePath(pendingFilePath);
            }
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}