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
package com.mcal.mcdesign.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RelativeLayout;

import com.mcal.mcdesign.utils.BitmapRepeater;
import com.mcal.mcpelauncher.R;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDActionBarView extends RelativeLayout {
    public MCDActionBarView(android.content.Context context) {
        super(context);
    }

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_header_bg);
        setBackgroundDrawable(new BitmapDrawable(BitmapRepeater.repeat(w, h, bitmap)));
    }
}
