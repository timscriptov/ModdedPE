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
package com.mcal.pesdk.nmod;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class ExtractFailedException extends Exception {
    public static final int TYPE_JSON_SYNTAX_EXCEPTION = 1;
    public static final int TYPE_IO_EXCEPTION = 2;
    public static final int TYPE_NO_MANIFEST = 3;
    public static final int TYPE_PACKAGE_NOT_FOUND = 4;
    public static final int TYPE_UNDEFINED_PACKAGE_NAME = 5;
    public static final int TYPE_INVAILD_PACKAGE_NAME = 6;
    public static final int TYPE_INEQUAL_PACKAGE_NAME = 7;
    public static final int TYPE_DECODE_FAILED = 8;
    public static final int TYPE_UNEXPECTED = 9;
    public static final int TYPE_REDUNDANT_MANIFEST = 10;

    private int mType;

    public ExtractFailedException(int type, Throwable cause) {
        super(cause);
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public String toTypeString() {
        switch (mType) {
            case TYPE_JSON_SYNTAX_EXCEPTION:
                return "JSON_SYNTAX_EXCEPTION";
            case TYPE_INEQUAL_PACKAGE_NAME:
                return "INEQUAL_PACKAGE_NAME";
            case TYPE_DECODE_FAILED:
                return "DECODE_FAILED";
            case TYPE_IO_EXCEPTION:
                return "IO_EXCEPTION";
            case TYPE_INVAILD_PACKAGE_NAME:
                return "INVAILD_PACKAGE_NAME";
            case TYPE_PACKAGE_NOT_FOUND:
                return "PACKAGE_NOT_FOUND";
            case TYPE_NO_MANIFEST:
                return "NO_MANIFEST";
            case TYPE_UNDEFINED_PACKAGE_NAME:
                return "UNDEFINED_PACKAGE_NAME";
            case TYPE_UNEXPECTED:
                return "UNEXPECTED";
            case TYPE_REDUNDANT_MANIFEST:
                return "REDUNDANT_MANIFEST";
            default:
                return "null";
        }
    }
}
