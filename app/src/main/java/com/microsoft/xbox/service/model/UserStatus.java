package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.JavaUtil;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public enum UserStatus {
    Offline,
    Online;

    public static UserStatus getStatusFromString(String status) {
        if (JavaUtil.stringsEqualCaseInsensitive(status, Online.toString())) {
            return Online;
        }
        return Offline;
    }
}
