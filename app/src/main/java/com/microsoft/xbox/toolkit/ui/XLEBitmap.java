package com.microsoft.xbox.toolkit.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEBitmap {
    public static String ALLOCATION_TAG = "XLEBITMAP";
    private Bitmap bitmapSrc = null;

    private XLEBitmap(Bitmap src) {
        bitmapSrc = src;
    }

    public static XLEBitmap createBitmap(int width, int height, Bitmap.Config config) {
        return createBitmap(Bitmap.createBitmap(width, height, config));
    }

    public static XLEBitmap createScaledBitmap(@NotNull XLEBitmap src, int width, int height, boolean filtered) {
        return createBitmap(Bitmap.createScaledBitmap(src.bitmapSrc, width, height, filtered));
    }

    public static XLEBitmap decodeStream(InputStream stream, BitmapFactory.Options options) {
        return createBitmap(BitmapFactory.decodeStream(stream, (Rect) null, options));
    }

    public static XLEBitmap decodeStream(InputStream stream) {
        return createBitmap(BitmapFactory.decodeStream(stream));
    }

    public static XLEBitmap decodeResource(Resources res, int id) {
        return createBitmap(BitmapFactory.decodeResource(res, id));
    }

    public static XLEBitmap decodeResource(Resources res, int id, BitmapFactory.Options options) {
        return createBitmap(BitmapFactory.decodeResource(res, id, options));
    }

    public static XLEBitmap createScaledBitmap8888(@NotNull XLEBitmap src, int width, int height, boolean filtered) {
        return createBitmap(TextureResizer.createScaledBitmap8888(src.bitmapSrc, width, height, filtered));
    }

    public static XLEBitmap createBitmap(Bitmap bitmapSrc2) {
        if (bitmapSrc2 == null) {
            return null;
        }
        return new XLEBitmap(bitmapSrc2);
    }

    public void finalize() {
    }

    public int getByteCount() {
        return bitmapSrc.getRowBytes() * bitmapSrc.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmapSrc;
    }

    public XLEBitmapDrawable getDrawable() {
        return new XLEBitmapDrawable(new BitmapDrawable(bitmapSrc));
    }

    public static class XLEBitmapDrawable {
        private BitmapDrawable drawable;

        public XLEBitmapDrawable(BitmapDrawable drawable2) {
            drawable = drawable2;
        }

        public BitmapDrawable getDrawable() {
            return drawable;
        }
    }
}
