package com.microsoft.onlineid.internal;

import java.util.Locale;

public class Integers {
    public static int parseIntHex(String hexInt) {
        Strings.verifyArgumentNotNullOrEmpty(hexInt, "hexHr");
        long l = Long.decode(hexInt).longValue();
        if (l >= 0 && l <= 4294967295L) {
            return (int) l;
        }
        throw new IllegalArgumentException(String.format(Locale.US, "Hex string does not fit in integer: %s", new Object[]{hexInt}));
    }
}
