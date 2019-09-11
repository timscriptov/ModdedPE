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

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.RelativeLayout
import com.mcal.mcdesign.utils.BitmapRepeater
import com.mcal.mcpelauncher.R

//##################################################################

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class MCDActionBarView : RelativeLayout
//##################################################################
{
    constructor(context: android.content.Context) : super(context) {}

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {}

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.mcd_header_bg)
        background = BitmapDrawable(resources, BitmapRepeater.repeat(w, h, bitmap))
    }
}
