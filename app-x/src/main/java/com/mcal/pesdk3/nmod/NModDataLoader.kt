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
import android.content.SharedPreferences

class NModDataLoader(
    private val context: Context
) {
    companion object {
        private const val TAG_SHARED_PREFERENCE = "nmod_data_list"
        private const val TAG_ENABLED_LIST = "enabled_nmods_list"
        private const val TAG_DISABLE_LIST = "disabled_nmods_list"
    }

    private fun toArrayList(str: String): ArrayList<String> {
        return str.split("/")
            .filter { it.isNotEmpty() }
            .toCollection(ArrayList())
    }

    private fun fromArrayList(arrayList: ArrayList<String>): String {
        return arrayList.joinToString("/")
    }

    fun getAllList(): ArrayList<String> {
        val result = ArrayList<String>()
        result.addAll(getDisabledList())
        result.addAll(getEnabledList())
        return result
    }

    fun removeByName(name: String) {
        val preferences = getSharedPreferences()
        val enabledList = getEnabledList()
        val disableList = getDisabledList()

        enabledList.remove(name)
        disableList.remove(name)

        preferences.edit()
            .putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
            .putString(TAG_DISABLE_LIST, fromArrayList(disableList))
            .apply()
    }

    fun setIsEnabled(nMod: NMod, isEnabled: Boolean) {
        if (isEnabled) {
            addNewEnabled(nMod)
        } else {
            removeEnabled(nMod)
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(TAG_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    private fun addNewEnabled(nMod: NMod) {
        val preferences = getSharedPreferences()
        val enabledList = getEnabledList()
        val disableList = getDisabledList()

        if (!enabledList.contains(nMod.getPackageName())) {
            enabledList.add(nMod.getPackageName())
        }
        disableList.remove(nMod.getPackageName())

        preferences.edit()
            .putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
            .putString(TAG_DISABLE_LIST, fromArrayList(disableList))
            .apply()
    }

    private fun removeEnabled(nMod: NMod) {
        val preferences = getSharedPreferences()
        val enabledList = getEnabledList()
        val disableList = getDisabledList()

        enabledList.remove(nMod.getPackageName())
        if (!disableList.contains(nMod.getPackageName())) {
            disableList.add(nMod.getPackageName())
        }

        preferences.edit()
            .putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
            .putString(TAG_DISABLE_LIST, fromArrayList(disableList))
            .apply()
    }

    fun upNMod(nMod: NMod) {
        val preferences = getSharedPreferences()
        val enabledList = getEnabledList()
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

        preferences.edit()
            .putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
            .apply()
    }

    fun downNMod(nMod: NMod) {
        val preferences = getSharedPreferences()
        val enabledList = getEnabledList()
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

        preferences.edit()
            .putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
            .apply()
    }

    fun getEnabledList(): ArrayList<String> {
        val preferences = getSharedPreferences()
        return toArrayList(preferences.getString(TAG_ENABLED_LIST, "") ?: "")
    }

    fun getDisabledList(): ArrayList<String> {
        val preferences = getSharedPreferences()
        return toArrayList(preferences.getString(TAG_DISABLE_LIST, "") ?: "")
    }
}
