package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.jobs.JobSignIn;
import com.microsoft.xbox.idp.jobs.MSAJob;

public class MSAFragment extends BaseFragment implements com.microsoft.xbox.idp.jobs.MSAJob.Callbacks {
    static final boolean $assertionsDisabled = (!MSAFragment.class.desiredAssertionStatus());
    public static final String ARG_SECURITY_POLICY = "ARG_SECURITY_POLICY";
    public static final String ARG_SECURITY_SCOPE = "ARG_SECURITY_SCOPE";
    private static final String KEY_STATE = "KEY_STATE";
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status, String cid, Ticket ticket) {
        }
    };
    private static final String TAG = MSAFragment.class.getSimpleName();
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private JobSignIn currentJob;
    private State state;

    public interface Callbacks {
        void onComplete(Status status, String str, Ticket ticket);
    }

    private static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        public String cid;

        public State() {
        }

        protected State(Parcel in) {
            this.cid = in.readString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cid);
        }
    }

    public enum Status {
        SUCCESS,
        ERROR,
        PROVIDER_ERROR
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ($assertionsDisabled || (activity instanceof Callbacks)) {
            this.callbacks = (Callbacks) activity;
            return;
        }
        throw new AssertionError();
    }

    public void onDetach() {
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        if (savedInstanceState != null) {
            this.state = (State) savedInstanceState.getParcelable(KEY_STATE);
            this.currentJob = new JobSignIn(getActivity(), this, extras.getString("ARG_SECURITY_SCOPE"), extras.getString("ARG_SECURITY_POLICY"));
        } else if (extras == null) {
            Log.e(TAG, "Intent has no extras");
            this.callbacks.onComplete(Status.ERROR, null, null);
        } else {
            String scope = extras.getString("ARG_SECURITY_SCOPE");
            if (scope == null) {
                Log.e(TAG, "No security scope");
                this.callbacks.onComplete(Status.ERROR, null, null);
                return;
            }
            String policy = extras.getString("ARG_SECURITY_POLICY");
            if (policy == null) {
                Log.e(TAG, "No security policy");
                this.callbacks.onComplete(Status.ERROR, null, null);
                return;
            }
            this.state = new State();
            this.currentJob = new JobSignIn(getActivity(), this, scope, policy).start();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_busy, container, false);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, this.state);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode + ", extras: " + (data == null ? null : data.getExtras()));
        if (this.currentJob != null) {
            this.currentJob.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onUiNeeded(MSAJob job) {
        Log.e(TAG, "Must show UI to acquire an account. Should not be here");
        this.callbacks.onComplete(Status.ERROR, null, null);
    }

    public void onFailure(MSAJob job, Exception e) {
        Log.d(TAG, "There was a problem acquiring an account: " + e);
        this.callbacks.onComplete(e instanceof NetworkException ? Status.PROVIDER_ERROR : Status.ERROR, null, null);
    }

    public void onUserCancel(MSAJob job) {
        Log.d(TAG, "The user cancelled the UI to acquire a ticket.");
        this.callbacks.onComplete(Status.ERROR, null, null);
    }

    public void onSignedOut(MSAJob job) {
        Log.d(TAG, "Signed out during sing in - should not be here.");
        this.callbacks.onComplete(Status.ERROR, null, null);
    }

    public void onAccountAcquired(MSAJob job, UserAccount userAccount) {
        this.state.cid = userAccount.getCid();
    }

    public void onTicketAcquired(MSAJob job, Ticket ticket) {
        this.callbacks.onComplete(Status.SUCCESS, this.state.cid, ticket);
    }
}
