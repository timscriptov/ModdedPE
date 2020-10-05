package com.mojang.minecraftpe;

import android.content.Intent;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationCancelError;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ActiveDirectorySignIn implements ActivityListener {
    public String mAccessToken;
    public AuthenticationContext mAuthenticationContext;
    public boolean mDialogOpen = false;
    public String mIdentityToken;
    public String mLastError;
    public boolean mResultObtained = false;
    public String mUserId;
    private boolean mIsActivityListening = false;

    public ActiveDirectorySignIn() {
        MainActivity.mInstance.addListener(this);
    }

    @NotNull
    @Contract(" -> new")
    public static ActiveDirectorySignIn createActiveDirectorySignIn() {
        return new ActiveDirectorySignIn();
    }

    public native void nativeOnDataChanged();

    public boolean getDialogOpen() {
        return mDialogOpen;
    }

    public boolean getResultObtained() {
        return mResultObtained;
    }

    public String getIdentityToken() {
        return mIdentityToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getLastError() {
        return mLastError;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAuthenticationContext != null) {
            mAuthenticationContext.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onResume() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public void authenticate(int prompt) {
        boolean doRefresh = true;
        mResultObtained = false;
        mDialogOpen = true;
        final PromptBehavior promptBehavior = prompt == 0 ? PromptBehavior.Always : PromptBehavior.Auto;
        if (prompt != 2) {
            doRefresh = false;
        }
        boolean finalDoRefresh = doRefresh;
        MainActivity.mInstance.runOnUiThread(() -> {
            AuthenticationContext unused = mAuthenticationContext = new AuthenticationContext(MainActivity.mInstance, "https://login.windows.net/common", true);
            if (finalDoRefresh) {
                mAuthenticationContext.acquireTokenSilent("https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", mUserId, getAdalCallback());
            } else {
                mAuthenticationContext.acquireToken(MainActivity.mInstance, "https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", "urn:ietf:wg:oauth:2.0:oob", "", promptBehavior, "", getAdalCallback());
            }
        });
    }

    public void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            return;
        }
        CookieSyncManager syncManager = CookieSyncManager.createInstance(MainActivity.mInstance);
        syncManager.startSync();
        cookieManager.removeAllCookie();
        syncManager.stopSync();
        syncManager.sync();
    }

    public AuthenticationCallback<AuthenticationResult> getAdalCallback() {
        return new AuthenticationCallback<AuthenticationResult>() {
            public void onSuccess(AuthenticationResult authenticationResult) {
                System.out.println("ADAL sign in success");
                boolean unused = mResultObtained = true;
                String unused2 = mAccessToken = authenticationResult.getAccessToken();
                String unused3 = mIdentityToken = authenticationResult.getIdToken();
                String unused4 = mLastError = "";
                boolean unused5 = mDialogOpen = false;
                String unused6 = mUserId = authenticationResult.getUserInfo().getUserId();
                nativeOnDataChanged();
            }

            public void onError(Exception e) {
                System.out.println("ADAL sign in error: " + e.getMessage());
                boolean unused = mResultObtained = false;
                if (!(e instanceof AuthenticationCancelError)) {
                    String unused2 = mLastError = e.getMessage();
                }
                boolean unused3 = mDialogOpen = false;
                String unused4 = mUserId = "";
                nativeOnDataChanged();
            }
        };
    }
}