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
package com.mcal.mcpelauncher.view

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.mcal.mcpelauncher.R

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class CenteredToolBar : Toolbar {
    private var centeredTitleTextView: AppCompatTextView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context, attrs
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    override fun setTitleTextColor(color: Int) {
        getCenteredTitleTextView().setTextColor(color)
    }

    override fun getTitle(): CharSequence {
        return getCenteredTitleTextView().text.toString()
    }

    override fun setTitle(@StringRes resId: Int) {
        val s = resources.getString(resId)
        title = s
    }

    override fun setTitle(title: CharSequence) {
        getCenteredTitleTextView().text = title
    }

    fun setTypeface(font: Typeface?) {
        getCenteredTitleTextView().typeface = font
    }

    private fun getCenteredTitleTextView(): AppCompatTextView {
        centeredTitleTextView?.let {
            return it
        } ?: run {
            return AppCompatTextView(context).apply {
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                gravity = Gravity.CENTER
                setTextAppearance(
                    context,
                    androidx.appcompat.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title
                )
                layoutParams =
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                        gravity = Gravity.CENTER
                    }
            }.also {
                centeredTitleTextView = it
                addView(centeredTitleTextView)
            }
        }
    }
}