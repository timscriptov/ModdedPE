package com.microsoft.xal.crypto;

import org.jetbrains.annotations.NotNull;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SecureRandom {
    @NotNull
    public static byte[] GenerateRandomBytes(int numBytes) {
        byte[] bytes = new byte[numBytes];
        new java.security.SecureRandom().nextBytes(bytes);
        return bytes;
    }
}