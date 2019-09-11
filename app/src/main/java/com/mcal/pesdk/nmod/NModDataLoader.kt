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
import android.content.SharedPreferences
import java.util.*

internal class NModDataLoader(private val mContext: Context) {

    val allList: ArrayList<String>
        get() {
            val ret = ArrayList<String>()
            ret.addAll(disabledList)
            ret.addAll(enabledList)
            return ret
        }

    private val sharedPreferences: SharedPreferences
        get() = mContext.getSharedPreferences(TAG_SHARED_PREFERENCE, Context.MODE_PRIVATE)

    val enabledList: ArrayList<String>
        get() {
            val preferences = sharedPreferences
            return toArrayList(preferences.getString(TAG_ENABLED_LIST, "")!!)
        }

    val disabledList: ArrayList<String>
        get() {
            val preferences = sharedPreferences
            return toArrayList(preferences.getString(TAG_DISABLE_LIST, "")!!)
        }

    fun removeByName(name: String) {
        val preferences = sharedPreferences
        val enabledList = enabledList
        val disableList = disabledList
        enabledList.remove(name)
        disableList.remove(name)
        val editor = preferences.edit()
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
        editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList))
        editor.apply()
    }

    fun setIsEnabled(nmod: NMod, isEnabled: Boolean) {
        if (isEnabled)
            addNewEnabled(nmod)
        else
            removeEnabled(nmod)
    }

    private fun addNewEnabled(nmod: NMod) {
        val preferences = sharedPreferences
        val enabledList = enabledList
        val disableList = disabledList
        if (enabledList.indexOf(nmod.packageName) == -1)
            enabledList.add(nmod.packageName)
        disableList.remove(nmod.packageName)
        val editor = preferences.edit()
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
        editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList))
        editor.apply()
    }

    private fun removeEnabled(nmod: NMod) {
        val preferences = sharedPreferences
        val enabledList = enabledList
        val disableList = disabledList
        enabledList.remove(nmod.packageName)
        if (disableList.indexOf(nmod.packageName) == -1)
            disableList.add(nmod.packageName)
        val editor = preferences.edit()
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
        editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList))
        editor.apply()
    }

    fun upNMod(nmod: NMod) {
        val preferences = sharedPreferences
        val enabledList = enabledList
        val index = enabledList.indexOf(nmod.packageName)
        if (index == -1 || index == 0)
            return
        val indexFront = index - 1
        val nameFront = enabledList[indexFront]
        if (nameFront == null || nameFront.isEmpty())
            return
        val nameSelf = nmod.packageName
        enabledList[indexFront] = nameSelf
        enabledList[index] = nameFront
        val editor = preferences.edit()
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
        editor.apply()
    }

    fun downNMod(nmod: NMod) {
        val preferences = sharedPreferences
        val enabledList = enabledList
        val index = enabledList.indexOf(nmod.packageName)
        if (index == -1 || index == enabledList.size - 1)
            return
        val indexBack = index + 1
        val nameBack = enabledList[indexBack]
        if (nameBack == null || nameBack.isEmpty())
            return
        val nameSelf = nmod.packageName
        enabledList[indexBack] = nameSelf
        enabledList[index] = nameBack
        val editor = preferences.edit()
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList))
        editor.apply()
    }

    companion object {
        private val TAG_SHARED_PREFERENCE = "nmod_data_list"
        private val TAG_ENABLED_LIST = "enabled_nmods_list"
        private val TAG_DISABLE_LIST = "disabled_nmods_list"

        private fun toArrayList(str: String): ArrayList<String> {
            val mStr = str.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val list = ArrayList<String>()
            for (strElement in mStr) {
                if (strElement != null && !strElement.isEmpty())
                    list.add(strElement)
            }
            return list
        }

        private fun fromArrayList(arrayList: ArrayList<String>?): String {
            var str = ""
            if (arrayList != null) {
                for (mStr in arrayList) {
                    str += mStr
                    str += "/"
                }
            }
            return str
        }
    }
}
