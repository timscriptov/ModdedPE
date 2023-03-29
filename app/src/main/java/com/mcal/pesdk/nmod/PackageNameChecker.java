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

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
class PackageNameChecker {
    static private boolean isValidJavaIdentifier(@NotNull String className) {
        if (className.length() == 0 || !Character.isJavaIdentifierStart(className.charAt(0)))
            return false;
        String name = className.substring(1);
        for (int i = 0; i < name.length(); i++)
            if (!Character.isJavaIdentifierPart(name.charAt(i)))
                return false;
        return true;
    }

    static boolean isValidPackageName(String fullName) {
        if (fullName == null)
            return false;

        if (!fullName.contains("."))
            return false;

        boolean flag = true;
        try {
            if (!fullName.endsWith(".")) {
                int index = fullName.indexOf(".");
                if (index != -1) {
                    String[] str = fullName.split("\\.");
                    for (String name : str) {
                        if (name.equals("")) {
                            flag = false;
                            break;
                        } else if (!isValidJavaIdentifier(name)) {
                            flag = false;
                            break;
                        }
                    }
                } else if (!isValidJavaIdentifier(fullName)) {
                    flag = false;
                }
            } else {
                flag = false;
            }
        } catch (Exception ex) {
            flag = false;
        }
        return flag;
    }
}
