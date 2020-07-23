package com.mojang.minecraftpe;

import android.content.Intent;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;

import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationCancelError;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ActiveDirectorySignIn implements ActivityListener {
    public String mAccessToken;
    public AuthenticationContext mAuthenticationContext;
    public boolean mDialogOpen = false;
    public String mIdentityToken;
    private boolean mIsActivityListening = false;
    public String mLastError;
    public boolean mResultObtained = false;
    public String mUserId;

    public native void nativeOnDataChanged();

    public ActiveDirectorySignIn() {
        MainActivity.mInstance.addListener(this);
    }

    public boolean getDialogOpen() {
        return this.mDialogOpen;
    }

    public boolean getResultObtained() {
        return this.mResultObtained;
    }

    public String getIdentityToken() {
        return this.mIdentityToken;
    }

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public String getLastError() {
        return this.mLastError;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mAuthenticationContext != null) {
            this.mAuthenticationContext.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onResume() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public void authenticate(int prompt) {
        final boolean doRefresh = true;
        this.mResultObtained = false;
        this.mDialogOpen = true;
        final PromptBehavior promptBehavior = prompt == 0 ? PromptBehavior.Always : PromptBehavior.Auto;
        /*if (prompt != 2) {
            doRefresh = false;
        }*/
        MainActivity.mInstance.runOnUiThread(() -> {
            AuthenticationContext unused = ActiveDirectorySignIn.this.mAuthenticationContext = new AuthenticationContext(MainActivity.mInstance, "https://login.windows.net/common", true);
            if (doRefresh) {
                ActiveDirectorySignIn.this.mAuthenticationContext.acquireTokenSilent("https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", ActiveDirectorySignIn.this.mUserId, ActiveDirectorySignIn.this.getAdalCallback());
            } else {
                ActiveDirectorySignIn.this.mAuthenticationContext.acquireToken(MainActivity.mInstance, "https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", "urn:ietf:wg:oauth:2.0:oob", "", promptBehavior, "", ActiveDirectorySignIn.this.getAdalCallback());
            }
        });
    }

    public void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.removeAllCookies((ValueCallback<Boolean>) null);
            cookieManager.flush();
            return;
        }
        CookieSyncManager syncManager = CookieSyncManager.createInstance(MainActivity.mInstance);
        syncManager.startSync();
        cookieManager.removeAllCookie();
        syncManager.stopSync();
        syncManager.sync();
    }

    @NotNull
    @Contract(" -> new")
    public static ActiveDirectorySignIn createActiveDirectorySignIn() {
        return new ActiveDirectorySignIn();
    }

    public AuthenticationCallback<AuthenticationResult> getAdalCallback() {
        return new AuthenticationCallback<AuthenticationResult>() {
            public void onSuccess(AuthenticationResult authenticationResult) {
                System.out.println("ADAL sign in success");
                boolean unused = ActiveDirectorySignIn.this.mResultObtained = true;
                String unused2 = ActiveDirectorySignIn.this.mAccessToken = authenticationResult.getAccessToken();
                String unused3 = ActiveDirectorySignIn.this.mIdentityToken = authenticationResult.getIdToken();
                String unused4 = ActiveDirectorySignIn.this.mLastError = "";
                boolean unused5 = ActiveDirectorySignIn.this.mDialogOpen = false;
                String unused6 = ActiveDirectorySignIn.this.mUserId = authenticationResult.getUserInfo().getUserId();
                ActiveDirectorySignIn.this.nativeOnDataChanged();
            }

            public void onError(Exception e) {
                System.out.println("ADAL sign in error: " + e.getMessage());
                boolean unused = ActiveDirectorySignIn.this.mResultObtained = false;
                if (!(e instanceof AuthenticationCancelError)) {
                    String unused2 = ActiveDirectorySignIn.this.mLastError = e.getMessage();
                }
                boolean unused3 = ActiveDirectorySignIn.this.mDialogOpen = false;
                String unused4 = ActiveDirectorySignIn.this.mUserId = "";
                ActiveDirectorySignIn.this.nativeOnDataChanged();
            }
        };
    }
}