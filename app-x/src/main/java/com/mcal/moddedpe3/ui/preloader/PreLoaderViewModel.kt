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
package com.mcal.moddedpe3.ui.preloader

import android.app.Activity
import android.content.Intent
import cafe.adriel.voyager.core.model.ScreenModel
import com.mcal.moddedpe3.MinecraftActivity
import com.mcal.moddedpe3.data.model.PreLoaderScreenState
import com.mcal.moddedpe3.data.repository.MainRepository
import com.mcal.moddedpe3.data.repository.MainRepositoryImpl.Companion.MINECRAFT_LIBS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PreLoaderViewModel(
    private val mainRepository: MainRepository
) : ScreenModel {
    private val _state = MutableStateFlow(PreLoaderScreenState())
    val state = _state.asStateFlow()

    fun initializePreLoader(activity: Activity) {
        addLog("Инициализация системы...")
        updateProgress(0.1f, "Проверка зависимостей")

        MINECRAFT_LIBS.forEachIndexed { index, library ->
            addLog("Загрузка $library")
            updateProgress(0.2f + (index * 0.15f), "Загрузка $library")
            mainRepository.loadNativeLibrary(library)
        }

        addLog("Все библиотеки успешно загружены")
        updateProgress(1.0f, "Готово")
        launchGame(activity)
    }

    private fun addLog(message: String) {
        _state.update { currentState ->
            currentState.copy(
                logs = currentState.logs + message
            )
        }
    }

    private fun updateProgress(progress: Float, status: String) {
        _state.update { currentState ->
            currentState.copy(
                progress = progress,
                currentStatus = status
            )
        }
    }

    private fun launchGame(activity: Activity) {
        val intent = Intent(activity, MinecraftActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        activity.startActivity(intent)
        activity.finish()
    }
}