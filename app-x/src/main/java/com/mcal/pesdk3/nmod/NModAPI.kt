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
package com.mcal.pesdk3.nmod

import android.content.Context
import com.mcal.pesdk3.data.ExtractFailedException

class NModAPI(
    private val context: Context
) {
    private val nModManager = NModManager(context)
    private val extractor = NModExtractor(context)

    @Throws(ExtractFailedException::class)
    fun archiveZippedNMod(filePath: String): ZippedNMod {
        return extractor.archiveFromZipped(filePath)
    }

    fun initNModData() {
        nModManager.init()
    }

    fun getLoadedNMods(): ArrayList<NMod> {
        return nModManager.getAllNMods()
    }

    fun getImportedEnabledNMods(): ArrayList<NMod> {
        return nModManager.getEnabledNMods()
    }

    fun getImportedDisabledNMods(): ArrayList<NMod> {
        return nModManager.getDisabledNMods()
    }

    fun getImportedEnabledNModsHaveBanners(): ArrayList<NMod> {
        return nModManager.getEnabledNModsIsValidBanner()
    }

    fun findInstalledNMods(): ArrayList<NMod> {
        val extractor = NModExtractor(context)
        return extractor.archiveAllFromInstalled()
    }

    fun importNMod(nMod: NMod): Boolean {
        return nModManager.importNMod(nMod, false)
    }

    fun removeImportedNMod(nMod: NMod) {
        nModManager.removeImportedNMod(nMod)
    }

    fun setEnabled(nMod: NMod, enabled: Boolean) {
        if (enabled) {
            nModManager.setEnabled(nMod)
        } else {
            nModManager.setDisable(nMod)
        }
    }

    fun upPosNMod(nMod: NMod) {
        nModManager.makeUp(nMod)
    }

    fun downPosNMod(nMod: NMod) {
        nModManager.makeDown(nMod)
    }

    @Throws(ExtractFailedException::class)
    fun archivePackagedNMod(packageName: String): PackagedNMod {
        val extractor = NModExtractor(context)
        return extractor.archiveFromInstalledPackage(packageName)
    }

    fun getVersionName(): String {
        return "1.4.0"
    }
}
