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
package com.mcal.mcpelauncher.utils

import android.app.Activity
import com.mcal.mcpelauncher.data.Preferences
import java.util.*

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object I18n {
    @JvmStatic
    fun setLanguage(context: Activity) {
        val defaultLocale = context.resources.configuration.locale
        val config = context.resources.configuration
        when (Preferences.languageType) {
            0 -> config.setLocale(Locale.getDefault())
            1 -> config.setLocale(Locale.ENGLISH)
            2 -> config.setLocale(Locale.SIMPLIFIED_CHINESE)
            3 -> config.setLocale(Locale.JAPANESE)
            4 -> config.setLocale(Locale("ru", "RU"))
            5 -> config.setLocale(Locale.CHINESE)
            6 -> config.setLocale(Locale("tr")) // Турецкий язык
            7 -> config.setLocale(Locale("pt")) // Португальский
            8 -> config.setLocale(Locale.FRENCH)
            9 -> config.setLocale(Locale("th")) // Тайский
            10 -> config.setLocale(Locale("kk")) // Казахский
            11 -> config.setLocale(Locale("uk")) // Украинский
            else -> config.setLocale(Locale.getDefault())
        }
        if (defaultLocale != config.locale) context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }
}