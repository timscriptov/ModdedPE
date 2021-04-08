package com.microsoft.xal.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ShaHasher {
    private final MessageDigest md = MessageDigest.getInstance("SHA-256");

    public ShaHasher() throws NoSuchAlgorithmException {
    }

    public void AddBytes(byte[] buffer) {
        md.update(buffer);
    }

    public byte[] SignHash() {
        return md.digest();
    }
}