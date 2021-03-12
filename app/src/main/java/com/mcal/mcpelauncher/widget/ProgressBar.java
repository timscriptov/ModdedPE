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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class ProgressBar extends android.widget.ProgressBar {
    private static final float mDefaultSpeed = 0.075F;
    private final Paint mPaint;
    private int mWidth = 0;
    private int mHeight = 0;
    private float mBlockDrawingProgress = 0;
    private int mShowedBlocks = 1;
    private boolean mIsScaling = true;

    public ProgressBar(android.content.Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FF2C9EF4"));
    }

    public ProgressBar(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FF2C9EF4"));
    }

    public ProgressBar(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#FF2C9EF4"));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsScaling)
            mBlockDrawingProgress += (mDefaultSpeed / 2);
        else
            mBlockDrawingProgress += mDefaultSpeed;
        if (mBlockDrawingProgress >= 1 && !mIsScaling) {
            mBlockDrawingProgress = 0;
            ++mShowedBlocks;
            if (mShowedBlocks > 4) {
                mShowedBlocks = 1;
                mIsScaling = true;
            }
        } else if (mBlockDrawingProgress >= 0.5 && mIsScaling) {
            mIsScaling = false;
            mBlockDrawingProgress = 0;
            mShowedBlocks = 2;
        }

        switch (mShowedBlocks) {
            case 1: {
                int drawWidth = (int) (((float) mWidth) * mBlockDrawingProgress);
                int drawHeight = (int) (((float) mHeight) * mBlockDrawingProgress);
                canvas.drawRect(0, drawHeight, mWidth - drawWidth, mHeight, mPaint);
                break;
            }
            case 2: {
                canvas.drawRect(0, mHeight >> 1, mWidth >> 1, mHeight, mPaint);
                int blockDrawHeight = (int) (((float) mHeight / 2) * mBlockDrawingProgress);
                canvas.drawRect(mWidth >> 1, blockDrawHeight, mWidth, blockDrawHeight + (mHeight >> 1), mPaint);
                break;
            }
            case 3: {
                canvas.drawRect(0, mHeight >> 1, mWidth, mHeight, mPaint);
                int blockDrawHeight = (int) (((float) mHeight / 2) * mBlockDrawingProgress);
                canvas.drawRect(0, 0, mWidth >> 1, blockDrawHeight + 1, mPaint);
                break;
            }
            case 4: {
                canvas.drawRect(0, mHeight >> 1, mWidth, mHeight, mPaint);
                canvas.drawRect(0, 0, mWidth >> 1, mHeight >> 1, mPaint);
                int blockDrawHeight = (int) (((float) mHeight / 2) * mBlockDrawingProgress);
                canvas.drawRect(mWidth >> 1, 0, mWidth, blockDrawHeight + 1, mPaint);
                break;
            }
        }
        invalidate();
    }
}
