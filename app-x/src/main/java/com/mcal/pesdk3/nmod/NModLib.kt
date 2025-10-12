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

import android.os.Bundle
import com.mojang.minecraftpe.MainActivity

class NModLib(
    private val name: String
) {
    init {
        nativeRegisterNatives(NModLib::class.java)
    }

    fun callOnActivityCreate(mainActivity: MainActivity, bundle: Bundle?): Boolean {
        return nativeCallOnActivityCreate(name, mainActivity, bundle)
    }

    fun callOnActivityFinish(mainActivity: MainActivity): Boolean {
        return nativeCallOnActivityFinish(name, mainActivity)
    }

    fun callOnLoad(mcVer: String, apiVer: String): Boolean {
        return nativeCallOnLoad(name, mcVer, apiVer)
    }

    companion object {
        private external fun nativeRegisterNatives(cls: Class<*>): Boolean

        private external fun nativeCallOnActivityFinish(name: String, mainActivity: MainActivity): Boolean

        private external fun nativeCallOnLoad(name: String, mcVersion: String, apiVersion: String): Boolean

        private external fun nativeCallOnActivityCreate(
            name: String,
            mainActivity: MainActivity,
            savedInstanceState: Bundle?
        ): Boolean
    }
}
