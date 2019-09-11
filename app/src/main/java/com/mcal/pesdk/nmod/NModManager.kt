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
import java.io.File
import java.io.IOException
import java.util.*

internal class NModManager(private val mContext: Context) {
    var enabledNMods = ArrayList<NMod>()
        private set
    var allNMods = ArrayList<NMod>()
        private set
    var disabledNMods = ArrayList<NMod>()
        private set

    val enabledNModsIsValidBanner: ArrayList<NMod>
        get() {
            val ret = ArrayList<NMod>()
            for (nmod in enabledNMods) {
                if (nmod.isValidBanner)
                    ret.add(nmod)
            }
            return ret
        }

    fun init() {
        allNMods = ArrayList()
        enabledNMods = ArrayList()
        disabledNMods = ArrayList()

        val dataloader = NModDataLoader(mContext)

        for (item in dataloader.allList) {
            if (!PackageNameChecker.isValidPackageName(item)) {
                dataloader.removeByName(item)
            }
        }

        forEachItemToAddNMod(dataloader.enabledList, true)
        forEachItemToAddNMod(dataloader.disabledList, false)
        refreshDatas()
    }

    fun removeImportedNMod(nmod: NMod) {
        enabledNMods.remove(nmod)
        disabledNMods.remove(nmod)
        allNMods.remove(nmod)
        val dataloader = NModDataLoader(mContext)
        dataloader.removeByName(nmod.packageName)
        if (nmod.nModType == NMod.NMOD_TYPE_ZIPPED) {
            val zippedNModPath = NModFilePathManager(mContext).nModsDir.toString() + File.separator + nmod.packageName
            val file = File(zippedNModPath)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun forEachItemToAddNMod(list: ArrayList<String>, enabled: Boolean) {
        for (packageName in list) {
            try {
                val zippedNModPath = NModFilePathManager(mContext).nModsDir.toString() + File.separator + packageName
                val zippedNMod = ZippedNMod(packageName, mContext, File(zippedNModPath))
                importNMod(zippedNMod, enabled)
                continue
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                val extractor = NModExtractor(mContext)
                val packagedNMod = extractor.archiveFromInstalledPackage(packageName)
                importNMod(packagedNMod, enabled)
            } catch (e: ExtractFailedException) {
                e.printStackTrace()
            }

        }
    }

    fun importNMod(newNMod: NMod, enabled: Boolean): Boolean {
        var replaced = false
        val iterator = allNMods.iterator()
        while (iterator.hasNext()) {
            val nmod = iterator.next()
            if (nmod == newNMod) {
                //iterator.remove();
                enabledNMods.remove(nmod)
                disabledNMods.remove(nmod)
                replaced = true
            }
        }

        allNMods.add(newNMod)
        if (enabled)
            setEnabled(newNMod)
        else
            setDisable(newNMod)
        return replaced
    }


    private fun refreshDatas() {
        val dataloader = NModDataLoader(mContext)

        for (item in dataloader.allList) {
            if (getImportedNMod(item) == null) {
                dataloader.removeByName(item)
            }
        }
    }

    private fun getImportedNMod(pkgname: String): NMod? {
        for (nmod in allNMods)
            if (nmod.packageName == pkgname)
                return nmod
        return null
    }

    fun makeUp(nmod: NMod) {
        val dataloader = NModDataLoader(mContext)
        dataloader.upNMod(nmod)
        refreshEnabledOrderList()
    }

    fun makeDown(nmod: NMod) {
        val dataloader = NModDataLoader(mContext)
        dataloader.downNMod(nmod)
        refreshEnabledOrderList()
    }

    private fun refreshEnabledOrderList() {
        val dataloader = NModDataLoader(mContext)
        val enabledList = dataloader.enabledList
        enabledNMods.clear()
        for (pkgName in enabledList) {
            val nmod = getImportedNMod(pkgName)
            if (nmod != null) {
                enabledNMods.add(nmod)
            }
        }
    }

    fun setEnabled(nmod: NMod) {
        if (nmod.isBugPack)
            return
        val dataloader = NModDataLoader(mContext)
        dataloader.setIsEnabled(nmod, true)
        enabledNMods.add(nmod)
        disabledNMods.remove(nmod)
    }

    fun setDisable(nmod: NMod) {
        val dataloader = NModDataLoader(mContext)
        dataloader.setIsEnabled(nmod, false)
        disabledNMods.add(nmod)
        enabledNMods.remove(nmod)
    }
}
