package com.microsoft.xal.browser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum ShowUrlType {
    Normal,
    CookieRemoval,
    CookieRemovalSkipIfSharedCredentials,
    NonAuthFlow;

    @Nullable
    @Contract(pure = true)
    public static ShowUrlType fromInt(int val) {
        switch (val) {
            case 0:
                return Normal;
            case 1:
                return CookieRemoval;
            case 2:
                return CookieRemovalSkipIfSharedCredentials;
            case 3:
                return NonAuthFlow;
            default:
                return null;
        }
    }
}