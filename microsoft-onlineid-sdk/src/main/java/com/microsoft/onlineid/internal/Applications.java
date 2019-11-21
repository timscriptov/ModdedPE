package com.microsoft.onlineid.internal;

import android.content.Context;
import android.content.pm.Signature;
import android.util.Base64;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.sts.Cryptography;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

public class Applications {
    public static String buildClientAppUri(Context applicationContext, String packageName) {
        return "android-app://" + packageName + "." + Cryptography.encodeBase32(getFirstCertHash(applicationContext, packageName));
    }

    private static byte[] getFirstCertHash(Context applicationContext, String packageName) {
        boolean z;
        MessageDigest digester = Cryptography.getShaDigester();
        Signature[] signatures = PackageInfoHelper.getAppSignatures(applicationContext, packageName);
        if (signatures.length > 0) {
            z = true;
        } else {
            z = false;
        }
        Assertion.check(z);
        byte[] firstCertHash = digester.digest(signatures[0].toByteArray());
        Settings settings = Settings.getInstance(applicationContext);
        if (settings.isSettingEnabled(Settings.IsCertificateTelemetryNeeded)) {
            Map<String, byte[]> appSignatures = new LinkedHashMap();
            for (Signature signature : signatures) {
                byte[] hash = digester.digest(signature.toByteArray());
                appSignatures.put(Base64.encodeToString(hash, 2), hash);
            }
            ClientAnalytics.get().logCertificates(appSignatures);
            settings.setSetting(Settings.IsCertificateTelemetryNeeded, String.valueOf(false));
        }
        return firstCertHash;
    }
}
