package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.JavaUtil;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public enum UserStatus {
    Offline,
    Online;

    public static UserStatus getStatusFromString(String str) {
        if (JavaUtil.stringsEqualCaseInsensitive(str, Online.toString())) {
            return Online;
        }
        return Offline;
    }
}
