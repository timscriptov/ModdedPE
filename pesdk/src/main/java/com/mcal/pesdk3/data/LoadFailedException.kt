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
package com.mcal.pesdk3.data

class LoadFailedException(val type: Int, cause: Throwable?) : Exception(cause) {
    fun toTypeString(): String {
        return when (this.type) {
            TYPE_DECODE_FAILED -> "DECODE_FAILED"
            TYPE_LOAD_LIB_FAILED -> "LOAD_LIB_FAILED"
            TYPE_FILE_NOT_FOUND -> "FILE_NOT_FOUND"
            TYPE_INVALID_SIZE -> "INVALID_SIZE"
            TYPE_IO_FAILED -> "IO_FAILED"
            TYPE_JSON_SYNTAX -> "JSON_SYNTAX"
            TYPE_LOAD_DEX_FAILED -> "LOAD_DEX_FAILED"
            else -> "TYPE"
        }
    }

    companion object {
        const val TYPE_LOAD_LIB_FAILED: Int = 1
        const val TYPE_IO_FAILED: Int = 2
        const val TYPE_JSON_SYNTAX: Int = 3
        const val TYPE_FILE_NOT_FOUND: Int = 4
        const val TYPE_DECODE_FAILED: Int = 5
        const val TYPE_INVALID_SIZE: Int = 6
        const val TYPE_LOAD_DEX_FAILED: Int = 7
    }
}
