package com.microsoft.xbox.idp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;
import com.microsoft.xbox.idp.ui.HeaderFragment.Callbacks;
import com.microsoft.xbox.idp.ui.SignOutFragment.Status;

public class SignOutActivity extends AuthActivity implements Callbacks, SignOutFragment.Callbacks {
    private static final String KEY_STATE = "KEY_STATE";
    private static final String TAG = SignOutActivity.class.getSimpleName();
    private State state;
    private AuthFlowScreenStatus status = AuthFlowScreenStatus.NO_ERROR;

    private static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        public Task currentTask;

        public State() {
        }

        protected State(Parcel in) {
            int taskId = in.readInt();
            if (taskId != -1) {
                this.currentTask = Task.values()[taskId];
            }
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.currentTask == null ? -1 : this.currentTask.ordinal());
        }
    }

    private enum Task {
        SIGN_OUT
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xbid_activity_auth_flow);
        if (savedInstanceState == null) {
            this.state = new State();
            showBodyFragment(Task.SIGN_OUT, new SignOutFragment(), new Bundle(), true);
            return;
        }
        this.state = (State) savedInstanceState.getParcelable(KEY_STATE);
    }

    public void onDestroy() {
        UTCPageView.removePage();
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, this.state);
    }

    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
        finishWithResult();
    }

    public void onClickCloseHeader() {
        Log.d(TAG, "onClickCloseHeader");
        this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
        finishWithResult();
    }

    public void onComplete(Status status2) {
        Log.d(TAG, "onComplete: StartSignInFragment.Status." + status2);
        switch (status2) {
            case SUCCESS:
                this.status = AuthFlowScreenStatus.NO_ERROR;
                UTCUser.trackSignout(getTitle());
                finishWithResult();
                return;
            case ERROR:
                this.status = AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    private void finishWithResult() {
        setResult(toActivityResult(this.status));
        finishCompat();
    }

    private void showBodyFragment(Task task, Fragment bodyFragment, Bundle args, boolean showHeader) {
        this.state.currentTask = task;
        showBodyFragment(bodyFragment, args, showHeader);
    }
}
