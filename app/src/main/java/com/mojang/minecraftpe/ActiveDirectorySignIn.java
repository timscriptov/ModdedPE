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

import java.io.PrintStream;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ActiveDirectorySignIn implements ActivityListener {
    private final boolean mIsActivityListening = false;
    public String mAccessToken;
    public AuthenticationContext mAuthenticationContext;
    public boolean mDialogOpen = false;
    public String mIdentityToken;
    public String mLastError;
    public boolean mResultObtained = false;
    public String mUserId;
    public boolean z = false;

    public ActiveDirectorySignIn() {
        MainActivity.mInstance.addListener(this);
    }

    @Contract(" -> new")
    public static @NotNull ActiveDirectorySignIn createActiveDirectorySignIn() {
        return new ActiveDirectorySignIn();
    }

    public native void nativeOnDataChanged();

    public void onDestroy() {
    }

    public void onResume() {
    }

    public void onStop() {
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

    public void onActivityResult(int i, int i2, Intent intent) {
        AuthenticationContext authenticationContext = this.mAuthenticationContext;
        if (authenticationContext != null) {
            authenticationContext.onActivityResult(i, i2, intent);
        }
    }

    public void authenticate(int i) {
        this.mResultObtained = false;
        this.mDialogOpen = true;
        final PromptBehavior promptBehavior = i == 0 ? PromptBehavior.Always : PromptBehavior.Auto;
        if (i == 2) {
            z = true;
        }
        MainActivity.mInstance.runOnUiThread(() -> {
            AuthenticationContext unused = mAuthenticationContext = new AuthenticationContext(MainActivity.mInstance, "https://login.windows.net/common", true);
            if (z) {
                mAuthenticationContext.acquireTokenSilent("https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", mUserId, ActiveDirectorySignIn.this.getAdalCallback());
            } else {
                mAuthenticationContext.acquireToken(MainActivity.mInstance, "https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", "urn:ietf:wg:oauth:2.0:oob", "", promptBehavior, "", getAdalCallback());
            }
        });
    }

    public void clearCookies() {
        CookieManager instance = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            instance.removeAllCookies(null);
            instance.flush();
            return;
        }
        CookieSyncManager createInstance = CookieSyncManager.createInstance(MainActivity.mInstance);
        createInstance.startSync();
        instance.removeAllCookie();
        createInstance.stopSync();
        createInstance.sync();
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

            public void onError(Exception exc) {
                PrintStream printStream = System.out;
                printStream.println("ADAL sign in error: " + exc.getMessage());
                boolean unused = mResultObtained = false;
                if (!(exc instanceof AuthenticationCancelError)) {
                    String unused2 = mLastError = exc.getMessage();
                }
                boolean unused3 = mDialogOpen = false;
                String unused4 = mUserId = "";
                nativeOnDataChanged();
            }
        };
    }
}