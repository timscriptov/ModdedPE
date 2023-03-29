package com.microsoft.xal.crypto;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class SecureRandom {
    @NonNull
    public static byte[] GenerateRandomBytes(int i) {
        byte[] bArr = new byte[i];
        new java.security.SecureRandom().nextBytes(bArr);
        return bArr;
    }
}