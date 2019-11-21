package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequest.Extras;
import com.microsoft.onlineid.internal.ApiRequestResultReceiver;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Intents.DataBuilder;
import com.microsoft.onlineid.internal.MsaService;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class SignOutActivity extends Activity {
    private boolean _isSignedOutOfThisAppOnly;
    private ActivityResultSender _resultSender;
    private String _userCid;

    private class SignOutResultReceiver extends ApiRequestResultReceiver {
        public SignOutResultReceiver() {
            super(new Handler());
        }

        protected void onSuccess(ApiResult result) {
            SignOutActivity.this._resultSender.putSignedOutCid(SignOutActivity.this._userCid, SignOutActivity.this._isSignedOutOfThisAppOnly).set();
            SignOutActivity.this.finish();
        }

        protected void onUserCancel() {
            SignOutActivity.this.finish();
        }

        protected void onFailure(Exception e) {
            SignOutActivity.this._resultSender.putException(e).set();
            SignOutActivity.this.finish();
        }

        protected void onUINeeded(PendingIntent intent) {
            onFailure(new UnsupportedOperationException("onUINeeded not expected for sign out request."));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        this._resultSender = new ActivityResultSender(this, ResultType.Account);
        this._userCid = getIntent().getStringExtra(BundleMarshaller.UserCidKey);
        buildDialog().show();
    }

    private AlertDialog buildDialog() {
        final Context applicationContext = getApplicationContext();
        final ApiRequest originalSignOutRequest = new ApiRequest(applicationContext, getIntent());
        Resources resources = new Resources(applicationContext);
        RelativeLayout customContentView = (RelativeLayout) getLayoutInflater().inflate(resources.getLayout("sign_out_custom_content_view"), null);
        final CheckBox checkBox = (CheckBox) customContentView.findViewById(resources.getId("signOutCheckBox"));
        checkBox.setText(String.format(resources.getString("sign_out_dialog_checkbox"), new Object[]{originalSignOutRequest.getAccountName()}));
        OnClickListener signOutOnClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String puid = originalSignOutRequest.getAccountPuid();
                String action = checkBox.isChecked() ? MsaService.ActionSignOutAllApps : MsaService.ActionSignOut;
                SignOutActivity.this._isSignedOutOfThisAppOnly = !checkBox.isChecked();
                new ApiRequest(applicationContext, new Intent(applicationContext, MsaService.class).setAction(action)).setAccountPuid(puid).setResultReceiver(new SignOutResultReceiver()).executeAsync();
                dialog.dismiss();
            }
        };
        OnClickListener cancelOnClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        OnCancelListener onCancelListener = new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                SignOutActivity.this.finish();
            }
        };
        Builder builder = new Builder(this);
        builder.setView(customContentView).setTitle(resources.getString("sign_out_dialog_title")).setPositiveButton(resources.getString("sign_out_dialog_button_sign_out"), signOutOnClickListener).setNegativeButton(resources.getString("sign_out_dialog_button_cancel"), cancelOnClickListener).setOnCancelListener(onCancelListener);
        return builder.create();
    }

    public static Intent getSignOutIntent(Context applicationContext, String accountPuid, String accountCid, String accountName, Bundle callerState) {
        return new Intent(applicationContext, SignOutActivity.class).putExtra(Extras.AccountPuid.getKey(), accountPuid).putExtra(Extras.AccountName.getKey(), accountName).putExtra(BundleMarshaller.UserCidKey, accountCid).putExtra(BundleMarshaller.ClientStateBundleKey, callerState).setData(new DataBuilder().add(accountPuid).add(accountName).build());
    }
}
