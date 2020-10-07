package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEAssert {
    static final boolean $assertionsDisabled = (!XLEAssert.class.desiredAssertionStatus());

    public static void assertTrue(String message, boolean condition) {
        if (!$assertionsDisabled && !condition) {
            throw new AssertionError();
        }
    }

    public static void assertIsUIThread() {
        assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }

    public static void assertIsNotUIThread() {
        assertTrue(Thread.currentThread() != ThreadManager.UIThread);
    }

    public static void assertTrue(boolean condition) {
        assertTrue((String) null, condition);
    }

    public static void assertNotNull(Object object) {
        assertTrue((String) null, object != null);
    }

    public static void assertNull(Object object) {
        assertTrue((String) null, object == null);
    }

    public static void assertNotNull(String message, Object object) {
        assertTrue(message, object != null);
    }

    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    public static void fail(String message) {
        assertTrue(message, false);
    }

    @NotNull
    private static String getCallerLocation() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int depth = 0;
        while (depth < elements.length && (!elements[depth].getClassName().equals(XLEAssert.class.getName()) || !elements[depth].getMethodName().equals("getCallerLocation"))) {
            depth++;
        }
        while (depth < elements.length && elements[depth].getClassName().equals(XLEAssert.class.getName())) {
            depth++;
        }
        if (depth < elements.length) {
            return elements[depth].toString();
        }
        return "unknown";
    }
}
