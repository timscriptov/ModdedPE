package com.microsoft.xbox.idp.jobs;

import android.app.PendingIntent;
import android.content.Context;
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

public class JobSilentSignIn extends MSAJob {
    public static final String TAG = JobSilentSignIn.class.getSimpleName();
    private final IAccountCallback accountCallback = new IAccountCallback() {
        public void onAccountAcquired(UserAccount userAccount, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "accountCallback.onAccountAcquired");
            UTCSignin.trackAccountAcquired(JobSilentSignIn.TAG, userAccount.getCid(), true);
            JobSilentSignIn.this.callbacks.onAccountAcquired(JobSilentSignIn.this, userAccount);
            userAccount.getTicket(new SecurityScope(JobSilentSignIn.this.scope, JobSilentSignIn.this.policy), bundle);
        }

        public void onAccountSignedOut(String id, boolean thisAppOnly, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "accountCallback.onAccountSignedOut");
            UTCError.trackSignedOut(JobSilentSignIn.TAG, true, CallBackSources.Account);
            UTCUser.trackSignout(JobSilentSignIn.this.activityTitle);
            JobSilentSignIn.this.callbacks.onSignedOut(JobSilentSignIn.this);
        }

        public void onUINeeded(PendingIntent pendingIntent, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "accountCallback.onUINeeded");
            UTCError.trackUINeeded(JobSilentSignIn.TAG, true, CallBackSources.Account);
            JobSilentSignIn.this.callbacks.onUiNeeded(JobSilentSignIn.this);
        }

        public void onFailure(AuthenticationException e, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "accountCallback.onFailure: " + e.getMessage());
            UTCError.trackFailure(JobSilentSignIn.TAG, true, CallBackSources.Account, (Exception) e);
            JobSilentSignIn.this.callbacks.onFailure(JobSilentSignIn.this, e);
        }

        public void onUserCancel(Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "accountCallback.onUserCancel");
            UTCError.trackMSACancel(JobSilentSignIn.TAG, true, CallBackSources.Account);
            JobSilentSignIn.this.callbacks.onUserCancel(JobSilentSignIn.this);
        }
    };
    public final CharSequence activityTitle;
    public final String cid;
    public final String policy;
    public final String scope;
    private final ITicketCallback ticketCallback = new ITicketCallback() {
        public void onTicketAcquired(Ticket ticket, UserAccount userAccount, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "ticketCallback.onTicketAcquired");
            if (userAccount != null) {
                UTCSignin.trackTicketAcquired(JobSilentSignIn.TAG, userAccount.getCid(), true);
                UTCSignin.trackMSASigninSuccess(userAccount.getCid(), true, JobSilentSignIn.this.activityTitle);
            } else {
                UTCSignin.trackTicketAcquired(JobSilentSignIn.TAG, JobSilentSignIn.this.cid, true);
                UTCSignin.trackMSASigninSuccess(JobSilentSignIn.this.cid, true, JobSilentSignIn.this.activityTitle);
            }
            JobSilentSignIn.this.callbacks.onTicketAcquired(JobSilentSignIn.this, ticket);
        }

        public void onUINeeded(PendingIntent pendingIntent, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "ticketCallback.onUINeeded");
            UTCError.trackUINeeded(JobSilentSignIn.TAG, true, CallBackSources.Ticket);
            JobSilentSignIn.this.callbacks.onUiNeeded(JobSilentSignIn.this);
        }

        public void onFailure(AuthenticationException e, Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "ticketCallback.onFailure: " + e.getMessage());
            UTCError.trackFailure(JobSilentSignIn.TAG, true, CallBackSources.Ticket, (Exception) e);
            JobSilentSignIn.this.callbacks.onFailure(JobSilentSignIn.this, e);
        }

        public void onUserCancel(Bundle bundle) {
            Log.d(JobSilentSignIn.TAG, "ticketCallback.onUserCancel");
            UTCError.trackMSACancel(JobSilentSignIn.TAG, true, CallBackSources.Ticket);
            JobSilentSignIn.this.callbacks.onUserCancel(JobSilentSignIn.this);
        }
    };

    public JobSilentSignIn(Context context, CharSequence activityTitle2, Callbacks callbacks, String scope2, String policy2, String cid2) {
        super(context.getApplicationContext(), callbacks);
        this.activityTitle = activityTitle2;
        this.scope = scope2;
        this.policy = policy2;
        this.cid = cid2;
        this.accountManager.setAccountCallback(this.accountCallback);
        this.accountManager.setTicketCallback(this.ticketCallback);
    }

    public Type getType() {
        return Type.SILENT_SIGN_IN;
    }

    public JobSilentSignIn start() {
        this.accountManager.getAccountById(this.cid, null);
        UTCSignin.trackSignin(this.cid, true, this.activityTitle);
        UTCSignin.trackMSASigninStart(this.cid, true, this.activityTitle);
        return this;
    }
}
