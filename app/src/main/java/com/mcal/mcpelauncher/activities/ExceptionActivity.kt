/*
 * Copyright (C) 2018-2020 Тимашков Иван
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
package com.mcal.mcpelauncher.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

class ExceptionActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val mLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
            gravity = 17
        }
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = mLayoutParams
        }
        val sv = ScrollView(this)
        linearLayout.addView(sv)
        val error = AppCompatTextView(this)
        sv.addView(error)
        setContentView(linearLayout)
        error.text = intent.getStringExtra("error")
    }
}