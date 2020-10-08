package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureResizer {
    @NotNull
    public static Bitmap createScaledBitmap8888(@NotNull Bitmap source, int dstwidth, int dstheight, boolean filter) {
        Paint paint;
        Bitmap bitmap;
        int width = source.getWidth();
        int height = source.getHeight();
        Matrix m = new Matrix();
        m.setScale(((float) dstwidth) / ((float) width), ((float) dstheight) / ((float) height));
        if (0 + width > source.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        } else if (0 + height > source.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        } else if (!source.isMutable() && 0 == 0 && 0 == 0 && width == source.getWidth() && height == source.getHeight() && (m == null || m.isIdentity())) {
            return source;
        } else {
            int neww = width;
            int newh = height;
            Canvas canvas = new Canvas();
            Rect srcR = new Rect(0, 0, 0 + width, 0 + height);
            RectF dstR = new RectF(0.0f, 0.0f, (float) width, (float) height);
            if (m == null || m.isIdentity()) {
                bitmap = Bitmap.createBitmap(neww, newh, Bitmap.Config.ARGB_8888);
                paint = null;
            } else {
                boolean hasAlpha = source.hasAlpha() || !m.rectStaysRect();
                RectF deviceR = new RectF();
                m.mapRect(deviceR, dstR);
                bitmap = Bitmap.createBitmap(Math.round(deviceR.width()), Math.round(deviceR.height()), Bitmap.Config.ARGB_8888);
                if (hasAlpha) {
                    bitmap.eraseColor(0);
                }
                canvas.translate(-deviceR.left, -deviceR.top);
                canvas.concat(m);
                paint = new Paint();
                paint.setFilterBitmap(filter);
                if (!m.rectStaysRect()) {
                    paint.setAntiAlias(true);
                }
            }
            bitmap.setDensity(source.getDensity());
            canvas.setBitmap(bitmap);
            canvas.drawBitmap(source, srcR, dstR, paint);
            return bitmap;
        }
    }
}
