package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 07.01.2021
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */

public interface OnBitmapSetListener {
    void onAfterImageSet(ImageView imageView, Bitmap bitmap);

    void onBeforeImageSet(ImageView imageView, Bitmap bitmap);
}
