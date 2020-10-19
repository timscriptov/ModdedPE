package com.microsoft.xal.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Ecdsa {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String ECDSA_SIGNATURE_NAME = "NONEwithECDSA";
    private static final String EC_ALGORITHM_NAME = "secp256r1";
    private static final String KEY_ALIAS_PREFIX = "xal_";

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private KeyPair keyPair;
    private String uniqueId;

    @Nullable
    public static Ecdsa restoreKeyAndId(@NotNull Context context) throws ClassCastException, IllegalArgumentException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        SharedPreferences preferences = context.getSharedPreferences("com.microsoft.xal.crypto", 0);
        if (!preferences.contains("id") || !preferences.contains("public") || !preferences.contains("private")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            return null;
        }
        String pubKeyStr = preferences.getString("public", "");
        String privKeyStr = preferences.getString("private", "");
        String id = preferences.getString("id", "");
        if (pubKeyStr.isEmpty() || privKeyStr.isEmpty() || id.isEmpty()) {
            SharedPreferences.Editor editor2 = preferences.edit();
            editor2.clear();
            editor2.apply();
            return null;
        }
        byte[] pubData = getBytesFromBase64String(pubKeyStr);
        byte[] privData = getBytesFromBase64String(privKeyStr);
        KeyFactory factory = KeyFactory.getInstance("ECDSA", "SC");
        Ecdsa ecdsa = new Ecdsa();
        ecdsa.uniqueId = id;
        ecdsa.keyPair = new KeyPair(factory.generatePublic(new X509EncodedKeySpec(pubData)), factory.generatePrivate(new PKCS8EncodedKeySpec(privData)));
        return ecdsa;
    }

    @NotNull
    @Contract(pure = true)
    private static String getKeyAlias(String uniqueId2) {
        return KEY_ALIAS_PREFIX + uniqueId2;
    }

    private static String getBase64StringFromBytes(byte[] array) {
        return Base64.encodeToString(array, 0, array.length, 11);
    }

    private static byte[] getBytesFromBase64String(String string) throws IllegalArgumentException {
        return Base64.decode(string, 11);
    }

    public void generateKey(String uniqueId2) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "SC");
        keyPairGenerator.initialize(new ECGenParameterSpec(EC_ALGORITHM_NAME));
        uniqueId = uniqueId2;
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public EccPubKey getPublicKey() {
        return new EccPubKey((ECPublicKey) keyPair.getPublic());
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public boolean storeKeyPairAndId(@NotNull Context context, String uniqueId2) {
        SharedPreferences.Editor editor = context.getSharedPreferences("com.microsoft.xal.crypto", 0).edit();
        editor.putString("id", uniqueId2);
        editor.putString("public", getBase64StringFromBytes(keyPair.getPublic().getEncoded()));
        editor.putString("private", getBase64StringFromBytes(keyPair.getPrivate().getEncoded()));
        return editor.commit();
    }

    public byte[] sign(byte[] buffer) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ECDSA_SIGNATURE_NAME);
        signature.initSign(keyPair.getPrivate());
        signature.update(buffer);
        return toP1363SignedBuffer(signature.sign());
    }

    public byte[] hashAndSign(byte[] buffer) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ShaHasher hasher = new ShaHasher();
        hasher.AddBytes(buffer);
        return sign(hasher.SignHash());
    }

    @NotNull
    private byte[] toP1363SignedBuffer(@NotNull byte[] asn1SignedBuffer) {
        int rOffset = 3 + 1;
        int rLength = asn1SignedBuffer[3];
        int sSizeOffset = rLength + 4 + 1;
        int sOffset = sSizeOffset + 1;
        int sLength = asn1SignedBuffer[sSizeOffset];
        if (rLength > 32) {
            rOffset++;
            rLength = 32;
        }
        if (sLength > 32) {
            sOffset++;
            sLength = 32;
        }
        ByteArrayOutputStream signatureStream = new ByteArrayOutputStream();
        signatureStream.write(asn1SignedBuffer, rOffset, rLength);
        signatureStream.write(asn1SignedBuffer, sOffset, sLength);
        return signatureStream.toByteArray();
    }
}
