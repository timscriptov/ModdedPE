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
package com.mcal.moddedpe3.data.model

import com.mcal.pesdk3.data.LocalNMod
import com.mcal.pesdk3.nmod.NMod

sealed class ImportResult {
    object Success : ImportResult()
    data class Error(val message: String) : ImportResult()
}

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    object Success : ImportState()
    object DeleteSuccess : ImportState()
    data class Error(val message: String) : ImportState()
}

data class ModsScreenState(
    val allMods: List<NMod> = emptyList(),
    val enabledMods: List<LocalNMod> = emptyList(),
    val disabledMods: List<LocalNMod> = emptyList(),
    val importState: ImportState = ImportState.Idle
) {
    fun hasMods(): Boolean = enabledMods.isNotEmpty() || disabledMods.isNotEmpty()

    fun getTotalModsCount(): Int = enabledMods.size + disabledMods.size
}
