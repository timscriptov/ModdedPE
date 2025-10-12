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
import com.mcal.pesdk3.NModPreferences

class NModDataLoader(
    private val context: Context
) {
    private val nModPreferences = NModPreferences(context)

    fun getNModsPreferences() : NModPreferences {
        return nModPreferences
    }

    fun getAllList(): ArrayList<String> {
        val result = ArrayList<String>()
        result.addAll(nModPreferences.enabledNMods)
        result.addAll(nModPreferences.disabledNMods)
        return result
    }

    fun removeByName(name: String) {
        val enabledList = nModPreferences.enabledNMods
        val disabledList = nModPreferences.disabledNMods

        enabledList.remove(name)
        disabledList.remove(name)

        nModPreferences.enabledNMods = enabledList
        nModPreferences.disabledNMods = disabledList
    }

    fun setIsEnabled(nMod: NMod, isEnabled: Boolean) {
        if (isEnabled) {
            addNewEnabled(nMod)
        } else {
            removeEnabled(nMod)
        }
    }

    private fun addNewEnabled(nMod: NMod) {
        val enabledList = nModPreferences.enabledNMods
        val disabledList = nModPreferences.disabledNMods

        if (!enabledList.contains(nMod.getPackageName())) {
            enabledList.add(nMod.getPackageName())
        }
        disabledList.remove(nMod.getPackageName())

        nModPreferences.enabledNMods = enabledList
        nModPreferences.disabledNMods = disabledList
    }

    private fun removeEnabled(nMod: NMod) {
        val enabledList = nModPreferences.enabledNMods
        val disabledList = nModPreferences.disabledNMods

        enabledList.remove(nMod.getPackageName())
        if (!disabledList.contains(nMod.getPackageName())) {
            disabledList.add(nMod.getPackageName())
        }

        nModPreferences.enabledNMods = enabledList
        nModPreferences.disabledNMods = disabledList
    }

    fun upNMod(nMod: NMod) {
        val enabledList = nModPreferences.enabledNMods
        val index = enabledList.indexOf(nMod.getPackageName())

        if (index == -1 || index == 0) {
            return
        }

        val indexFront = index - 1
        val nameFront = enabledList[indexFront]
        if (nameFront.isEmpty()) {
            return
        }

        val nameSelf = nMod.getPackageName()
        enabledList[indexFront] = nameSelf
        enabledList[index] = nameFront

        nModPreferences.enabledNMods = enabledList
    }

    fun downNMod(nMod: NMod) {
        val enabledList = nModPreferences.enabledNMods
        val index = enabledList.indexOf(nMod.getPackageName())

        if (index == -1 || index == enabledList.size - 1) {
            return
        }

        val indexBack = index + 1
        val nameBack = enabledList[indexBack]
        if (nameBack.isEmpty()) {
            return
        }

        val nameSelf = nMod.getPackageName()
        enabledList[indexBack] = nameSelf
        enabledList[index] = nameBack

        nModPreferences.enabledNMods = enabledList
    }
}
