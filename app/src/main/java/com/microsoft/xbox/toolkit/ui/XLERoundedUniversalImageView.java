package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLERoundedUniversalImageView extends XLEUniversalImageView {
    public XLERoundedUniversalImageView(Context context) {
        super(context, new XLEUniversalImageView.Params());
    }

    public XLERoundedUniversalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XLERoundedUniversalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static Bitmap getRoundedCroppedBitmap(@NotNull Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() == radius && bitmap.getHeight() == radius) {
            finalBitmap = bitmap;
        } else {
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        }
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(((float) (finalBitmap.getWidth() / 2)) + 0.7f, ((float) (finalBitmap.getHeight() / 2)) + 0.7f, ((float) (finalBitmap.getWidth() / 2)) + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);
        return output;
    }

    public void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable != null && getWidth() != 0 && getHeight() != 0) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
            int w = getWidth();
            int height = getHeight();
            canvas.drawBitmap(getRoundedCroppedBitmap(bitmap, w), 0.0f, 0.0f, (Paint) null);
        }
    }
}
