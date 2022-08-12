/*
 * Copyright (C) 2018-2022 Тимашков Иван
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
package com.microsoft.xal.browser

import org.jetbrains.annotations.Contract

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
enum class ShowUrlType {
    Normal, CookieRemoval, CookieRemovalSkipIfSharedCredentials, NonAuthFlow;

    companion object {
        @JvmStatic
        @Contract(pure = true)
        fun fromInt(value: Int): ShowUrlType? {
            if (value == 0) {
                return Normal
            }
            if (value == 1) {
                return CookieRemoval
            }
            if (value == 2) {
                return CookieRemovalSkipIfSharedCredentials
            }
            return if (value != 3) {
                null
            } else NonAuthFlow
        }
    }
}