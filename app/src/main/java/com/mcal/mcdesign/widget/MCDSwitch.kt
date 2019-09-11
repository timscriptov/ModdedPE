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
package com.mcal.mcdesign.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.widget.Switch

import com.mcal.mcpelauncher.R

//##################################################################

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class MCDSwitch : Switch
//##################################################################
{
    internal var bitmap: Bitmap? = null
    internal var bitmapClicked: Bitmap? = null
    internal lateinit var bitmapNI: Bitmap

    constructor(context: android.content.Context) : super(context) {}

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {}

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun onDraw(canvas: Canvas) {
        if (bitmap == null || bitmapClicked == null) {
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_switch_default)
            bitmapClicked = BitmapFactory.decodeResource(context.resources, R.drawable.ic_switch_checked)
            bitmapNI = BitmapFactory.decodeResource(context.resources, R.drawable.ic_switch_not_important)

        }
        if (!super.isClickable())
            canvas.drawBitmap(bitmapNI, 0f, 0f, null)
        else if (super.isChecked())
            canvas.drawBitmap(bitmapClicked!!, 0f, 0f, null)
        else
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        invalidate()
    }


}
