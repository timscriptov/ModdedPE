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
import android.content.Context
import android.content.Intent
import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import com.mcal.moddedpe3.MinecraftActivity
import com.mcal.moddedpe3.data.model.HomeScreenState
import com.mcal.moddedpe3.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val context: Context,
    private val mainRepository: MainRepository
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
        )
    }

    fun launchGame(activity: Activity?) {
        // Загружаем библиотеки и проверяем успешность
        val librariesLoaded = mainRepository.loadNativeLibraries()

        if (librariesLoaded) {
            // Добавляем ассеты перед запуском
            try {
                val assetManager = context.assets
                mainRepository.addAssetOverrides(assetManager)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error adding asset overrides", e)
            }

            val intent = Intent(activity, MinecraftActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
            activity?.startActivity(intent)
            activity?.finish()
        } else {
            // Показать ошибку пользователю
            Log.e("HomeViewModel", "Failed to load native libraries, cannot launch game")
            // Здесь можно добавить показ Toast или Snackbar с ошибкой
        }
    }
}