package com.microsoft.onlineid.sts;

import android.net.Uri.Builder;
import android.util.Base64;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.sts.SharedKeyGenerator.KeyPurpose;
import com.microsoft.onlineid.sts.request.AbstractStsRequest;
import java.security.SecureRandom;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;

public class OneTimeCredentialSigner {
    private static final String ApplicationIDLabel = "appid";
    private static final String BinaryVersionLabel = "bver";
    private static final String CurrentTimeLabel = "ct";
    private static final String DATokenLabel = "da";
    private static final String HashAlgorithmLabel = "hashalg";
    private static final String HashAlgorithmValue = "SHA256";
    private static final String HashLabel = "hash";
    private static final String HmacSha256Algorithm = "HmacSHA256";
    private static final String NonceLabel = "nonce";
    private final Date _currentServerTime;
    private final DAToken _daToken;
    private final SecureRandom _secureRandom;
    private final SharedKeyGenerator _sharedKeyGenerator;

    public OneTimeCredentialSigner(Date currentServerTime, DAToken token) {
        this._daToken = token;
        this._currentServerTime = currentServerTime;
        this._secureRandom = new SecureRandom();
        this._sharedKeyGenerator = new SharedKeyGenerator(token.getSessionKey());
    }

    OneTimeCredentialSigner(DAToken mockDAToken, Date currentServerTime, SecureRandom mockSecureRandom, SharedKeyGenerator mockSharedKeyGenerator) {
        this._daToken = mockDAToken;
        this._currentServerTime = currentServerTime;
        this._secureRandom = mockSecureRandom;
        this._sharedKeyGenerator = mockSharedKeyGenerator;
    }

    public String generateOneTimeSignedCredential(String appId) {
        byte[] nonce = new byte[32];
        this._secureRandom.nextBytes(nonce);
        Builder signedOneTimeCredential = new Builder().appendQueryParameter(CurrentTimeLabel, Long.toString(this._currentServerTime.getTime() / 1000)).appendQueryParameter(HashAlgorithmLabel, HashAlgorithmValue).appendQueryParameter(BinaryVersionLabel, AbstractStsRequest.StsBinaryVersion).appendQueryParameter(ApplicationIDLabel, appId).appendQueryParameter(DATokenLabel, this._daToken.getToken()).appendQueryParameter(NonceLabel, Base64.encodeToString(nonce, 2));
        return signedOneTimeCredential.appendQueryParameter(HashLabel, Base64.encodeToString(Cryptography.getInitializedHmacSha256Digester(new SecretKeySpec(this._sharedKeyGenerator.generateKey(KeyPurpose.CredentialSignature, nonce), HmacSha256Algorithm)).doFinal(signedOneTimeCredential.build().getEncodedQuery().getBytes(Strings.Utf8Charset)), 2)).build().getEncodedQuery();
    }
}
