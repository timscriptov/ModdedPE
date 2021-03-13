/*
 * Copyright (C) 2018-2021 Тимашков Иван
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
package com.mcal.pesdk.nmod;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class NModDataLoader {
    private static final String TAG_SHARED_PREFERENCE = "nmod_data_list";
    private static final String TAG_ENABLED_LIST = "enabled_nmods_list";
    private static final String TAG_DISABLE_LIST = "disabled_nmods_list";
    private Context mContext;

    NModDataLoader(Context context) {
        mContext = context;
    }

    @NotNull
    private static ArrayList<String> toArrayList(@NotNull String str) {
        String[] mStr = str.split("/");
        ArrayList<String> list = new ArrayList<>();
        for (String strElement : mStr) {
            if (strElement != null && !strElement.isEmpty())
                list.add(strElement);
        }
        return list;
    }

    private static String fromArrayList(ArrayList<String> arrayList) {
        String str = "";
        if (arrayList != null) {
            for (String mStr : arrayList) {
                str += mStr;
                str += "/";
            }
        }
        return str;
    }

    ArrayList<String> getAllList() {
        ArrayList<String> ret = new ArrayList<>();
        ret.addAll(getDisabledList());
        ret.addAll(getEnabledList());
        return ret;
    }

    void removeByName(String name) {
        SharedPreferences preferences = getSharedPreferences();
        ArrayList<String> enabledList = getEnabledList();
        ArrayList<String> disableList = getDisabledList();
        enabledList.remove(name);
        disableList.remove(name);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
        editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList));
        editor.apply();
    }

    void setIsEnabled(NMod nmod, boolean isEnabled) {
        if (isEnabled)
            addNewEnabled(nmod);
        else
            removeEnabled(nmod);
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(TAG_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    private void addNewEnabled(@NotNull NMod nmod) {
        SharedPreferences preferences = getSharedPreferences();
        ArrayList<String> enabledList = getEnabledList();
        ArrayList<String> disableList = getDisabledList();
        if (enabledList.indexOf(nmod.getPackageName()) == -1)
            enabledList.add(nmod.getPackageName());
        disableList.remove(nmod.getPackageName());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
        editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList));
        editor.apply();
    }

    private void removeEnabled(@NotNull NMod nmod) {
        SharedPreferences preferences = getSharedPreferences();
        ArrayList<String> enabledList = getEnabledList();
        ArrayList<String> disableList = getDisabledList();
        enabledList.remove(nmod.getPackageName());
        if (disableList.indexOf(nmod.getPackageName()) == -1)
            disableList.add(nmod.getPackageName());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
        editor.putString(TAG_DISABLE_LIST, fromArrayList(disableList));
        editor.apply();
    }

    void upNMod(@NotNull NMod nmod) {
        SharedPreferences preferences = getSharedPreferences();
        ArrayList<String> enabledList = getEnabledList();
        int index = enabledList.indexOf(nmod.getPackageName());
        if (index == -1 || index == 0)
            return;
        int indexFront = index - 1;
        String nameFront = enabledList.get(indexFront);
        if (nameFront == null || nameFront.isEmpty())
            return;
        String nameSelf = nmod.getPackageName();
        enabledList.set(indexFront, nameSelf);
        enabledList.set(index, nameFront);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
        editor.apply();
    }

    void downNMod(@NotNull NMod nmod) {
        SharedPreferences preferences = getSharedPreferences();
        ArrayList<String> enabledList = getEnabledList();
        int index = enabledList.indexOf(nmod.getPackageName());
        if (index == -1 || index == (enabledList.size() - 1))
            return;
        int indexBack = index + 1;
        String nameBack = enabledList.get(indexBack);
        if (nameBack == null || nameBack.isEmpty())
            return;
        String nameSelf = nmod.getPackageName();
        enabledList.set(indexBack, nameSelf);
        enabledList.set(index, nameBack);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TAG_ENABLED_LIST, fromArrayList(enabledList));
        editor.apply();
    }

    ArrayList<String> getEnabledList() {
        SharedPreferences preferences = getSharedPreferences();
        return toArrayList(preferences.getString(TAG_ENABLED_LIST, ""));
    }

    ArrayList<String> getDisabledList() {
        SharedPreferences preferences = getSharedPreferences();
        return toArrayList(preferences.getString(TAG_DISABLE_LIST, ""));
    }
}
