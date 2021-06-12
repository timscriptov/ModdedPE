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
package com.mcal.mcpelauncher.ui.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.RatingBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.mcal.mcpelauncher.BuildConfig
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.data.Preferences

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object Dialogs {

    /**
     * Диалог с просьбой оценить приложение
     */
    @JvmStatic
    fun rate(context: Context) {
        val v = LayoutInflater.from(context).inflate(R.layout.rate, null)
        val ratingBar = v.findViewById<RatingBar>(R.id.rating_bar)
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(context.getString(R.string.rate))
        dialog.setView(v)
        dialog.setPositiveButton(android.R.string.ok) { dialog1: DialogInterface, _: Int ->
            if (ratingBar.rating > 3) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.mcal.mcpelauncher")
                    )
                )
                Preferences.rated
                dialog1.cancel()
            } else {
                Preferences.rated = true
                dialog1.cancel()
            }
        }
        dialog.setNegativeButton(android.R.string.cancel) { dialog1: DialogInterface, _: Int ->
            dialog1.cancel()
        }
        dialog.show()
    }

    /**
     * Диалог с просьбой предоставить дополнительное разрешение на Android 11
     */
    @RequiresApi(Build.VERSION_CODES.R)
    @JvmStatic
    fun showScopedStorageDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.scoped_storage_title)
            .setMessage(R.string.scoped_storage_msg)
            .setPositiveButton(R.string.settings_title) { p1, p2 ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
                context.startActivity(intent)
            }
            .create().show()
    }
}