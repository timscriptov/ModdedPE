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
import java.io.File

class NModManager(
    private val context: Context
) {
    private var enabledNMods = ArrayList<NMod>()
    private var allNMods = ArrayList<NMod>()
    private var disabledNMods = ArrayList<NMod>()

    init {
        val dataLoader = NModDataLoader(context)

        dataLoader.getAllList().forEach { item ->
            if (!PackageNameChecker.isValidPackageName(item)) {
                dataLoader.removeByName(item)
            }
        }

        forEachItemToAddNMod(dataLoader.getEnabledList(), true)
        forEachItemToAddNMod(dataLoader.getDisabledList(), false)
        refreshData()
    }

    fun getEnabledNMods(): ArrayList<NMod> {
        return enabledNMods
    }

    fun getDisabledNMods(): ArrayList<NMod> {
        return disabledNMods
    }

    fun getEnabledNModsIsValidBanner(): ArrayList<NMod> {
        return getEnabledNMods().filter { it.isValidBanner() }.toCollection(ArrayList())
    }

    fun getAllNMods(): ArrayList<NMod> = allNMods

    fun removeImportedNMod(nMod: NMod) {
        getEnabledNMods().remove(nMod)
        getDisabledNMods().remove(nMod)
        getAllNMods().remove(nMod)
        val dataLoader = NModDataLoader(context)
        dataLoader.removeByName(nMod.getPackageName())
        if (nMod.getNModType() == NMod.NMOD_TYPE_ZIPPED) {
            val zippedNModPath =
                NModFilePathManager(context).getNModsDir().toString() + File.separator + nMod.getPackageName()
            val file = File(zippedNModPath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun forEachItemToAddNMod(list: ArrayList<String>, enabled: Boolean) {
        for (packageName in list) {
            try {
                val zippedNModPath =
                    NModFilePathManager(context).getNModsDir().toString() + File.separator + packageName
                val zippedNMod = ZippedNMod(packageName, context, File(zippedNModPath))
                importNMod(zippedNMod, enabled)
                continue
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val extractor = NModExtractor(context)
                val packagedNMod = extractor.archiveFromInstalledPackage(packageName)
                importNMod(packagedNMod, enabled)
            } catch (e: ExtractFailedException) {
                e.printStackTrace()
            }
        }
    }

    fun importNMod(newNMod: NMod, enabled: Boolean): Boolean {
        var replaced = false
        for (nMod in getAllNMods()) {
            if (nMod == newNMod) {
                getEnabledNMods().remove(nMod)
                getDisabledNMods().remove(nMod)
                replaced = true
            }
        }

        getAllNMods().add(newNMod)
        if (enabled) {
            setEnabled(newNMod)
        } else {
            setDisable(newNMod)
        }
        return replaced
    }

    private fun refreshData() {
        val dataLoader = NModDataLoader(context)
        dataLoader.getAllList().forEach { item ->
            if (getImportedNMod(item) == null) {
                dataLoader.removeByName(item)
            }
        }
    }

    private fun getImportedNMod(pkgName: String): NMod? {
        return getAllNMods().find { it.getPackageName() == pkgName }
    }

    fun makeUp(nMod: NMod) {
        val dataLoader = NModDataLoader(context)
        dataLoader.upNMod(nMod)
        refreshEnabledOrderList()
    }

    fun makeDown(nMod: NMod) {
        val dataLoader = NModDataLoader(context)
        dataLoader.downNMod(nMod)
        refreshEnabledOrderList()
    }

    private fun refreshEnabledOrderList() {
        val dataLoader = NModDataLoader(context)
        val enabledList = dataLoader.getEnabledList()
        getEnabledNMods().clear()
        for (pkgName in enabledList) {
            getImportedNMod(pkgName)?.let { nMod ->
                getEnabledNMods().add(nMod)
            }
        }
    }

    fun setEnabled(nMod: NMod) {
        if (nMod.isBugPack()) {
            return
        }
        val dataLoader = NModDataLoader(context)
        dataLoader.setIsEnabled(nMod, true)
        getEnabledNMods().add(nMod)
        getDisabledNMods().remove(nMod)
    }

    fun setDisable(nMod: NMod) {
        val dataLoader = NModDataLoader(context)
        dataLoader.setIsEnabled(nMod, false)
        getDisabledNMods().add(nMod)
        getEnabledNMods().remove(nMod)
    }
}
