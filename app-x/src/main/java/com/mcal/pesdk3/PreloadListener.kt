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
package com.mcal.pesdk3

import android.os.Bundle
import com.mcal.pesdk3.nmod.NMod

open class PreloadListener {
    open fun onStart() {}
    open fun onLoadSubstrateLib() {}
    open fun onLoadXHookLib() {}
    open fun onLoadGameLauncherLib() {}
    open fun onLoadPESdkLib() {}
    open fun onStartLoadingAllNMods() {}
    open fun onNModLoaded(nmod: NMod) {}
    open fun onFailedLoadingNMod(nmod: NMod) {}
    open fun onFinish(bundle: Bundle?) {}
}
