package com.microsoft.onlineid.internal;

public class Objects {
    public static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    public static void verifyArgumentNotNull(Object o, String argumentName) {
        if (o == null) {
            throw new IllegalArgumentException(argumentName + " must not be null.");
        }
    }
}
