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
package com.mcal.pesdk.nmod;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class LoadFailedException extends Exception {
    public static final int TYPE_LOAD_LIB_FAILED = 1;
    public static final int TYPE_IO_FAILED = 2;
    public static final int TYPE_JSON_SYNTAX = 3;
    public static final int TYPE_FILE_NOT_FOUND = 4;
    public static final int TYPE_DECODE_FAILED = 5;
    public static final int TYPE_INVALID_SIZE = 6;
    private int mType;

    public LoadFailedException(int type, Throwable cause) {
        super(cause);
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public String toTypeString() {
        switch (mType) {
            case TYPE_DECODE_FAILED:
                return "DECODE_FAILED";
            case TYPE_LOAD_LIB_FAILED:
                return "LOAD_LIB_FAILED";
            case TYPE_FILE_NOT_FOUND:
                return "FILE_NOT_FOUND";
            case TYPE_INVALID_SIZE:
                return "INVALID_SIZE";
            case TYPE_IO_FAILED:
                return "IO_FAILED";
            case TYPE_JSON_SYNTAX:
                return "JSON_SYNTAX";
        }
        return "TYPE";
    }
}
