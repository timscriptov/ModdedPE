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
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.mcal.mcpelauncher.BuildConfig
import com.mcal.mcpelauncher.R

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object Dialogs {

    /**
     * Диалог с просьбой предоставить дополнительное разрешение на Android 11
     */
    @RequiresApi(Build.VERSION_CODES.R)
    @JvmStatic
    fun showScopedStorageDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.scoped_storage_title)
            .setMessage(R.string.scoped_storage_msg)
            .setPositiveButton(R.string.settings_title) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
                context.startActivity(intent)
            }
            .create().show()
    }
}