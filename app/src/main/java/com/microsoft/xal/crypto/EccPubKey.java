package com.microsoft.xal.crypto;

import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class EccPubKey {
    private final ECPublicKey publicKey;

    EccPubKey(ECPublicKey publicKey2) {
        publicKey = publicKey2;
    }

    public BigInteger getX() {
        return publicKey.getW().getAffineX();
    }

    public String getBase64UrlX() {
        return getBase64Coordinate(getX());
    }

    public BigInteger getY() {
        return publicKey.getW().getAffineY();
    }

    public String getBase64UrlY() {
        return getBase64Coordinate(getY());
    }

    private String getBase64Coordinate(@NotNull BigInteger coordinate) {
        byte[] coordinateByteArray = coordinate.toByteArray();
        int offset = 0;
        if (coordinateByteArray.length > 32) {
            offset = coordinateByteArray.length - 32;
        } else if (coordinateByteArray.length < 32) {
            byte[] resized = new byte[32];
            System.arraycopy(coordinateByteArray, 0, resized, resized.length - coordinateByteArray.length, coordinateByteArray.length);
            coordinateByteArray = resized;
        }
        return Base64.encodeToString(coordinateByteArray, offset, 32, 11);
    }
}
