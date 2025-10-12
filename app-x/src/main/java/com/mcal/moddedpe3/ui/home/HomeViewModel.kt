/*
 * Copyright (C) 2018-2025 Тимашков Иван
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
package com.mcal.moddedpe3.ui.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import cafe.adriel.voyager.core.model.ScreenModel
import com.mcal.moddedpe3.data.model.HomeScreenState
import com.mcal.moddedpe3.data.repository.MainRepository
import com.mcal.moddedpe3.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val mainRepository: MainRepository,
    private val settingsRepository: SettingsRepository,
) : ScreenModel {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    init {
        val packageInfo = mainRepository.findMinecraftPackage()
        _state.value = _state.value.copy(
            isInstalled = packageInfo != null,
            packageInfo = packageInfo,
            name = mainRepository.getMinecraftLabel(packageInfo),
            versionName = mainRepository.getMinecraftVersionName(packageInfo),
            versionCode = mainRepository.getMinecraftVersionCode(packageInfo),
            iconBitmap = mainRepository.getMinecraftIconBitmap()
        )
    }

    fun installGame(activity: Activity) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=${settingsRepository.getMinecraftPackageName()}".toUri()
                setPackage("com.android.vending")
            }

            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            } else {
                installFromBrowser(activity)
            }
        } catch (e: Exception) {
            Log.e("installGame", "Failed to open Google Play", e)
            try {
                installFromBrowser(activity)
            } catch (e2: Exception) {
                Log.e("installGame", "Failed to open browser", e2)
                Toast.makeText(activity, "Не удалось открыть Google Play", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun installFromBrowser(activity: Activity) {
        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            data =
                "https://play.google.com/store/apps/details?id=${settingsRepository.getMinecraftPackageName()}".toUri()
        }
        activity.startActivity(webIntent)
    }
}
