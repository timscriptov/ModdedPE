package com.mojang.minecraftpe;

import android.content.Intent;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationCancelError;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;

public class ActiveDirectorySignIn implements ActivityListener {
    private String mAccessToken;
    private AuthenticationContext mAuthenticationContext;
    private boolean mDialogOpen = false;
    private String mIdentityToken;
    private String mLastError;
    private boolean mResultObtained = false;
    private String mUserId;

    private native void nativeOnDataChanged();

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
        MainActivity.mInstance.runOnUiThread(new Runnable() {
            public void run() {
                String resourceId = "https://meeservices.minecraft.net";
                String redirectUri = "urn:ietf:wg:oauth:2.0:oob";
                String clientId = "b36b1432-1a1c-4c82-9b76-24de1cab42f2";
                mAuthenticationContext = new AuthenticationContext(MainActivity.mInstance, "https://login.windows.net/common", true);
                if (doRefresh) {
                    mAuthenticationContext.acquireTokenSilent(resourceId, clientId, mUserId, getAdalCallback());
                } else {
                    mAuthenticationContext.acquireToken(MainActivity.mInstance, resourceId, clientId, redirectUri, "", promptBehavior, "", getAdalCallback());
                }
            }
        });
    }

    public static ActiveDirectorySignIn createActiveDirectorySignIn() {
        return new ActiveDirectorySignIn();
    }

    private AuthenticationCallback<AuthenticationResult> getAdalCallback() {
        return new AuthenticationCallback<AuthenticationResult>() {
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

            public void onError(Exception e) {
                System.out.println("ADAL sign in error: " + e.getMessage());
                mResultObtained = false;
                if (!(e instanceof AuthenticationCancelError)) {
                    mLastError = e.getMessage();
                }
                mDialogOpen = false;
                mUserId = "";
                nativeOnDataChanged();
            }
        };
    }
}
