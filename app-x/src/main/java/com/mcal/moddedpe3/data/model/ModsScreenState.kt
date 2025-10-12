package com.mcal.moddedpe3.data.model

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
    val enabledMods: List<NMod> = emptyList(),
    val disabledMods: List<NMod> = emptyList(),
    val importState: ImportState = ImportState.Idle
) {
    fun getTotalModsCount(): Int {
        return enabledMods.size + disabledMods.size
    }

    fun hasMods(): Boolean {
        return enabledMods.isNotEmpty() || disabledMods.isNotEmpty()
    }
}
