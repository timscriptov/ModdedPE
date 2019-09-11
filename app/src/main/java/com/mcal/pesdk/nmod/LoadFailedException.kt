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

class LoadFailedException(val type: Int, cause: Throwable) : Exception(cause) {

    fun toTypeString(): String {
        when (type) {
            TYPE_DECODE_FAILED -> return "DECODE_FAILED"
            TYPE_LOAD_LIB_FAILED -> return "LOAD_LIB_FAILED"
            TYPE_FILE_NOT_FOUND -> return "FILE_NOT_FOUND"
            TYPE_INVALID_SIZE -> return "INVALID_SIZE"
            TYPE_IO_FAILED -> return "IO_FAILED"
            TYPE_JSON_SYNTAX -> return "JSON_SYNTAX"
        }
        return "TYPE"
    }

    companion object {
        val TYPE_LOAD_LIB_FAILED = 1
        val TYPE_IO_FAILED = 2
        val TYPE_JSON_SYNTAX = 3
        val TYPE_FILE_NOT_FOUND = 4
        val TYPE_DECODE_FAILED = 5
        val TYPE_INVALID_SIZE = 6
    }
}
