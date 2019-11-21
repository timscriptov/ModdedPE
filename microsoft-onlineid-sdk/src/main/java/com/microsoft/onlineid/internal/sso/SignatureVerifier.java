package com.microsoft.onlineid.internal.sso;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.sts.Cryptography;
import com.microsoft.onlineid.sts.ServerConfig;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SignatureVerifier {
    private final Context _applicationContext;
    private final ServerConfig _config;
    private final PackageManager _packageManager;

    @Deprecated
    public SignatureVerifier() {
        this._applicationContext = null;
        this._packageManager = null;
        this._config = null;
    }

    public SignatureVerifier(Context localApplicationContext) {
        this._applicationContext = localApplicationContext;
        this._packageManager = localApplicationContext.getPackageManager();
        this._config = new ServerConfig(localApplicationContext);
    }

    public boolean isTrusted(String packageName) {
        if (this._applicationContext.getPackageName().equalsIgnoreCase(packageName)) {
            return true;
        }
        Settings settings = Settings.getInstance(this._applicationContext);
        if (Settings.isDebugBuild() && !settings.isSettingEnabled(Settings.ShouldCheckSsoCertificatesInDebug)) {
            return true;
        }
        try {
            PackageInfo info = this._packageManager.getPackageInfo(packageName, 64);
            Set<String> hashAllowedList = this._config.getStringSet(ServerConfig.AndroidSsoCertificates);
            List<String> appHashes = new ArrayList();
            List<String> untrustedHashes = new ArrayList();
            MessageDigest digest = Cryptography.getSha256Digester();
            boolean isTrusted = true;
            for (Signature signature : info.signatures) {
                String hash = Base64.encodeToString(digest.digest(signature.toByteArray()), 2);
                appHashes.add(hash);
                if (!hashAllowedList.contains(hash)) {
                    isTrusted = false;
                    untrustedHashes.add(hash);
                }
            }
            if (isTrusted) {
                return isTrusted;
            }
            Logger.warning("Not trusting " + packageName + " because some hashes are not in the allowed list: " + Arrays.toString(untrustedHashes.toArray()));
            Logger.warning("Hashes for " + packageName + " are: " + Arrays.toString(appHashes.toArray()));
            Logger.warning("Allowed list is: " + Arrays.toString(hashAllowedList.toArray()));
            return isTrusted;
        } catch (NameNotFoundException ex) {
            String message = "Cannot check trust state of missing package: " + packageName;
            Logger.error(message, ex);
            Assertion.check(false, message);
            return false;
        }
    }

    public boolean isPackageInUid(int uid, String packageName) {
        String[] packages = this._packageManager.getPackagesForUid(uid);
        if (packages == null || packages.length == 0) {
            return false;
        }
        return Arrays.asList(packages).contains(packageName);
    }
}
