package com.microsoft.onlineid.internal;

import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.sdk.BuildConfig;

public class Assertion {
    public static void check(boolean assertionExpression) throws AssertionError {
        check(assertionExpression, BuildConfig.VERSION_NAME);
    }

    public static void check(boolean assertionExpression, Object errorMessage) throws AssertionError {
        if (!assertionExpression && Settings.isDebugBuild()) {
            throw new AssertionError(errorMessage);
        }
    }
}
