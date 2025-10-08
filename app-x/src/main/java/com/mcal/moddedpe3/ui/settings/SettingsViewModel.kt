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
package com.mcal.moddedpe3.ui.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.moddedpe3.data.model.SettingsScreenState
import com.mcal.moddedpe3.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ScreenModel {
    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    private val _exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        screenModelScope.launch(_exceptionHandler) {
            settingsRepository.loadSettings()?.let { savedSettings ->
                _state.value = savedSettings
            }
        }
    }

    fun setSafeMode(enabled: Boolean) {
        _state.value = _state.value.copy(isSafeMode = enabled)
        saveSettings()
    }

    fun setMinecraftPackageName(packageName: String) {
        _state.value = _state.value.copy(minecraftPackageName = packageName)
        saveSettings()
    }

    private fun saveSettings() {
        screenModelScope.launch(_exceptionHandler) {
            settingsRepository.saveSettings(_state.value)
        }
    }
}