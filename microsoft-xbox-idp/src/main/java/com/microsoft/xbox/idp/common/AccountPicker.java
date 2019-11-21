package com.microsoft.xbox.idp.common;

import android.content.Context;
import android.content.Intent;

import com.microsoft.xbox.idp.ui.AuthFlowActivity;
import com.microsoft.xbox.idp.ui.SignOutActivity;

public class AccountPicker {
    public static Intent newSignInIntent(Context context, String securityScope, String securityPolicy) {
        Intent intent = new Intent(context, AuthFlowActivity.class);
        intent.putExtra("ARG_SECURITY_SCOPE", securityScope);
        intent.putExtra("ARG_SECURITY_POLICY", securityPolicy);
        return intent;
    }

    public static Intent newSignInIntent(Context context, String securityScope, String securityPolicy, String altButtonText) {
        Intent intent = new Intent(context, AuthFlowActivity.class);
        intent.putExtra("ARG_SECURITY_SCOPE", securityScope);
        intent.putExtra("ARG_SECURITY_POLICY", securityPolicy);
        intent.putExtra("ARG_ALT_BUTTON_TEXT", altButtonText);
        return intent;
    }

    public static Intent newSignOutIntent(Context context) {
        return new Intent(context, SignOutActivity.class);
    }
}
