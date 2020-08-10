/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcpelauncher.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class BitmapRepeater {
    private static Bitmap repeatW(int width, @NotNull Bitmap src) {
        int count = (width + src.getWidth() - 1) / src.getWidth() + 1;
        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            if (idx + 1 == count)
                canvas.drawBitmap(src, width, 0, null);
            else
                canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    private static Bitmap repeatH(int height, @NotNull Bitmap src) {
        int count = (height + src.getHeight() - 1) / src.getHeight() + 1;
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            if (idx + 1 == count)
                canvas.drawBitmap(src, 0, height, null);
            else
                canvas.drawBitmap(src, 0, idx * src.getHeight(), null);
        }
        return bitmap;
    }

    public static Bitmap repeat(int width, int height, Bitmap src) {
        Bitmap ret = repeatW(width, src);
        ret = repeatH(height, ret);
        return ret;
    }
}
