package com.microsoft.xal.browser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum ShowUrlType {
    Normal,
    CookieRemoval,
    CookieRemovalSkipIfSharedCredentials,
    NonAuthFlow;

    @Contract(pure = true)
    public static @Nullable ShowUrlType fromInt(int val) {
        if (val == 0) {
            return Normal;
        }
        if (val == 1) {
            return CookieRemoval;
        }
        if (val == 2) {
            return CookieRemovalSkipIfSharedCredentials;
        }
        if (val != 3) {
            return null;
        }
        return NonAuthFlow;
    }
}