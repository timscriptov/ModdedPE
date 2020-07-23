/*
 * Copyright (C) 2018-2020 Тимашков Иван
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
package com.mcal.mcpelauncher.utils;

import android.app.Activity;
import android.content.res.Configuration;

import com.mcal.mcpelauncher.data.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class I18n {
    public static void setLanguage(@NotNull Activity context) {
        Locale defaultLocale = context.getResources().getConfiguration().locale;
        Configuration config = context.getResources().getConfiguration();

        switch (Preferences.getLanguageType()) {
            case 0:
            default:
                config.setLocale(Locale.getDefault());
                break;
            case 1:
                config.setLocale(Locale.ENGLISH);
                break;
            case 2:
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
            case 3:
                config.setLocale(Locale.JAPANESE);
                break;
            case 4:
                config.setLocale(new Locale("ru", "RU"));
                break;
            case 5:
                config.setLocale(Locale.CHINESE);
                break;
            case 6:
                config.setLocale(new Locale("tr"));// Турецкий язык
                break;
            case 7:
                config.setLocale(new Locale("pt"));// Португальский
                break;
            case 8:
                config.setLocale(Locale.FRENCH);
                break;
            case 9:
                config.setLocale(new Locale("th"));// Тайский
                break;
            case 10:
                config.setLocale(new Locale("kk"));// Казахский
                break;
            case 11:
                config.setLocale(new Locale("uk"));// Украинский
                break;
        }
        if (!defaultLocale.equals(config.locale))
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}