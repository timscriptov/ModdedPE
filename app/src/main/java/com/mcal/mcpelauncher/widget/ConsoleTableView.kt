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
package com.mcal.mcpelauncher.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout

import androidx.appcompat.widget.AppCompatTextView

import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.R

@SuppressLint("InflateParams", "ClickableViewAccessibility")
class ConsoleTableView : RelativeLayout {
    constructor(c: Context) : super(c) {
        addTableView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        addTableView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        addTableView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        addTableView()
    }

    private fun addTableView() {
        val rootView = LayoutInflater.from(context).inflate(R.layout.moddedpe_main_console_table, null)

        (rootView.findViewById<View>(R.id.moddedpe_main_text_view_app_version) as AppCompatTextView).text = context.resources.getString(R.string.app_version)
        (rootView.findViewById<View>(R.id.moddedpe_main_text_view_target_mc_version) as AppCompatTextView).setTextColor(if (ModdedPEApplication.mPESdk.minecraftInfo.isSupportedMinecraftVersion(context.resources.getStringArray(R.array.target_mcpe_versions))) Color.GREEN else Color.RED)
        (rootView.findViewById<View>(R.id.moddedpe_main_text_view_target_mc_version) as AppCompatTextView).setText(R.string.target_mcpe_version_info)
        addView(rootView)
    }
}
