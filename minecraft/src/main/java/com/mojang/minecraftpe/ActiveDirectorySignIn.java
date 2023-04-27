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
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
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
        mResultObtained = false;
        mDialogOpen = true;
        final PromptBehavior promptBehavior = i == 0 ? PromptBehavior.Always : PromptBehavior.Auto;
        if (i == 2) {
            z = true;
        }
        MainActivity.mInstance.runOnUiThread(() -> {
            mAuthenticationContext = new AuthenticationContext(MainActivity.mInstance, "https://login.windows.net/common", true);
            if (z) {
                mAuthenticationContext.acquireTokenSilent("https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", mUserId, ActiveDirectorySignIn.this.getAdalCallback());
            } else {
                mAuthenticationContext.acquireToken(MainActivity.mInstance, "https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", "urn:ietf:wg:oauth:2.0:oob", "", promptBehavior, "", getAdalCallback());
            }
        });
    }

    public void clearCookies() {
        CookieManager instance = CookieManager.getInstance();
        instance.removeAllCookies(null);
        instance.flush();
    }

    public AuthenticationCallback<AuthenticationResult> getAdalCallback() {
        return new AuthenticationCallback<>() {
            public void onSuccess(AuthenticationResult authenticationResult) {
                System.out.println("ADAL sign in success");
                mResultObtained = true;
                mAccessToken = authenticationResult.getAccessToken();
                mIdentityToken = authenticationResult.getIdToken();
                mLastError = "";
                mDialogOpen = false;
                mUserId = authenticationResult.getUserInfo().getUserId();
                nativeOnDataChanged();
            }

            public void onError(Exception exc) {
                PrintStream printStream = System.out;
                printStream.println("ADAL sign in error: " + exc.getMessage());
                mResultObtained = false;
                if (!(exc instanceof AuthenticationCancelError)) {
                    mLastError = exc.getMessage();
                }
                mDialogOpen = false;
                mUserId = "";
                nativeOnDataChanged();
            }
        };
    }
}
