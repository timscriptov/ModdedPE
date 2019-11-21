package com.microsoft.xbox.idp.jobs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.microsoft.onlineid.IAccountCallback;
import com.microsoft.onlineid.ITicketCallback;
import com.microsoft.onlineid.SecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCSignin;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;

public class JobSignIn extends MSAJob {
    public static final String TAG = JobSignIn.class.getSimpleName();
    private final IAccountCallback accountCallback = new IAccountCallback() {
        public void onAccountAcquired(UserAccount userAccount, Bundle bundle) {
            Log.d(JobSignIn.TAG, "accountCallback.onAccountAcquired");
            UTCSignin.trackAccountAcquired(JobSignIn.TAG, userAccount.getCid(), false);
            JobSignIn.this.callbacks.onAccountAcquired(JobSignIn.this, userAccount);
            userAccount.getTicket(new SecurityScope(JobSignIn.this.scope, JobSignIn.this.policy), bundle);
        }

        public void onAccountSignedOut(String id, boolean thisAppOnly, Bundle bundle) {
            Log.d(JobSignIn.TAG, "accountCallback.onAccountSignedOut");
            UTCUser.trackSignout(JobSignIn.this.activity.getTitle());
            JobSignIn.this.callbacks.onSignedOut(JobSignIn.this);
        }

        public void onUINeeded(PendingIntent pendingIntent, Bundle bundle) {
            Log.d(JobSignIn.TAG, "accountCallback.onUINeeded");
            UTCError.trackUINeeded(JobSignIn.TAG, false, CallBackSources.Account);
            try {
                JobSignIn.this.activity.startIntentSenderForResult(pendingIntent.getIntentSender(), 0, null, 0, 0, 0);
            } catch (SendIntentException e) {
                Log.d(JobSignIn.TAG, e.getMessage());
                JobSignIn.this.callbacks.onFailure(JobSignIn.this, e);
            }
        }

        public void onFailure(AuthenticationException e, Bundle bundle) {
            Log.d(JobSignIn.TAG, "accountCallback.onFailure: " + e.getMessage());
            UTCError.trackFailure(JobSignIn.TAG, false, CallBackSources.Account, (Exception) e);
            JobSignIn.this.callbacks.onFailure(JobSignIn.this, e);
        }

        public void onUserCancel(Bundle bundle) {
            Log.d(JobSignIn.TAG, "accountCallback.onUserCancel");
            UTCUser.trackMSACancel(JobSignIn.this.activity.getTitle(), JobSignIn.TAG, false, CallBackSources.Account);
            JobSignIn.this.callbacks.onUserCancel(JobSignIn.this);
        }
    };
    public final Activity activity;
    public final String policy;
    public final String scope;
    private final ITicketCallback ticketCallback = new ITicketCallback() {
        public void onTicketAcquired(Ticket ticket, UserAccount userAccount, Bundle bundle) {
            Log.d(JobSignIn.TAG, "ticketCallback.onTicketAcquired");
            if (userAccount != null) {
                UTCSignin.trackTicketAcquired(JobSignIn.TAG, userAccount.getCid(), false);
                UTCSignin.trackMSASigninSuccess(userAccount.getCid(), false, JobSignIn.this.activity.getTitle());
            } else {
                UTCSignin.trackTicketAcquired(JobSignIn.TAG, null, false);
                UTCSignin.trackMSASigninSuccess(null, false, JobSignIn.this.activity.getTitle());
            }
            JobSignIn.this.callbacks.onTicketAcquired(JobSignIn.this, ticket);
        }

        public void onUINeeded(PendingIntent pendingIntent, Bundle bundle) {
            Log.d(JobSignIn.TAG, "ticketCallback.onUINeeded");
            try {
                JobSignIn.this.activity.startIntentSenderForResult(pendingIntent.getIntentSender(), 0, null, 0, 0, 0);
            } catch (SendIntentException e) {
                Log.d(JobSignIn.TAG, e.getMessage());
                JobSignIn.this.callbacks.onFailure(JobSignIn.this, e);
            }
        }

        public void onFailure(AuthenticationException e, Bundle bundle) {
            Log.d(JobSignIn.TAG, "ticketCallback.onFailure: " + e.getMessage());
            UTCError.trackFailure(JobSignIn.TAG, false, CallBackSources.Ticket, (Exception) e);
            JobSignIn.this.callbacks.onFailure(JobSignIn.this, e);
        }

        public void onUserCancel(Bundle bundle) {
            Log.d(JobSignIn.TAG, "ticketCallback.onUserCancel");
            UTCUser.trackMSACancel(JobSignIn.this.activity.getTitle(), JobSignIn.TAG, false, CallBackSources.Ticket);
            JobSignIn.this.callbacks.onUserCancel(JobSignIn.this);
        }
    };

    public JobSignIn(Activity activity2, Callbacks callbacks, String scope2, String policy2) {
        super(activity2.getApplicationContext(), callbacks);
        this.activity = activity2;
        this.scope = scope2;
        this.policy = policy2;
        this.accountManager.setAccountCallback(this.accountCallback);
        this.accountManager.setTicketCallback(this.ticketCallback);
    }

    public Type getType() {
        return Type.SIGN_IN;
    }

    public JobSignIn start() {
        UTCSignin.trackSignin(null, false, this.activity.getTitle());
        UTCSignin.trackMSASigninStart(null, false, this.activity.getTitle());
        this.accountManager.getAccountPickerIntent(null, null);
        UTCSignin.trackPageView(this.activity.getTitle());
        return this;
    }
}
