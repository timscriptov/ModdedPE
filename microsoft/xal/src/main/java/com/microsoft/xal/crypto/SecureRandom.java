package com.microsoft.xal.crypto;

import androidx.annotation.NonNull;

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class SecureRandom {
    @NonNull
    public static byte[] GenerateRandomBytes(int i) {
        byte[] bArr = new byte[i];
        new java.security.SecureRandom().nextBytes(bArr);
        return bArr;
    }
}