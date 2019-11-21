package com.microsoft.xbox.idp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.Profile;
import com.microsoft.xbox.idp.model.Profile.Response;
import com.microsoft.xbox.idp.model.Profile.SettingId;
import com.microsoft.xbox.idp.model.Profile.User;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCIntroducing;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.toolkit.BitmapLoader.Result;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.BitmapLoaderInfo;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.idp.util.HttpUtil.ImageSize;
import com.microsoft.xbox.idp.util.ObjectLoaderInfo;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

public class IntroducingFragment extends BaseFragment implements OnClickListener, ActivityContext {
    static final boolean $assertionsDisabled = (!IntroducingFragment.class.desiredAssertionStatus());
    private static final String KEY_STATE = "IntroducingFragment.KEY_STATE";
    private static final int LOADER_GAMER_IMAGE = 2;
    private static final int LOADER_GAMER_PROFILE = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onCloseWithStatus(Status status) {
        }
    };
    public static final String TAG = IntroducingFragment.class.getSimpleName();
    private final LoaderCallbacks<Result> bitmapCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int id, Bundle args) {
            Log.d(IntroducingFragment.TAG, "Creating LOADER_GAMER_IMAGE");
            String strUri = args.getString(ErrorHelper.KEY_RESULT_KEY);
            Uri uri = HttpUtil.getImageSizeUrlParams(Uri.parse(strUri).buildUpon(), ImageSize.MEDIUM).build();
            Log.d(IntroducingFragment.TAG, "uri: " + uri);
            return new BitmapLoader(IntroducingFragment.this.getActivity(), CacheUtil.getBitmapCache(), strUri, uri.toString());
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            if (result.hasException()) {
                UTCError.trackException(result.getException(), "Introducing view");
            }
            Log.d(IntroducingFragment.TAG, "Finished LOADER_GAMER_IMAGE");
            IntroducingFragment.this.gamerpicView.setImageBitmap((Bitmap) result.getData());
        }

        public void onLoaderReset(Loader<Result> loader) {
            IntroducingFragment.this.gamerpicView.setImageBitmap(null);
        }
    };
    public View bottomBarShadow;
    private Callbacks callbacks = NO_OP_CALLBACKS;
    public TextView displayNameText;
    public TextView gamerTagText;
    public ImageView gamerpicView;
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray<>();
    private final LoaderCallbacks<ObjectLoader.Result<Response>> profileCallbacks = new LoaderCallbacks<ObjectLoader.Result<Response>>() {
        public Loader<ObjectLoader.Result<Response>> onCreateLoader(int id, Bundle args) {
            Log.d(IntroducingFragment.TAG, "Creating LOADER_GAMER_PROFILE");
            return new ObjectLoader(IntroducingFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), args.get(ErrorHelper.KEY_RESULT_KEY), Response.class, Profile.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall("GET", EndpointsFactory.get().profile(), "/users/me/profile/settings?settings=" + (SettingId.GameDisplayPicRaw + "," + SettingId.Gamerscore + "," + SettingId.Gamertag + "," + SettingId.FirstName + "," + SettingId.LastName)), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION));
        }

        public void onLoadFinished(Loader<ObjectLoader.Result<Response>> loader, ObjectLoader.Result<Response> result) {
            Log.d(IntroducingFragment.TAG, "Finished LOADER_GAMER_PROFILE");
            if (!result.hasData() || ((Response) result.getData()).profileUsers == null || ((Response) result.getData()).profileUsers.length <= 0) {
                UTCError.trackServiceFailure("Service Error - Load Profile", "Introducing view", result.getError());
                Log.e(IntroducingFragment.TAG, "No gamer profile data");
                IntroducingFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            Log.e(IntroducingFragment.TAG, "Got gamer profile data");
            IntroducingFragment.this.user = ((Response) result.getData()).profileUsers[0];
            UTCIntroducing.trackPageView(IntroducingFragment.this.user, IntroducingFragment.this.getActivityTitle());
            IntroducingFragment.this.displayNameText.setText(IntroducingFragment.this.getString(R.string.xbid_first_and_last_name_android, new Object[]{IntroducingFragment.this.user.settings.get(SettingId.FirstName), IntroducingFragment.this.user.settings.get(SettingId.LastName)}));
            IntroducingFragment.this.gamerTagText.setText((CharSequence) IntroducingFragment.this.user.settings.get(SettingId.Gamertag));
            if (!TextUtils.isEmpty((CharSequence) IntroducingFragment.this.user.settings.get(SettingId.GameDisplayPicRaw))) {
                Bundle bundle = new Bundle();
                bundle.putString(ErrorHelper.KEY_RESULT_KEY, (String) IntroducingFragment.this.user.settings.get(SettingId.GameDisplayPicRaw));
                IntroducingFragment.this.state.errorHelper.initLoader(2, bundle);
            }
        }

        public void onLoaderReset(Loader<ObjectLoader.Result<Response>> loader) {
        }
    };
    public ScrollView scrollView;
    public State state;
    public User user;

    public interface Callbacks {
        void onCloseWithStatus(Status status);
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
        NO_ERROR,
        ERROR_USER_CANCEL,
        PROVIDER_ERROR
    }

    public IntroducingFragment() {
        this.loaderMap.put(1, new ObjectLoaderInfo(this.profileCallbacks));
        this.loaderMap.put(2, new BitmapLoaderInfo(this.bitmapCallbacks));
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
        UTCPageView.removePage();
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_introducing, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.state = savedInstanceState == null ? new State() : (State) savedInstanceState.getParcelable(KEY_STATE);
        this.state.errorHelper.setActivityContext(this);
        this.scrollView = (ScrollView) view.findViewById(R.id.xbid_scroll_container);
        this.bottomBarShadow = view.findViewById(R.id.xbid_bottom_bar_shadow);
        this.scrollView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @SuppressLint("WrongConstant")
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                IntroducingFragment.this.bottomBarShadow.setVisibility(UiUtil.canScroll(IntroducingFragment.this.scrollView) ? 0 : 4);
            }
        });
        this.gamerpicView = (ImageView) view.findViewById(R.id.xbid_gamerpic);
        this.displayNameText = (TextView) view.findViewById(R.id.xbid_display_name);
        this.gamerTagText = (TextView) view.findViewById(R.id.xbid_gamertag);
        Button doneButton = (Button) view.findViewById(R.id.xbid_done);
        doneButton.setOnClickListener(this);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("ARG_LOG_IN_BUTTON_TEXT")) {
                doneButton.setText(args.getString("ARG_LOG_IN_BUTTON_TEXT"));
            }
            if (args.containsKey("ARG_ALT_BUTTON_TEXT")) {
                doneButton.setText(args.getString("ARG_ALT_BUTTON_TEXT"));
            }
        }
    }

    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            Log.d(TAG, "Initializing LOADER_GAMER_PROFILE");
            Bundle bundle = new Bundle(args);
            bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(IntroducingFragment.class, 1));
            if (this.state.errorHelper != null) {
                this.state.errorHelper.initLoader(1, bundle);
                return;
            }
            return;
        }
        Log.e(TAG, "No arguments provided");
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
        Log.d(TAG, "onActivityResult");
        this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.xbid_done) {
            UTCIntroducing.trackDone(this.user, getActivityTitle());
            this.callbacks.onCloseWithStatus(Status.NO_ERROR);
        }
    }

    public LoaderInfo getLoaderInfo(int loaderId) {
        return (LoaderInfo) this.loaderMap.get(loaderId);
    }
}
