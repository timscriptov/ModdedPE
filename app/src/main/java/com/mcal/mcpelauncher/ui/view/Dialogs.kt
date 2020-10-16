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
package com.mcal.mcpelauncher.ui.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.data.Preferences

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object Dialogs {

    @JvmStatic
    fun rate(context: Context) {
        val v = LayoutInflater.from(context).inflate(R.layout.rate, null)
        val ratingBar = v.findViewById<RatingBar>(R.id.rating_bar)
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(context.getString(R.string.rate))
        dialog.setView(v)
        dialog.setPositiveButton(android.R.string.ok) { dialog1: DialogInterface, _: Int ->
            if (ratingBar.rating > 3) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mcal.mcpelauncher")))
                Preferences.getRated()
                dialog1.cancel()
            } else {
                Preferences.setRated(true)
                dialog1.cancel()
            }
        }
        dialog.setNegativeButton(android.R.string.cancel) { dialog1: DialogInterface, _: Int ->
            dialog1.cancel()
        }
        dialog.show()
    }
}