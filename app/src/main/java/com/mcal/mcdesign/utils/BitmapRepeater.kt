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
package com.mcal.mcdesign.utils

import android.graphics.Bitmap
import android.graphics.Canvas

//##################################################################

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object BitmapRepeater
//##################################################################
{
    private fun repeatW(width: Int, src: Bitmap): Bitmap {
        val count = (width + src.width - 1) / src.width + 1
        val bitmap = Bitmap.createBitmap(width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        for (idx in 0 until count) {
            if (idx + 1 == count)
                canvas.drawBitmap(src, width.toFloat(), 0f, null)
            else
                canvas.drawBitmap(src, (idx * src.width).toFloat(), 0f, null)
        }
        return bitmap
    }

    private fun repeatH(height: Int, src: Bitmap): Bitmap {
        val count = (height + src.height - 1) / src.height + 1
        val bitmap = Bitmap.createBitmap(src.width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        for (idx in 0 until count) {
            if (idx + 1 == count)
                canvas.drawBitmap(src, 0f, height.toFloat(), null)
            else
                canvas.drawBitmap(src, 0f, (idx * src.height).toFloat(), null)
        }
        return bitmap
    }

    fun repeat(width: Int, height: Int, src: Bitmap): Bitmap {
        var ret = repeatW(width, src)
        ret = repeatH(height, ret)
        return ret
    }
}
