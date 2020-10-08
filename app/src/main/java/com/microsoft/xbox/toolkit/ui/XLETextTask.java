package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.TextPaint;

import androidx.appcompat.widget.AppCompatImageView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLETextTask extends AsyncTask<XLETextArg, Void, Bitmap> {
    private static final String TAG = XLETextTask.class.getSimpleName();
    private final WeakReference<AppCompatImageView> img;
    private final int imgHeight;
    private final int imgWidth;

    public XLETextTask(AppCompatImageView img2) {
        img = new WeakReference<>(img2);
        imgWidth = img2.getWidth();
        imgHeight = img2.getHeight();
    }

    public Bitmap doInBackground(@NotNull XLETextArg... args) {
        Bitmap bm = null;
        if (args.length > 0) {
            XLETextArg arg = args[0];
            XLETextArg.Params params = arg.getParams();
            String msg = arg.getText();
            TextPaint p = new TextPaint();
            p.setTextSize(params.getTextSize());
            p.setAntiAlias(true);
            p.setColor(params.getColor());
            p.setTypeface(params.getTypeface());
            int width = Math.round(p.measureText(msg));
            int height = Math.round(p.descent() - p.ascent());
            int bmWidth = width;
            int bmHeight = height;
            if (params.isAdjustForImageSize()) {
                bmWidth = Math.max(width, imgWidth);
                bmHeight = Math.max(height, imgHeight);
            }
            if (params.hasTextAspectRatio()) {
                float ar = params.getTextAspectRatio().floatValue();
                if (ar > 0.0f) {
                    if (((float) bmHeight) > ((float) bmWidth) * ar) {
                        bmWidth = (int) (((float) bmHeight) / ar);
                    } else {
                        bmHeight = (int) (((float) bmWidth) * ar);
                    }
                }
            }
            bm = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);
            if (params.hasEraseColor()) {
                bm.eraseColor(params.getEraseColor());
            }
            new Canvas(bm).drawText(msg, (float) ((Math.max(0, bmWidth - width) / 2) + 0), (-p.ascent()) + ((float) (Math.max(0, bmHeight - height) / 2)), p);
        }
        return bm;
    }

    public void onPostExecute(Bitmap bm) {
        AppCompatImageView v = (AppCompatImageView) img.get();
        if (v != null) {
            v.setImageBitmap(bm);
        }
    }
}
