/*
 * Copyright (C) 2018-2021 Тимашков Иван
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
package com.mcal.moddedpe.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ProgressBar

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class ProgressBar : ProgressBar {
    private lateinit var mPaint: Paint
    private var mWidth = 0
    private var mHeight = 0
    private var mBlockDrawingProgress = 0f
    private var mShowedBlocks = 1
    private var mIsScaling = true

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#FF0050CB")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        mBlockDrawingProgress += if (mIsScaling) {
            mDefaultSpeed / 2
        } else {
            mDefaultSpeed
        }
        if (mBlockDrawingProgress >= 1 && !mIsScaling) {
            mBlockDrawingProgress = 0f
            ++mShowedBlocks
            if (mShowedBlocks > 4) {
                mShowedBlocks = 1
                mIsScaling = true
            }
        } else if (mBlockDrawingProgress >= 0.5 && mIsScaling) {
            mIsScaling = false
            mBlockDrawingProgress = 0f
            mShowedBlocks = 2
        }
        when (mShowedBlocks) {
            1 -> {
                val drawWidth = (mWidth.toFloat() * mBlockDrawingProgress).toInt()
                val drawHeight = (mHeight.toFloat() * mBlockDrawingProgress).toInt()
                canvas.drawRect(
                    0f,
                    drawHeight.toFloat(),
                    (mWidth - drawWidth).toFloat(),
                    mHeight.toFloat(),
                    mPaint
                )
            }

            2 -> {
                canvas.drawRect(
                    0f,
                    (mHeight shr 1).toFloat(),
                    (mWidth shr 1).toFloat(),
                    mHeight.toFloat(),
                    mPaint
                )
                val blockDrawHeight = (mHeight.toFloat() / 2 * mBlockDrawingProgress).toInt()
                canvas.drawRect(
                    (mWidth shr 1).toFloat(),
                    blockDrawHeight.toFloat(),
                    mWidth.toFloat(),
                    (blockDrawHeight + (mHeight shr 1)).toFloat(),
                    mPaint
                )
            }

            3 -> {
                canvas.drawRect(
                    0f,
                    (mHeight shr 1).toFloat(),
                    mWidth.toFloat(),
                    mHeight.toFloat(),
                    mPaint
                )
                val blockDrawHeight = (mHeight.toFloat() / 2 * mBlockDrawingProgress).toInt()
                canvas.drawRect(
                    0f,
                    0f,
                    (mWidth shr 1).toFloat(),
                    (blockDrawHeight + 1).toFloat(),
                    mPaint
                )
            }

            4 -> {
                canvas.drawRect(
                    0f,
                    (mHeight shr 1).toFloat(),
                    mWidth.toFloat(),
                    mHeight.toFloat(),
                    mPaint
                )
                canvas.drawRect(0f, 0f, (mWidth shr 1).toFloat(), (mHeight shr 1).toFloat(), mPaint)
                val blockDrawHeight = (mHeight.toFloat() / 2 * mBlockDrawingProgress).toInt()
                canvas.drawRect(
                    (mWidth shr 1).toFloat(),
                    0f,
                    mWidth.toFloat(),
                    (blockDrawHeight + 1).toFloat(),
                    mPaint
                )
            }
        }
        invalidate()
    }

    companion object {
        private const val mDefaultSpeed = 0.075f
    }
}