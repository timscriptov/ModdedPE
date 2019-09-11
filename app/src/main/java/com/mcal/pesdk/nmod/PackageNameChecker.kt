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

internal object PackageNameChecker {
    private fun isValidJavaIdentifier(className: String): Boolean {
        if (className.isEmpty() || !Character.isJavaIdentifierStart(className[0]))
            return false
        val name = className.substring(1)
        for (i in 0 until name.length)
            if (!Character.isJavaIdentifierPart(name[i]))
                return false
        return true
    }

    fun isValidPackageName(fullName: String?): Boolean {
        if (fullName == null)
            return false

        if (!fullName.contains("."))
            return false

        var flag = true
        try {
            if (!fullName.endsWith(".")) {
                val index = fullName.indexOf(".")
                if (index != -1) {
                    val str = fullName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (name in str) {
                        if (name == "") {
                            flag = false
                            break
                        } else if (!isValidJavaIdentifier(name)) {
                            flag = false
                            break
                        }
                    }
                } else if (!isValidJavaIdentifier(fullName)) {
                    flag = false
                }
            } else {
                flag = false
            }
        } catch (ex: Exception) {
            flag = false
        }
        return flag
    }
}
