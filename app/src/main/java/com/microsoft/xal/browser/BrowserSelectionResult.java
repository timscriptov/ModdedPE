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
package com.microsoft.xal.browser;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class BrowserSelectionResult {
    private final BrowserInfo m_defaultBrowserInfo;
    private final String m_notes;
    private final boolean m_useCustomTabs;

    BrowserSelectionResult(BrowserInfo browserInfo, String str, boolean z) {
        this.m_defaultBrowserInfo = browserInfo;
        this.m_notes = str;
        this.m_useCustomTabs = z;
    }

    @NonNull
    public String toString() {
        Locale locale = Locale.US;
        Object[] objArr = new Object[4];
        objArr[0] = this.m_useCustomTabs ? "CT" : "WK";
        objArr[1] = this.m_defaultBrowserInfo.packageName;
        objArr[2] = this.m_notes;
        objArr[3] = this.m_defaultBrowserInfo.versionName;
        return String.format(locale, "%s-%s-%s::%s", objArr);
    }

    public String packageName() {
        if (this.m_useCustomTabs) {
            return this.m_defaultBrowserInfo.packageName;
        }
        return null;
    }

    static class BrowserInfo {
        public final String packageName;
        public final int versionCode;
        public final String versionName;

        BrowserInfo(String str, int i, String str2) {
            this.packageName = str;
            this.versionCode = i;
            this.versionName = str2;
        }
    }
}