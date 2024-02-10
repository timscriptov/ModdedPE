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

import java.util.*

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
class BrowserSelectionResult constructor(
    private val m_defaultBrowserInfo: BrowserInfo,
    private val m_notes: String,
    private val m_useCustomTabs: Boolean,
) {
    override fun toString(): String {
        val locale = Locale.US
        val arrayList = arrayOfNulls<String>(4)
        arrayList[0] = if (m_useCustomTabs) "CT" else "WK"
        arrayList[1] = m_defaultBrowserInfo.packageName
        arrayList[2] = m_notes
        arrayList[3] = m_defaultBrowserInfo.versionName
        return String.format(locale, "%s-%s-%s::%s", *arrayList)
    }

    fun packageName(): String? {
        return if (m_useCustomTabs) {
            m_defaultBrowserInfo.packageName
        } else {
            null
        }
    }
}
