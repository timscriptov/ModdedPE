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

object PackageNameChecker {
    private fun isValidJavaIdentifier(className: String): Boolean {
        if (className.isEmpty() || !Character.isJavaIdentifierStart(className[0])) {
            return false
        }
        val name = className.substring(1)
        for (char in name) {
            if (!Character.isJavaIdentifierPart(char)) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun isValidPackageName(fullName: String?): Boolean {
        if (fullName == null) {
            return false
        }

        if (!fullName.contains(".")) {
            return false
        }

        return try {
            if (fullName.endsWith(".")) {
                false
            } else {
                val parts = fullName.split("\\.".toRegex())
                parts.all { part ->
                    part.isNotEmpty() && isValidJavaIdentifier(part)
                }
            }
        } catch (ex: Exception) {
            false
        }
    }
}
