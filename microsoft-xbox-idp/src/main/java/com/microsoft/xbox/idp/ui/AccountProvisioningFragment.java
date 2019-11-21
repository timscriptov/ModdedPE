package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.GsonBuilder;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.interop.XboxLiveAppConfig;
import com.microsoft.xbox.idp.model.Privacy;
import com.microsoft.xbox.idp.model.Privacy.Key;
import com.microsoft.xbox.idp.model.Privacy.Settings;
import com.microsoft.xbox.idp.model.Privacy.Value;
import com.microsoft.xbox.idp.model.Profile;
import com.microsoft.xbox.idp.model.Profile.GamerpicChangeRequest;
import com.microsoft.xbox.idp.model.Profile.GamerpicChoiceList;
import com.microsoft.xbox.idp.model.Profile.GamerpicListEntry;
import com.microsoft.xbox.idp.model.Profile.GamerpicUpdateResponse;
import com.microsoft.xbox.idp.model.UserAccount;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.Service.Errors;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Result;
import com.microsoft.xbox.idp.toolkit.XTokenLoader;
import com.microsoft.xbox.idp.toolkit.XTokenLoader.Data;
import com.microsoft.xbox.idp.ui.AccountProvisioningResult.AgeGroup;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.idp.util.ObjectLoaderInfo;
import com.microsoft.xbox.idp.util.ResultLoaderInfo;

import org.apache.http.client.methods.HttpPut;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AccountProvisioningFragment extends BaseFragment implements ActivityContext {
    static final boolean $assertionsDisabled = (!AccountProvisioningFragment.class.desiredAssertionStatus());
    private static final String GAMERPIC_UPDATE_IMAGE_URL_KEY = "GAMERPIC_UPDATE_IMAGE_URL_KEY";
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_GAMERPIC_CHOICE_LIST = 4;
    private static final int LOADER_GAMERPIC_UPDATE = 5;
    private static final int LOADER_GET_PRIVACY_SETTINGS = 6;
    private static final int LOADER_GET_PROFILE = 1;
    private static final int LOADER_POST_PROFILE = 2;
    private static final int LOADER_SET_PRIVACY_SETTINGS = 7;
    private static final int LOADER_XTOKEN = 3;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onCloseWithStatus(Status status, AccountProvisioningResult result) {
        }
    };
    public static final String TAG = AccountProvisioningFragment.class.getSimpleName();
    public Callbacks callbacks;
    private final LoaderCallbacks<Result<GamerpicChoiceList>> gamerpicChoiceListCallbacks = new LoaderCallbacks<Result<GamerpicChoiceList>>() {
        public Loader<Result<GamerpicChoiceList>> onCreateLoader(int id, Bundle args) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GAMERPIC_CHOICE_LIST");
            XboxLiveAppConfig config = new XboxLiveAppConfig();
            int titleId = config.getOverrideTitleId();
            if (titleId == 0) {
                titleId = config.getTitleId();
            }
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), args.get(ErrorHelper.KEY_RESULT_KEY), GamerpicChoiceList.class, Profile.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall("GET", "http://dlassets.xboxlive.com", String.format("/public/content/ppl/gamerpics/gamerpicsautoassign-%08X.json", new Object[]{Integer.valueOf(titleId)})), "3"));
        }

        public void onLoadFinished(Loader<Result<GamerpicChoiceList>> loader, Result<GamerpicChoiceList> result) {
            Log.d(AccountProvisioningFragment.TAG, "Finished LOADER_GAMERPIC_CHOICE_LIST");
            if (!result.hasData() || ((GamerpicChoiceList) result.getData()).gamerpics == null) {
                Log.e(AccountProvisioningFragment.TAG, "Failed to get gamerpic choice list");
                UTCError.trackServiceFailure(Errors.GamerPicChoiceList, "Welcome view", result.getError());
                Bundle bundle = new Bundle();
                bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, 6));
                AccountProvisioningFragment.this.state.errorHelper.initLoader(6, bundle);
                return;
            }
            Log.e(AccountProvisioningFragment.TAG, "Got gamerpic choice list");
            List<GamerpicListEntry> gamerPicList = ((GamerpicChoiceList) result.getData()).gamerpics;
            if (!gamerPicList.isEmpty()) {
                String gamerPicUrl = String.format("http://dlassets.xboxlive.com/public/content/ppl/gamerpics/%s-xl.png", new Object[]{((GamerpicListEntry) gamerPicList.get(new Random().nextInt(gamerPicList.size()))).id});
                Bundle gamerPicUpdateArgs = new Bundle();
                gamerPicUpdateArgs.putString(AccountProvisioningFragment.GAMERPIC_UPDATE_IMAGE_URL_KEY, gamerPicUrl);
                AccountProvisioningFragment.this.state.errorHelper.initLoader(5, gamerPicUpdateArgs);
            }
        }

        public void onLoaderReset(Loader<Result<GamerpicChoiceList>> loader) {
        }
    };
    private final LoaderCallbacks<Result<GamerpicUpdateResponse>> gamerpicUpdateCallbacks = new LoaderCallbacks<Result<GamerpicUpdateResponse>>() {
        public Loader<Result<GamerpicUpdateResponse>> onCreateLoader(int id, Bundle args) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GAMERPIC_UPDATE");
            GamerpicChangeRequest req = new GamerpicChangeRequest(args.getString(AccountProvisioningFragment.GAMERPIC_UPDATE_IMAGE_URL_KEY));
            HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", EndpointsFactory.get().profile(), "/users/me/profile/settings/PublicGamerpic"), "3");
            httpCall.setRequestBody(new GsonBuilder().create().toJson((Object) req, (Type) GamerpicChangeRequest.class));
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), null, args.get(ErrorHelper.KEY_RESULT_KEY), GamerpicUpdateResponse.class, Profile.registerAdapters(new GsonBuilder()).create(), httpCall);
        }

        public void onLoadFinished(Loader<Result<GamerpicUpdateResponse>> loader, Result<GamerpicUpdateResponse> result) {
            Log.d(AccountProvisioningFragment.TAG, "Finished LOADER_GAMERPIC_UPDATE");
            if (result.hasError()) {
                UTCError.trackServiceFailure(Errors.GamerPicUpdate, "Introducing view", result.getError());
                Log.e(AccountProvisioningFragment.TAG, "Failed to update gamerpic");
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, 6));
            AccountProvisioningFragment.this.state.errorHelper.initLoader(6, bundle);
        }

        public void onLoaderReset(Loader<Result<GamerpicUpdateResponse>> loader) {
        }
    };
    private final LoaderCallbacks<Result<Settings>> getPrivacySettingsCallbacks = new LoaderCallbacks<Result<Settings>>() {
        public Loader<Result<Settings>> onCreateLoader(int id, Bundle args) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GET_PRIVACY_SETTINGS");
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), args.get(ErrorHelper.KEY_RESULT_KEY), Settings.class, Privacy.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall("GET", EndpointsFactory.get().privacy(), "/users/me/privacy/settings"), "4"));
        }

        public void onLoadFinished(Loader<Result<Settings>> loader, Result<Settings> result) {
            Log.d(AccountProvisioningFragment.TAG, "LOADER_GET_PRIVACY_SETTINGS finished");
            if (result.hasData()) {
                Log.d(AccountProvisioningFragment.TAG, "Got privacy settings");
                Settings ps = (Settings) result.getData();
                if (ps.settings == null) {
                    Log.d(AccountProvisioningFragment.TAG, "Privacy settings map is null");
                    AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                } else if (ps.isSettingSet(Key.ShareIdentity) || ps.isSettingSet(Key.ShareIdentityTransitively)) {
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentity or ShareIdentityTransitively are set");
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentity: " + ps.settings.get(Key.ShareIdentity));
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentityTransitively: " + ps.settings.get(Key.ShareIdentityTransitively));
                    AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                } else {
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentity and ShareIdentityTransitively are NotSet");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, 7));
                    AccountProvisioningFragment.this.state.errorHelper.initLoader(7, bundle);
                }
            } else {
                Log.e(AccountProvisioningFragment.TAG, "Error getting privacy settings: " + result.getError());
                UTCError.trackServiceFailure("Service Error - Load privacy settings", "Sign up view", result.getError());
                AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
            }
        }

        public void onLoaderReset(Loader<Result<Settings>> loader) {
        }
    };
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray<>();
    private final LoaderCallbacks<Result<Void>> setPrivacySettingsCallbacks = new LoaderCallbacks<Result<Void>>() {
        public Loader<Result<Void>> onCreateLoader(int id, Bundle args) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_SET_PRIVACY_SETTINGS");
            HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall(HttpPut.METHOD_NAME, EndpointsFactory.get().privacy(), "/users/me/privacy/settings"), "4");
            Settings ps = Settings.newWithMap();
            ps.settings.put(Key.ShareIdentity, Value.PeopleOnMyList);
            ps.settings.put(Key.ShareIdentityTransitively, Value.Everyone);
            httpCall.setRequestBody(Privacy.registerAdapters(new GsonBuilder()).create().toJson((Object) ps, (Type) Settings.class));
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), args.get(ErrorHelper.KEY_RESULT_KEY), Void.class, Privacy.registerAdapters(new GsonBuilder()).create(), httpCall);
        }

        public void onLoadFinished(Loader<Result<Void>> loader, Result<Void> result) {
            Log.d(AccountProvisioningFragment.TAG, "LOADER_SET_PRIVACY_SETTINGS finished");
            if (result.hasError()) {
                Log.e(AccountProvisioningFragment.TAG, "Error setting privacy settings: " + result.getError());
                UTCError.trackServiceFailure("Service Error - Set privacy settings", "Sign up view", result.getError());
                AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            Log.e(AccountProvisioningFragment.TAG, "Privacy settings set successfully");
            AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
        }

        public void onLoaderReset(Loader<Result<Void>> loader) {
        }
    };
    public State state;
    public UserAccount userAccount;
    private final LoaderCallbacks<Result<UserAccount>> userProfileCallbacks = new LoaderCallbacks<Result<UserAccount>>() {
        public Loader<Result<UserAccount>> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case 1:
                    Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GET_PROFILE");
                    return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), args.get(ErrorHelper.KEY_RESULT_KEY), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall("GET", EndpointsFactory.get().userAccount(), "/users/current/profile"), "4"));
                case 2:
                    Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_POST_PROFILE");
                    HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", EndpointsFactory.get().userAccount(), "/users/current/profile"), "4");
                    UserAccount ua = AccountProvisioningFragment.this.userAccount;
                    ua.touAcceptanceDate = new Date();
                    ua.msftOptin = false;
                    if (TextUtils.isEmpty(ua.legalCountry)) {
                        ua.legalCountry = Locale.getDefault().getCountry();
                    }
                    httpCall.setRequestBody(UserAccount.registerAdapters(new GsonBuilder()).create().toJson((Object) ua, (Type) UserAccount.class));
                    return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), args.get(ErrorHelper.KEY_RESULT_KEY), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), httpCall);
                default:
                    return null;
            }
        }

        public void onLoadFinished(Loader<Result<UserAccount>> loader, Result<UserAccount> result) {
            switch (loader.getId()) {
                case 1:
                    Log.d(AccountProvisioningFragment.TAG, "LOADER_GET_PROFILE finished");
                    if (result.hasData()) {
                        Log.e(AccountProvisioningFragment.TAG, "Got UserAccount");
                        AccountProvisioningFragment.this.userAccount = (UserAccount) result.getData();
                        Bundle bundle = new Bundle(AccountProvisioningFragment.this.getArguments());
                        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, 2));
                        AccountProvisioningFragment.this.state.errorHelper.initLoader(2, bundle);
                        return;
                    }
                    Log.e(AccountProvisioningFragment.TAG, "Error getting UserAccount");
                    UTCError.trackServiceFailure("Service Error - Profile Load", "Sign up view", result.getError());
                    AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CREATION);
                    return;
                case 2:
                    Log.d(AccountProvisioningFragment.TAG, "LOADER_POST_PROFILE finished");
                    if (result.hasData()) {
                        Log.e(AccountProvisioningFragment.TAG, "Got UserAccount");
                        AccountProvisioningFragment.this.userAccount = (UserAccount) result.getData();
                        Bundle args = AccountProvisioningFragment.this.getArguments();
                        Bundle bundle2 = new Bundle();
                        bundle2.putLong("ARG_USER_PTR", args.getLong("ARG_USER_PTR"));
                        bundle2.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, 3));
                        AccountProvisioningFragment.this.state.errorHelper.initLoader(3, bundle2);
                        return;
                    }
                    Log.e(AccountProvisioningFragment.TAG, "Error getting UserAccount");
                    UTCError.trackServiceFailure("Service Error - Profile Load", "Sign up view", result.getError());
                    AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CREATION);
                    return;
                default:
                    return;
            }
        }

        public void onLoaderReset(Loader<Result<UserAccount>> loader) {
        }
    };
    private final LoaderCallbacks<XTokenLoader.Result> xtokenCallbacks = new LoaderCallbacks<XTokenLoader.Result>() {
        public Loader<XTokenLoader.Result> onCreateLoader(int id, Bundle args) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_XTOKEN");
            return new XTokenLoader(AccountProvisioningFragment.this.getActivity(), args.getLong("ARG_USER_PTR"), CacheUtil.getResultCache(XTokenLoader.Result.class), args.get(ErrorHelper.KEY_RESULT_KEY));
        }

        public void onLoadFinished(Loader<XTokenLoader.Result> loader, XTokenLoader.Result result) {
            Log.d(AccountProvisioningFragment.TAG, "LOADER_XTOKEN finished");
            if (result.hasData()) {
                AccountProvisioningFragment.this.xtokenData = (Data) result.getData();
                AccountProvisioningFragment.this.state.result = new AccountProvisioningResult(AccountProvisioningFragment.this.userAccount.gamerTag, AccountProvisioningFragment.this.userAccount.userXuid);
                AgeGroup ageGroup = AgeGroup.fromServiceString(AccountProvisioningFragment.this.xtokenData.getAuthFlowResult().getAgeGroup());
                if (ageGroup != null) {
                    Log.d(AccountProvisioningFragment.TAG, "ageGroup: " + ageGroup);
                    AccountProvisioningFragment.this.state.result.setAgeGroup(ageGroup);
                    if (ageGroup != AgeGroup.Child) {
                        AccountProvisioningFragment.this.state.errorHelper.initLoader(4, new Bundle());
                        return;
                    }
                    AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                    return;
                }
                Log.e(AccountProvisioningFragment.TAG, "Unknown age group");
                AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                return;
            }
            Log.d(AccountProvisioningFragment.TAG, "LOADER_XTOKEN: " + result.getError());
            UTCError.trackServiceFailure("Service Error - Load XTOKEN", "Sign up view", result.getError());
            AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
        }

        public void onLoaderReset(Loader<XTokenLoader.Result> loader) {
        }
    };
    public Data xtokenData;

    public interface Callbacks {
        void onCloseWithStatus(Status status, AccountProvisioningResult accountProvisioningResult);
    }

    public static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        public ErrorHelper errorHelper;
        public AccountProvisioningResult result;

        public State() {
            this.errorHelper = new ErrorHelper();
        }

        protected State(Parcel in) {
            this.errorHelper = (ErrorHelper) in.readParcelable(ErrorHelper.class.getClassLoader());
            this.result = (AccountProvisioningResult) in.readParcelable(AccountProvisioningResult.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.errorHelper, flags);
            dest.writeParcelable(this.result, flags);
        }
    }

    public enum Status {
        NO_ERROR,
        PROVIDER_ERROR,
        ERROR_USER_CANCEL
    }

    public AccountProvisioningFragment() {
        this.loaderMap.put(1, new ObjectLoaderInfo(this.userProfileCallbacks));
        this.loaderMap.put(2, new ObjectLoaderInfo(this.userProfileCallbacks));
        this.loaderMap.put(3, new ResultLoaderInfo(XTokenLoader.Result.class, this.xtokenCallbacks));
        this.loaderMap.put(4, new ObjectLoaderInfo(this.gamerpicChoiceListCallbacks));
        this.loaderMap.put(5, new ObjectLoaderInfo(this.gamerpicUpdateCallbacks));
        this.loaderMap.put(6, new ObjectLoaderInfo(this.getPrivacySettingsCallbacks));
        this.loaderMap.put(7, new ObjectLoaderInfo(this.setPrivacySettingsCallbacks));
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
            Log.e(TAG, "No arguments provided");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR, null);
        } else if (!args.containsKey("ARG_USER_PTR")) {
            Log.e(TAG, "No ARG_USER_PTR");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR, null);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_busy, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.state = savedInstanceState == null ? new State() : (State) savedInstanceState.getParcelable(KEY_STATE);
        this.state.errorHelper.setActivityContext(this);
    }

    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        Log.d(TAG, "Initializing LOADER_GET_PROFILE");
        Bundle bundle = new Bundle(args);
        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, 1));
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
        Log.d(TAG, "onActivityResult");
        this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR, null);
    }

    public LoaderInfo getLoaderInfo(int loaderId) {
        return (LoaderInfo) this.loaderMap.get(loaderId);
    }
}
