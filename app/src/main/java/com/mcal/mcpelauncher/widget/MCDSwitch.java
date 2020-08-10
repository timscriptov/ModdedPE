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
package com.mcal.mcpelauncher.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.Switch;

import com.mcal.mcpelauncher.R;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDSwitch extends Switch {
    Bitmap bitmap;
    Bitmap bitmapClicked;
    Bitmap bitmapNI;

    public MCDSwitch(android.content.Context context) {
        super(context);
    }

    public MCDSwitch(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public MCDSwitch(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MCDSwitch(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null || bitmapClicked == null) {
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_switch_default);
            bitmapClicked = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_switch_checked);
            bitmapNI = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_switch_not_important);

        }
        if (!super.isClickable())
            canvas.drawBitmap(bitmapNI, 0, 0, null);
        else if (super.isChecked())
            canvas.drawBitmap(bitmapClicked, 0, 0, null);
        else
            canvas.drawBitmap(bitmap, 0, 0, null);
        invalidate();
    }
}