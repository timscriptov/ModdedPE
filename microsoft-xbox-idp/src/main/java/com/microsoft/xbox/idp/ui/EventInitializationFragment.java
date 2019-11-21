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
import com.microsoft.xbox.idp.toolkit.EventInitializationLoader;
import com.microsoft.xbox.idp.toolkit.EventInitializationLoader.Result;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.ResultLoaderInfo;

public class EventInitializationFragment extends BaseFragment implements ActivityContext {
    static final boolean $assertionsDisabled = (!EventInitializationFragment.class.desiredAssertionStatus());
    public static final String ARG_RPS_TICKET = "ARG_RPS_TICKET";
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_EVENT_INITIALIZATION = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status) {
        }
    };
    public static final String TAG = EventInitializationFragment.class.getSimpleName();
    public Callbacks callbacks = NO_OP_CALLBACKS;
    private final LoaderCallbacks<Result> eventLoaderCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int id, Bundle args) {
            Log.d(EventInitializationFragment.TAG, "Creating LOADER_EVENT_INITIALIZATION");
            return new EventInitializationLoader(EventInitializationFragment.this.getActivity(), args.getLong("ARG_USER_PTR"), args.getString("ARG_RPS_TICKET"), CacheUtil.getResultCache(Result.class), args.get(ErrorHelper.KEY_RESULT_KEY));
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            Log.d(EventInitializationFragment.TAG, "LOADER_EVENT_INITIALIZATION finished");
            if (result.hasError()) {
                Log.d(EventInitializationFragment.TAG, "LOADER_EVENT_INITIALIZATION: " + result.getError());
                EventInitializationFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            EventInitializationFragment.this.callbacks.onComplete(Status.SUCCESS);
        }

        public void onLoaderReset(Loader<Result> loader) {
            Log.d(EventInitializationFragment.TAG, "LOADER_EVENT_INITIALIZATION reset");
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

    public EventInitializationFragment() {
        this.loaderMap.put(1, new ResultLoaderInfo(Result.class, this.eventLoaderCallbacks));
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
        } else if (!args.containsKey("ARG_USER_PTR")) {
            Log.e(TAG, "No ARG_USER_PTR");
            this.callbacks.onComplete(Status.ERROR);
        } else if (!args.containsKey("ARG_RPS_TICKET")) {
            Log.e(TAG, "No ARG_RPS_TICKET");
            this.callbacks.onComplete(Status.ERROR);
        } else {
            this.state = savedInstanceState == null ? new State() : (State) savedInstanceState.getParcelable(KEY_STATE);
            this.state.errorHelper.setActivityContext(this);
        }
    }

    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        Log.d(TAG, "Initializing LOADER_XB_LOGIN");
        Bundle bundle = new Bundle(args);
        bundle.putLong("ARG_USER_PTR", args.getLong("ARG_USER_PTR"));
        bundle.putString("ARG_RPS_TICKET", args.getString("ARG_RPS_TICKET"));
        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(EventInitializationFragment.class, 1));
        if (this.state.errorHelper != null) {
            this.state.errorHelper.initLoader(1, bundle);
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
