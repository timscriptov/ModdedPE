package com.microsoft.onlineid.internal.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.ui.PropertyBag.Key;
import com.microsoft.onlineid.sdk.R;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class BundledAssetVendor implements IWebPropertyProvider {
    private static final String AccessControlAllowOriginAllValue = "*";
    private static final String AccessControlAllowOriginKey = "Access-Control-Allow-Origin";
    private static final Map<String, String> AccessControlAllowOriginMap = Collections.singletonMap(AccessControlAllowOriginKey, AccessControlAllowOriginAllValue);
    private static final String HttpsScheme = "https://";
    private static BundledAssetVendor Instance = null;
    public static final String ManifestAssetPath = "com.microsoft.onlineid.serverAssetBundle.path";
    public static final String ManifestAssetVersion = "com.microsoft.onlineid.serverAssetBundle.version";
    private final Context _applicationContext;
    private AssetManager _assetManager;
    private Object _countLock = new Object();
    private volatile int _hitCount;
    private volatile int _missCount;
    private String _pathToAssetBundle = null;
    private String _version;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key = new int[Key.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[Key.TelemetryResourceBundleVersion.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[Key.TelemetryResourceBundleHits.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[Key.TelemetryResourceBundleMisses.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private BundledAssetVendor(Context context) {
        this._applicationContext = getApplicationContextFromContext(context);
    }

    public static BundledAssetVendor getInstance(Context context) throws IllegalArgumentException {
        if (Instance == null) {
            synchronized (BundledAssetVendor.class) {
                if (Instance == null) {
                    Instance = new BundledAssetVendor(context);
                    Instance.initialize();
                }
            }
        } else if (Instance.getApplicationContext() != getApplicationContextFromContext(context)) {
            Assertion.check(false, "Replacing previous instance with new instance for provided different context.");
            synchronized (BundledAssetVendor.class) {
                Instance = new BundledAssetVendor(context);
                Instance.initialize();
            }
        }
        return Instance;
    }

    private static Context getApplicationContextFromContext(Context context) {
        if (context.getApplicationContext() != null) {
            return context.getApplicationContext();
        }
        return context;
    }

    private Context getApplicationContext() {
        return this._applicationContext;
    }

    private void initialize() {
        this._assetManager = this._applicationContext.getAssets();
        PackageManager packageManager = this._applicationContext.getPackageManager();
        this._missCount = 0;
        this._hitCount = 0;
        try {
            Bundle metaDataBundle = packageManager.getApplicationInfo(this._applicationContext.getPackageName(), 128).metaData;
            if (metaDataBundle == null) {
                this._pathToAssetBundle = null;
                this._version = null;
                return;
            }
            this._pathToAssetBundle = metaDataBundle.getString(ManifestAssetPath);
            this._version = metaDataBundle.getString(ManifestAssetVersion);
        } catch (NameNotFoundException e) {
            Logger.error("Package name not found", e);
        }
    }

    protected String buildLocalAssetPath(String url) {
        return new StringBuilder(this._pathToAssetBundle).append('/').append(url.substring(HttpsScheme.length())).toString();
    }

    @TargetApi(17)
    public WebResourceResponse getAsset(String url) {
        if (TextUtils.isEmpty(this._pathToAssetBundle)) {
            return null;
        }
        String localAssetPath = buildLocalAssetPath(url);
        if (TextUtils.isEmpty(localAssetPath)) {
            return null;
        }
        Mimetype mimetype = Mimetype.findFromFilename(localAssetPath);
        if (mimetype == null) {
            return null;
        }
        try {
            WebResourceResponse response = new WebResourceResponse(mimetype.toString(), "UTF-8", this._assetManager.open(localAssetPath));
            if (VERSION.SDK_INT >= 21 && mimetype == Mimetype.FONT) {
                response.setResponseHeaders(AccessControlAllowOriginMap);
            }
            if (Settings.isDebugBuild()) {
                Logger.info("BundledAssetVendor: Proxied " + url + " with " + localAssetPath);
            }
            incrementHitCount();
            return response;
        } catch (IOException e) {
            if (Settings.isDebugBuild()) {
                Logger.info("BundledAssetVendor: MISS: No proxied asset found for " + url);
            }
            incrementMissCount();
            return null;
        }
    }

    protected void incrementHitCount() {
        synchronized (this._countLock) {
            this._hitCount++;
        }
    }

    protected void incrementMissCount() {
        synchronized (this._countLock) {
            this._missCount++;
        }
    }

    protected void setHitCount(int count) {
        synchronized (this._countLock) {
            this._hitCount = count;
        }
    }

    protected void setMissCount(int count) {
        synchronized (this._countLock) {
            this._missCount = count;
        }
    }

    public boolean handlesProperty(Key key) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[key.ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
            case ApiResult.ResultUINeeded /*2*/:
            case 3:
                return true;
            default:
                return false;
        }
    }

    public String getProperty(Key key) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[key.ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return this._version;
            case ApiResult.ResultUINeeded /*2*/:
                return Integer.toString(this._hitCount);
            case 3:
                return Integer.toString(this._missCount);
            default:
                return null;
        }
    }

    public void setProperty(Key key, String value) {
        try {
            switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$ui$PropertyBag$Key[key.ordinal()]) {
                case ApiResult.ResultUINeeded /*2*/:
                    setHitCount(Integer.parseInt(value));
                    return;
                case 3:
                    setMissCount(Integer.parseInt(value));
                    return;
                default:
                    return;
            }
        } catch (NumberFormatException e) {
            Logger.error("Could not convert string to integer: '" + value + "'");
        }
        Logger.error("Could not convert string to integer: '" + value + "'");
    }
}
