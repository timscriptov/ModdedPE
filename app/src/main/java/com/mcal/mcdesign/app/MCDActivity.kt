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
package com.mcal.mcdesign.app

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.mcal.mcdesign.utils.BitmapRepeater
import com.mcal.mcpelauncher.R

open class MCDActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultActionBar()
    }

    protected open fun setDefaultActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            val actionBarCustomView = LayoutInflater.from(this).inflate(R.layout.mcd_actionbar, null) as RelativeLayout
            val layoutParams = ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            actionBar.setDisplayShowHomeEnabled(false)
            actionBar.setDisplayShowCustomEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setCustomView(actionBarCustomView, layoutParams)
            val parent = actionBarCustomView.parent as Toolbar
            parent.setContentInsetsAbsolute(0, 0)

            val titleTV = actionBarCustomView.findViewById<AppCompatTextView>(R.id.mcd_actionbar_title)
            titleTV.text = title
        }
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)

        if (supportActionBar != null) {
            val actionBarCustomView = supportActionBar!!.customView
            val titleTV = actionBarCustomView.findViewById<AppCompatTextView>(R.id.mcd_actionbar_title)
            titleTV.setText(titleId)
        }
    }

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)

        if (supportActionBar != null) {
            val actionBarCustomView = supportActionBar!!.customView
            val titleTV = actionBarCustomView.findViewById<AppCompatTextView>(R.id.mcd_actionbar_title)
            titleTV.text = title
        }
    }

    protected fun setActionBarViewRight(view: View) {
        if (supportActionBar != null) {
            val actionBarCustomView = supportActionBar!!.customView
            val layout = actionBarCustomView.findViewById<RelativeLayout>(R.id.mcd_actionbar_ViewRight)
            layout.removeAllViews()
            layout.addView(view)
        }
    }

    protected fun setActionBarViewLeft(view: View) {
        if (supportActionBar != null) {
            val actionBarCustomView = supportActionBar!!.customView
            val layout = actionBarCustomView.findViewById<RelativeLayout>(R.id.mcd_actionbar_ViewLeft)
            layout.removeAllViews()
            layout.addView(view)
        }
    }

    protected fun setActionBarButtonCloseRight() {
        val buttonClose = layoutInflater.inflate(R.layout.moddedpe_ui_button_close, null)
        buttonClose.findViewById<View>(R.id.moddedpe_ui_button_item_image_button).setOnClickListener { finish() }
        setActionBarViewRight(buttonClose)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.mcd_bg)
        bitmap = BitmapRepeater.repeat(windowManager.defaultDisplay.width, windowManager.defaultDisplay.height, bitmap)
        window.decorView.background = BitmapDrawable(resources, bitmap)
    }
}
