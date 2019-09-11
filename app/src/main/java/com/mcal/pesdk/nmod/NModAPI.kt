/*
 * Copyright (C) 2018-2019 Тимашков Иван
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
package com.mcal.pesdk.nmod

import android.content.Context
import java.util.*

class NModAPI(private val mContext: Context) {
    private val mNModManager: NModManager
    private val mExtractor: NModExtractor

    val loadedNMods: ArrayList<NMod>
        get() = mNModManager.allNMods

    val importedEnabledNMods: ArrayList<NMod>
        get() = mNModManager.enabledNMods

    val importedDisabledNMods: ArrayList<NMod>
        get() = mNModManager.disabledNMods

    val importedEnabledNModsHaveBanners: ArrayList<NMod>
        get() = mNModManager.enabledNModsIsValidBanner

    val versionName: String
        get() = "1.4.1"

    init {
        this.mNModManager = NModManager(mContext)
        this.mExtractor = NModExtractor(mContext)
    }

    @Throws(ExtractFailedException::class)
    fun archiveZippedNMod(filePath: String): ZippedNMod {
        return mExtractor.archiveFromZipped(filePath)
    }

    fun initNModDatas() {
        mNModManager.init()
    }

    fun findInstalledNMods(): ArrayList<NMod> {
        val arvhiver = NModExtractor(mContext)
        return arvhiver.archiveAllFromInstalled()
    }

    fun importNMod(nmod: NMod): Boolean {
        return mNModManager.importNMod(nmod, false)
    }

    fun removeImportedNMod(nmod: NMod) {
        mNModManager.removeImportedNMod(nmod)
    }

    fun setEnabled(nmod: NMod, enabled: Boolean) {
        if (enabled)
            mNModManager.setEnabled(nmod)
        else
            mNModManager.setDisable(nmod)
    }

    fun upPosNMod(nmod: NMod) {
        mNModManager.makeUp(nmod)
    }

    fun downPosNMod(nmod: NMod) {
        mNModManager.makeDown(nmod)
    }

    @Throws(ExtractFailedException::class)
    fun archivePackagedNMod(packageName: String): PackagedNMod {
        val extractor = NModExtractor(mContext)
        return extractor.archiveFromInstalledPackage(packageName)
    }
}
