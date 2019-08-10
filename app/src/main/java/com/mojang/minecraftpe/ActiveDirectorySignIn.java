package com.mojang.minecraftpe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationCancelError;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;

public class ActiveDirectorySignIn implements ActivityListener {
    
    public String mAccessToken;
    public AuthenticationContext mAuthenticationContext;
    public boolean mDialogOpen = false;
    public String mIdentityToken;
    public String mLastError;
    public boolean mResultObtained = false;

    public ActiveDirectorySignIn() {
        MainActivity.mInstance.addListener(this);
    }

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

    public void onDestroy() {
    }

    public void authenticate(boolean attemptSSO) {
        this.mResultObtained = false;
        this.mDialogOpen = true;
        final PromptBehavior promptBehavior = attemptSSO ? PromptBehavior.Auto : PromptBehavior.Always;
        MainActivity.mInstance.runOnUiThread(new Runnable() {
            public void run() {
                mAuthenticationContext = new AuthenticationContext((Context) MainActivity.mInstance, "https://login.windows.net/common", true);
                AuthenticationContext access$000 = mAuthenticationContext;
                MainActivity mainActivity = MainActivity.mInstance;
                access$000.acquireToken((Activity) mainActivity, "https://meeservices.minecraft.net", "b36b1432-1a1c-4c82-9b76-24de1cab42f2", "urn:ietf:wg:oauth:2.0:oob", "", promptBehavior, "", getAdalCallback());
            }
        });
    }

    public static ActiveDirectorySignIn createActiveDirectorySignIn() {
        return new ActiveDirectorySignIn();
    }

    public AuthenticationCallback<AuthenticationResult> getAdalCallback() {
        return new AuthenticationCallback<AuthenticationResult>() {
            public void onSuccess(AuthenticationResult authenticationResult) {
                System.out.println("ADAL sign in success");
                mResultObtained = true;
                mAccessToken = authenticationResult.getAccessToken();
                mIdentityToken = authenticationResult.getIdToken();
                mLastError = "";
                mDialogOpen = false;
            }

            public void onError(Exception e) {
                System.out.println("ADAL sign in error: " + e.getMessage());
                mResultObtained = false;
                if (!(e instanceof AuthenticationCancelError)) {
                    mLastError = e.getMessage();
                }
                mDialogOpen = false;
            }
        };
    }
}
