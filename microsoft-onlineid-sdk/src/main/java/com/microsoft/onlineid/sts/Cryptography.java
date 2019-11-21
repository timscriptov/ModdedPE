package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Assertion;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {
    public static final String AesAlgorithm = "AES";
    public static final int AesCbcPkcs5PaddingInitializationVectorByteCount = 16;
    public static final String AesCbcPkcs5PaddingTransformation = "AES/CBC/PKCS5Padding";
    private static final String ByteToBase32Lookup = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    public static final String HmacSha1Algorithm = "HmacSHA1";
    public static final String HmacSha256Algorithm = "HmacSHA256";
    public static final String Sha256Algorithm = "SHA256";

    public static MessageDigest getSha256Digester() {
        try {
            return MessageDigest.getInstance(Sha256Algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageDigest getShaDigester() {
        try {
            return MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Mac getInitializedHmacSha1Digester(Key derivedKey) {
        return getInitializedHmacDigester(derivedKey, HmacSha1Algorithm);
    }

    public static Mac getInitializedHmacSha256Digester(Key derivedKey) {
        return getInitializedHmacDigester(derivedKey, HmacSha256Algorithm);
    }

    private static Mac getInitializedHmacDigester(Key derivedKey, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(derivedKey);
            return mac;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static byte[] decryptWithAesCbcPcs5PaddingCipher(byte[] encryptedData, byte[] encryptionKey) throws IllegalBlockSizeException, BadPaddingException {
        return getInitializedDecryptionCipher(AesCbcPkcs5PaddingTransformation, new SecretKeySpec(encryptionKey, AesAlgorithm), new IvParameterSpec(encryptedData, 0, AesCbcPkcs5PaddingInitializationVectorByteCount)).doFinal(encryptedData, AesCbcPkcs5PaddingInitializationVectorByteCount, encryptedData.length - 16);
    }

    private static Cipher getInitializedDecryptionCipher(String transformation, Key key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(2, key, iv);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e2) {
            throw new RuntimeException(e2);
        } catch (InvalidKeyException e3) {
            throw new RuntimeException(e3);
        } catch (InvalidAlgorithmParameterException e4) {
            throw new RuntimeException(e4);
        }
    }

    public static String encodeBase32(byte[] input) {
        Assertion.check(input != null);
        StringBuilder output = new StringBuilder(((input.length * 8) / 5) + 1);
        for (int i = 0; i < input.length; i += 5) {
            byte[] buffer = new byte[8];
            int numBytes = Math.min(input.length - i, 5);
            System.arraycopy(input, i, buffer, (buffer.length - numBytes) - 1, numBytes);
            long val = ByteBuffer.wrap(buffer).getLong();
            for (int bitOffset = ((numBytes + 1) * 8) - 5; bitOffset > 3; bitOffset -= 5) {
                output.append(ByteToBase32Lookup.charAt((int) ((val >>> bitOffset) & 31)));
            }
        }
        return output.toString();
    }
}
