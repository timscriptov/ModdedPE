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
package com.mcal.mcpelauncher.app

import android.os.Bundle

import com.mcal.mcdesign.app.MCDActivity
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.utils.I18n
import com.mcal.pesdk.PESdk

open class BaseActivity : MCDActivity() {
    protected open val peSdk: PESdk
        get() = ModdedPEApplication.mPESdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        I18n.setLanguage(this)
    }
}
