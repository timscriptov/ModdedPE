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

import android.os.Bundle

import com.mojang.minecraftpe.MainActivity

class NModLib(private val mName: String) {

    private external fun nativeCallOnActivityFinish(name: String, mainActivity: MainActivity): Boolean

    private external fun nativeCallOnLoad(name: String, mcVersion: String, apiVersion: String): Boolean

    private external fun nativeCallOnActivityCreate(mame: String, mainActivity: MainActivity, savedInstanceState: Bundle): Boolean

    fun callOnActivityCreate(mainActivity: com.mojang.minecraftpe.MainActivity, bundle: Bundle): Boolean {
        return nativeCallOnActivityCreate(mName, mainActivity, bundle)
    }

    fun callOnActivityFinish(mainActivity: com.mojang.minecraftpe.MainActivity): Boolean {
        return nativeCallOnActivityFinish(mName, mainActivity)
    }

    fun callOnLoad(mcver: String, apiVer: String): Boolean {
        return nativeCallOnLoad(mName, mcver, apiVer)
    }
}
