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

class ExtractFailedException(val type: Int, cause: Throwable) : Exception(cause) {

    fun toTypeString(): String {
        return when (type) {
            TYPE_JSON_SYNTAX_EXCEPTION -> "JSON_SYNTAX_EXCEPTION"
            TYPE_INEQUAL_PACKAGE_NAME -> "INEQUAL_PACKAGE_NAME"
            TYPE_DECODE_FAILED -> "DECODE_FAILED"
            TYPE_IO_EXCEPTION -> "IO_EXCEPTION"
            TYPE_INVAILD_PACKAGE_NAME -> "INVAILD_PACKAGE_NAME"
            TYPE_PACKAGE_NOT_FOUND -> "PACKAGE_NOT_FOUND"
            TYPE_NO_MANIFEST -> "NO_MANIFEST"
            TYPE_UNDEFINED_PACKAGE_NAME -> "UNDEFINED_PACKAGE_NAME"
            TYPE_UNEXPECTED -> "UNEXPECTED"
            TYPE_REDUNDANT_MANIFEST -> "REDUNDANT_MANIFEST"
            else -> "null"
        }
    }

    companion object {
        val TYPE_JSON_SYNTAX_EXCEPTION = 1
        val TYPE_IO_EXCEPTION = 2
        val TYPE_NO_MANIFEST = 3
        val TYPE_PACKAGE_NOT_FOUND = 4
        val TYPE_UNDEFINED_PACKAGE_NAME = 5
        val TYPE_INVAILD_PACKAGE_NAME = 6
        val TYPE_INEQUAL_PACKAGE_NAME = 7
        val TYPE_DECODE_FAILED = 8
        val TYPE_UNEXPECTED = 9
        val TYPE_REDUNDANT_MANIFEST = 10
    }
}
