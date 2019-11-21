package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import com.microsoft.xbox.idp.toolkit.FinishSignInLoader;
import com.microsoft.xbox.idp.toolkit.FinishSignInLoader.Result;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.ResultLoaderInfo;

public class FinishSignInFragment extends BaseFragment implements ActivityContext {
    static final boolean $assertionsDisabled = (!FinishSignInFragment.class.desiredAssertionStatus());
    public static final String ARG_AUTH_STATUS = "ARG_AUTH_STATUS";
    public static final String ARG_CID = "ARG_CID";
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_FINISH_SIGN_IN = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status) {
        }
    };
    public static final String TAG = FinishSignInFragment.class.getSimpleName();
    public Callbacks callbacks = NO_OP_CALLBACKS;
    private final LoaderCallbacks<Result> finishSignInCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int id, Bundle args) {
            Log.d(FinishSignInFragment.TAG, "Creating LOADER_FINISH_SIGN_IN");
            return new FinishSignInLoader(FinishSignInFragment.this.getActivity(), AuthFlowScreenStatus.valueOf(args.getString(FinishSignInFragment.ARG_AUTH_STATUS)), args.getString(FinishSignInFragment.ARG_CID), CacheUtil.getResultCache(Result.class), args.get(ErrorHelper.KEY_RESULT_KEY));
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            Log.d(FinishSignInFragment.TAG, "LOADER_FINISH_SIGN_IN finished");
            if (result.hasError()) {
                Log.d(FinishSignInFragment.TAG, "LOADER_FINISH_SIGN_IN: " + result.getError());
                FinishSignInFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            FinishSignInFragment.this.callbacks.onComplete(Status.SUCCESS);
        }

        public void onLoaderReset(Loader<Result> loader) {
            Log.d(FinishSignInFragment.TAG, "LOADER_FINISH_SIGN_IN reset");
        }
    };
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray<>();
    public State state;

    public interface Callbacks {
        void onComplete(Status status);
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
        public ErrorHelper errorHelper;

        public State() {
            this.errorHelper = new ErrorHelper();
        }

        protected State(Parcel in) {
            this.errorHelper = (ErrorHelper) in.readParcelable(ErrorHelper.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.errorHelper, flags);
        }
    }

    public enum Status {
        SUCCESS,
        ERROR,
        PROVIDER_ERROR
    }

    public FinishSignInFragment() {
        this.loaderMap.put(1, new ResultLoaderInfo(Result.class, this.finishSignInCallbacks));
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
        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "No arguments");
            this.callbacks.onComplete(Status.ERROR);
        } else if (!args.containsKey(ARG_AUTH_STATUS)) {
            Log.e(TAG, "No ARG_AUTH_STATUS");
            this.callbacks.onComplete(Status.ERROR);
        } else if (!args.containsKey(ARG_CID)) {
            Log.e(TAG, "No ARG_CID");
            this.callbacks.onComplete(Status.ERROR);
        } else {
            this.state = savedInstanceState == null ? new State() : (State) savedInstanceState.getParcelable(KEY_STATE);
            this.state.errorHelper.setActivityContext(this);
        }
    }

    public void onResume() {
        super.onResume();
        Bundle args = new Bundle(getArguments());
        Log.d(TAG, "Initializing LOADER_FINISH_SIGN_IN");
        Bundle bundle = new Bundle(args);
        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(FinishSignInFragment.class, 1));
        if (this.state.errorHelper != null) {
            this.state.errorHelper.initLoader(1, bundle, false);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, this.state);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult result = this.state.errorHelper.getActivityResult(requestCode, resultCode, data);
        if (result == null) {
            return;
        }
        if (result.isTryAgain()) {
            Log.d(TAG, "Trying again");
            this.state.errorHelper.deleteLoader();
            return;
        }
        this.state.errorHelper = null;
        this.callbacks.onComplete(Status.PROVIDER_ERROR);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_busy, container, false);
    }

    public LoaderInfo getLoaderInfo(int loaderId) {
        return (LoaderInfo) this.loaderMap.get(loaderId);
    }
}
