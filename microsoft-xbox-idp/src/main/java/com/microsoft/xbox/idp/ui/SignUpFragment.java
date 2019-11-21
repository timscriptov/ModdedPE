package com.microsoft.xbox.idp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.model.Const;
import com.microsoft.xbox.idp.model.GamerTag.Request;
import com.microsoft.xbox.idp.model.GamerTag.ReservationRequest;
import com.microsoft.xbox.idp.model.GamerTag.Response;
import com.microsoft.xbox.idp.model.Suggestions;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.helpers.UTCSignup;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Result;
import com.microsoft.xbox.idp.ui.AccountProvisioningResult.AgeGroup;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.idp.util.ObjectLoaderInfo;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

import java.lang.reflect.Type;
import java.util.Locale;

public class SignUpFragment extends BaseFragment implements OnClickListener, ActivityContext {
    static final boolean $assertionsDisabled = (!SignUpFragment.class.desiredAssertionStatus());
    public static final String ARG_ACCOUNT_PROVISIONING_RESULT = "ARG_ACCOUNT_PROVISIONING_RESULT";
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_CLAIM_GAMERTAG = 1;
    private static final int LOADER_RESERVE_GAMERTAG = 2;
    private static final int LOADER_SUGGESTIONS = 3;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onCloseWithStatus(Status status) {
        }
    };
    public static final String TAG = SignUpFragment.class.getSimpleName();
    public View bottomBarShadow;
    public Callbacks callbacks = NO_OP_CALLBACKS;
    private Button claimItButton;
    private View clearTextButton;
    public EditText editTextGamerTag;
    public View editTextGamerTagContainer;
    private final TextWatcher gamerTagChangeListener = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            SignUpFragment.this.resetGamerTagState(s);
        }
    };
    private final LoaderCallbacks<Result<Response>> gamerTagClaimCallbacks = new LoaderCallbacks<Result<Response>>() {
        public Loader<Result<Response>> onCreateLoader(int id, Bundle args) {
            Log.d(SignUpFragment.TAG, "Creating LOADER_CLAIM_GAMERTAG");
            HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", EndpointsFactory.get().accounts(), "/users/current/profile/gamertag"), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION);
            Request req = new Request();
            req.gamertag = SignUpFragment.this.state.gamerTag;
            req.preview = false;
            req.reservationId = SignUpFragment.this.provisioningResult.getXuid();
            httpCall.setRequestBody(new Gson().toJson((Object) req, (Type) Request.class));
            return new ObjectLoader(SignUpFragment.this.getActivity(), Response.class, new Gson(), httpCall);
        }

        public void onLoadFinished(Loader<Result<Response>> loader, Result<Response> result) {
            Log.d(SignUpFragment.TAG, "LOADER_CLAIM_GAMERTAG finished");
            if (!result.hasData()) {
                Log.e(SignUpFragment.TAG, "Error getting GamerTag.Response");
                SignUpFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
            } else if (((Response) result.getData()).hasFree) {
                Log.i(SignUpFragment.TAG, "Gamertag claimed successfully");
                SignUpFragment.this.state.gamerTag = SignUpFragment.this.editTextGamerTag.getText().toString();
                Interop.UpdateGamerTag(SignUpFragment.this.state.gamerTag);
                SignUpFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR);
            } else {
                Log.e(SignUpFragment.TAG, "Gamertag is not free");
                SignUpFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
            }
            SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
        }

        public void onLoaderReset(Loader<Result<Response>> loader) {
        }
    };
    private final LoaderCallbacks<Result<Void>> gamerTagReservationCallbacks = new LoaderCallbacks<Result<Void>>() {
        public Loader<Result<Void>> onCreateLoader(int id, Bundle args) {
            Log.d(SignUpFragment.TAG, "creating LOADER_RESERVE_GAMERTAG");
            HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", EndpointsFactory.get().userManagement(), "/gamertags/reserve"), "1");
            httpCall.setRequestBody(new Gson().toJson((Object) new ReservationRequest(SignUpFragment.this.editTextGamerTag.getText().toString(), SignUpFragment.this.provisioningResult.getXuid()), (Type) ReservationRequest.class));
            return new ObjectLoader(SignUpFragment.this.getActivity(), Void.class, new Gson(), httpCall);
        }

        public void onLoadFinished(Loader<Result<Void>> loader, Result<Void> result) {
            Log.d(SignUpFragment.TAG, "LOADER_RESERVE_GAMERTAG finished");
            if (!result.hasError()) {
                SignUpFragment.this.state.gamerTag = SignUpFragment.this.editTextGamerTag.getText().toString();
                SignUpFragment.this.state.reserved = true;
                SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
            } else if (result.getError().getHttpStatus() == 409) {
                SignUpFragment.this.state.gamerTagWithSuggestions = SignUpFragment.this.editTextGamerTag.getText().toString();
                SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
                SignUpFragment.this.getLoaderManager().restartLoader(3, null, SignUpFragment.this.suggestionsCallbacks);
            } else {
                Log.e(SignUpFragment.TAG, result.getError().toString());
                UTCError.trackServiceFailure("Service Error - Reserve gamertag", "Sign up view", result.getError());
                SignUpFragment.this.setGamerTagState(GamerTagState.ERROR);
            }
        }

        public void onLoaderReset(Loader<Result<Void>> loader) {
            SignUpFragment.this.state.reserved = false;
            SignUpFragment.this.state.gamerTagWithSuggestions = null;
            SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
        }
    };
    private GamerTagState gamerTagState;
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray<>();
    private final OnItemClickListener onSuggestionClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            SignUpFragment.this.editTextGamerTag.setText((CharSequence) SignUpFragment.this.suggestionsListAdapter.getItem(position));
        }
    };
    private TextView privacyDetailsText;
    private TextView privacyText;
    public AccountProvisioningResult provisioningResult;
    public ScrollView scrollView;
    private View searchButton;
    public State state;
    public final LoaderCallbacks<Result<Suggestions.Response>> suggestionsCallbacks = new LoaderCallbacks<Result<Suggestions.Response>>() {
        public Loader<Result<Suggestions.Response>> onCreateLoader(int id, Bundle args) {
            Log.d(SignUpFragment.TAG, "Creating LOADER_SUGGESTIONS");
            HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", EndpointsFactory.get().userManagement(), "/gamertags/generate"), "1");
            Suggestions.Request req = new Suggestions.Request();
            req.Algorithm = 1;
            req.Count = 3;
            req.Locale = Locale.getDefault().toString().replace("_", "-");
            req.Seed = SignUpFragment.this.editTextGamerTag.getText().toString();
            Log.d(SignUpFragment.TAG, "getting suggestions for " + req.Seed);
            httpCall.setRequestBody(new Gson().toJson((Object) req, (Type) Suggestions.Request.class));
            return new ObjectLoader(SignUpFragment.this.getActivity(), Suggestions.Response.class, new Gson(), httpCall);
        }

        public void onLoadFinished(Loader<Result<Suggestions.Response>> loader, Result<Suggestions.Response> result) {
            Log.d(SignUpFragment.TAG, "LOADER_SUGGESTIONS finished");
            if (result.hasData()) {
                Log.d(SignUpFragment.TAG, "Got suggestions");
                SignUpFragment.this.state.suggestions = (Suggestions.Response) result.getData();
                SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
                return;
            }
            Log.d(SignUpFragment.TAG, "Error getting suggestions: " + result.getError());
            UTCError.trackServiceFailure("Service Error - Load suggestions", "Sign up view", result.getError());
        }

        public void onLoaderReset(Loader<Result<Suggestions.Response>> loader) {
            SignUpFragment.this.state.suggestions = null;
        }
    };
    private AbsListView suggestionsList;
    public ArrayAdapter<String> suggestionsListAdapter;
    private TextView textGamerTagComment;
    private final ClickableSpan xboxDotComLauncher = new ClickableSpan() {
        public void onClick(View widget) {
            Log.d(SignUpFragment.TAG, "xboxDotComLauncher.onClick");
            try {
                SignUpFragment.this.startActivity(new Intent("android.intent.action.VIEW", Const.URL_XBOX_COM));
            } catch (ActivityNotFoundException e) {
                Log.e(SignUpFragment.TAG, e.getMessage());
            }
        }
    };

    public interface Callbacks {
        void onCloseWithStatus(Status status);
    }

    private enum GamerTagState {
        UNINITIALIZED(R.string.xbid_tools_empty),
        INITIAL(R.string.xbid_gamertag_available),
        EMPTY(R.string.xbid_tools_empty),
        AVAILABLE(R.string.xbid_gamertag_available),
        UNAVAILABLE(R.string.xbid_gamertag_not_available_no_suggestions_android),
        UNAVAILABLE_WITH_SUGGESTIONS(R.string.xbid_gamertag_not_available_android),
        UNKNOWN(R.string.xbid_gamertag_check_availability),
        CHECKING(R.string.xbid_gamertag_checking_android),
        ERROR(R.string.xbid_gamertag_checking_error);

        private final int stringId;

        private GamerTagState(int stringId2) {
            this.stringId = stringId2;
        }

        public int getStringId() {
            return this.stringId;
        }
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
        public String gamerTag;
        public String gamerTagWithSuggestions;
        public boolean reserved;
        public Suggestions.Response suggestions;

        public State() {
            this.gamerTag = null;
            this.reserved = false;
            this.gamerTagWithSuggestions = null;
            this.suggestions = null;
            this.errorHelper = new ErrorHelper();
        }

        public boolean hasSuggestions() {
            return (this.suggestions == null || this.suggestions.Gamertags == null || this.suggestions.Gamertags.isEmpty()) ? false : true;
        }

        protected State(Parcel in) {
            this.gamerTag = in.readString();
            this.reserved = in.readByte() != 0;
            this.gamerTagWithSuggestions = in.readString();
            this.suggestions = (Suggestions.Response) in.readParcelable(Suggestions.Response.class.getClassLoader());
            this.errorHelper = (ErrorHelper) in.readParcelable(ErrorHelper.class.getClassLoader());
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.gamerTag);
            dest.writeByte((byte) (this.reserved ? 1 : 0));
            dest.writeString(this.gamerTagWithSuggestions);
            dest.writeParcelable(this.suggestions, flags);
            dest.writeParcelable(this.errorHelper, flags);
        }

        public int describeContents() {
            return 0;
        }
    }

    public enum Status {
        NO_ERROR,
        ERROR_USER_CANCEL,
        ERROR_SWITCH_USER,
        PROVIDER_ERROR
    }

    public SignUpFragment() {
        this.loaderMap.put(2, new ObjectLoaderInfo(this.gamerTagReservationCallbacks));
        this.loaderMap.put(1, new ObjectLoaderInfo(this.gamerTagClaimCallbacks));
        this.loaderMap.put(3, new ObjectLoaderInfo(this.suggestionsCallbacks));
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "No arguments provided");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR);
        } else if (!args.containsKey(ARG_ACCOUNT_PROVISIONING_RESULT)) {
            Log.e(TAG, "No ARG_ACCOUNT_PROVISIONING_RESULT");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_sign_up, container, false);
    }

    @SuppressLint("StringFormatInvalid")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.scrollView = (ScrollView) view.findViewById(R.id.xbid_scroll_container);
        this.bottomBarShadow = view.findViewById(R.id.xbid_bottom_bar_shadow);
        this.scrollView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @SuppressLint("WrongConstant")
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                SignUpFragment.this.bottomBarShadow.setVisibility(UiUtil.canScroll(SignUpFragment.this.scrollView) ? 0 : 4);
            }
        });
        this.editTextGamerTagContainer = view.findViewById(R.id.xbid_enter_gamertag_container);
        this.editTextGamerTag = (EditText) view.findViewById(R.id.xbid_enter_gamertag);
        this.editTextGamerTag.addTextChangedListener(this.gamerTagChangeListener);
        this.editTextGamerTag.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                SignUpFragment.this.editTextGamerTagContainer.setBackgroundResource(hasFocus ? R.drawable.xbid_edit_text_state_focused : R.drawable.xbid_edit_text_state_normal);
            }
        });
        this.clearTextButton = view.findViewById(R.id.xbid_clear_text);
        this.clearTextButton.setOnClickListener(this);
        this.searchButton = view.findViewById(R.id.xbid_search);
        this.searchButton.setOnClickListener(this);
        this.textGamerTagComment = (TextView) view.findViewById(R.id.xbid_enter_gamertag_comment);
        this.textGamerTagComment.setOnClickListener(this);
        this.privacyText = (TextView) view.findViewById(R.id.xbid_privacy);
        this.privacyDetailsText = (TextView) view.findViewById(R.id.xbid_privacy_details);
        TextView diffAccountLink = (TextView) view.findViewById(R.id.xbid_aleady_have_gamer_tag_answer);
        diffAccountLink.setOnClickListener(this);
        diffAccountLink.setText(Html.fromHtml("<u>" + getString(R.string.xbid_another_sign_in) + "</u>"));
        this.claimItButton = (Button) view.findViewById(R.id.xbid_claim_it);
        this.claimItButton.setOnClickListener(this);
        this.suggestionsList = (AbsListView) view.findViewById(R.id.xbid_suggestions_list);
        this.suggestionsListAdapter = new ArrayAdapter<>(getActivity(), R.layout.xbid_row_suggestion, R.id.xbid_suggestion_text);
        this.suggestionsList.setAdapter(this.suggestionsListAdapter);
        this.suggestionsList.setOnItemClickListener(this.onSuggestionClickListener);
        this.provisioningResult = (AccountProvisioningResult) getArguments().getParcelable(ARG_ACCOUNT_PROVISIONING_RESULT);
        if (this.provisioningResult != null) {
            UTCCommonDataModel.setUserId(this.provisioningResult.getXuid());
        }
        UTCSignup.trackPageView(getActivityTitle());
        AgeGroup ageGroup = this.provisioningResult.getAgeGroup();
        if (savedInstanceState == null) {
            this.state = new State();
            this.state.gamerTag = this.provisioningResult.getGamerTag();
            this.editTextGamerTag.setText(this.state.gamerTag);
        } else {
            this.state = (State) savedInstanceState.getParcelable(KEY_STATE);
            resetGamerTagState(this.editTextGamerTag.getText());
        }
        this.state.errorHelper.setActivityContext(this);
        this.privacyText.setText(getString(R.string.xbid_privacy_settings_header_android, new Object[]{getString(ageGroup.resIdAgeGroup)}));
        UiUtil.ensureClickableSpanOnUnderlineSpan(this.privacyDetailsText, ageGroup.resIdAgeGroupDetails, this.xboxDotComLauncher);
    }

    public void onResume() {
        super.onResume();
        this.state.errorHelper.restartLoader();
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
        int id = v.getId();
        if (id == R.id.xbid_enter_gamertag_comment || id == R.id.xbid_search) {
            setGamerTagState(GamerTagState.CHECKING);
            Log.d(TAG, "Restarting LOADER_RESERVE_GAMERTAG");
            UTCSignup.trackSearchGamerTag(this.provisioningResult, getActivityTitle());
            getLoaderManager().restartLoader(2, null, this.gamerTagReservationCallbacks);
        } else if (id == R.id.xbid_aleady_have_gamer_tag_answer) {
            UTCSignup.trackSignInWithDifferentUser(this.provisioningResult, getActivityTitle());
            UTCUser.setIsSilent(false);
            this.callbacks.onCloseWithStatus(Status.ERROR_SWITCH_USER);
        } else if (id == R.id.xbid_claim_it) {
            if (this.gamerTagState == GamerTagState.INITIAL) {
                Log.d(TAG, "Interop.SignUpStatus.NO_ERROR");
                this.callbacks.onCloseWithStatus(Status.NO_ERROR);
            } else {
                Log.d(TAG, "Restarting LOADER_CLAIM_GAMERTAG");
                getLoaderManager().restartLoader(1, null, this.gamerTagClaimCallbacks);
            }
            UTCSignup.trackClaimGamerTag(this.provisioningResult, getActivityTitle());
        } else if (id == R.id.xbid_clear_text) {
            this.editTextGamerTag.setText("");
            UTCSignup.trackClearGamerTag(this.provisioningResult, getActivityTitle());
        }
    }

    public LoaderInfo getLoaderInfo(int loaderId) {
        return (LoaderInfo) this.loaderMap.get(loaderId);
    }

    public void setGamerTagState(GamerTagState newState) {
        boolean z;
        boolean z2;
        boolean searchEnabled;
        int i;
        int i2 = 8;
        boolean z3 = true;
        this.textGamerTagComment.setText(newState.getStringId());
        TextView textView = this.textGamerTagComment;
        if (newState == GamerTagState.UNKNOWN) {
            z = true;
        } else {
            z = false;
        }
        textView.setFocusable(z);
        EditText editText = this.editTextGamerTag;
        if (newState == GamerTagState.CHECKING || newState == GamerTagState.UNINITIALIZED) {
            z2 = false;
        } else {
            z2 = true;
        }
        editText.setEnabled(z2);
        if (newState == GamerTagState.UNKNOWN || newState == GamerTagState.ERROR) {
            searchEnabled = true;
        } else {
            searchEnabled = false;
        }
        this.textGamerTagComment.setEnabled(searchEnabled);
        this.searchButton.setEnabled(searchEnabled);
        View view = this.searchButton;
        if (searchEnabled) {
            i = 0;
        } else {
            i = 8;
        }
        view.setVisibility(i);
        Button button = this.claimItButton;
        if (!(newState == GamerTagState.AVAILABLE || newState == GamerTagState.INITIAL)) {
            z3 = false;
        }
        button.setEnabled(z3);
        if (newState == GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS) {
            this.suggestionsListAdapter.clear();
            if (this.state.hasSuggestions()) {
                this.suggestionsListAdapter.addAll(this.state.suggestions.Gamertags);
            }
            this.suggestionsListAdapter.notifyDataSetChanged();
        } else if (this.gamerTagState == GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS) {
            this.suggestionsListAdapter.clear();
            this.suggestionsListAdapter.notifyDataSetChanged();
        }
        View view2 = this.clearTextButton;
        if (!TextUtils.isEmpty(this.editTextGamerTag.getText())) {
            i2 = 0;
        }
        view2.setVisibility(i2);
        this.gamerTagState = newState;
    }

    public void resetGamerTagState(CharSequence gamertag) {
        if (TextUtils.isEmpty(this.state.gamerTag)) {
            setGamerTagState(GamerTagState.UNINITIALIZED);
        } else if (TextUtils.isEmpty(gamertag)) {
            setGamerTagState(GamerTagState.EMPTY);
        } else if (TextUtils.equals(gamertag, this.state.gamerTag)) {
            if (TextUtils.equals(this.state.gamerTag, this.provisioningResult.getGamerTag())) {
                setGamerTagState(GamerTagState.INITIAL);
            } else if (this.state.reserved) {
                setGamerTagState(GamerTagState.AVAILABLE);
            } else if (TextUtils.equals(gamertag, this.state.gamerTagWithSuggestions)) {
                setGamerTagState(this.state.hasSuggestions() ? GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS : GamerTagState.UNAVAILABLE);
            } else {
                setGamerTagState(GamerTagState.UNKNOWN);
            }
        } else if (TextUtils.equals(gamertag, this.state.gamerTagWithSuggestions)) {
            setGamerTagState(this.state.hasSuggestions() ? GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS : GamerTagState.UNAVAILABLE);
        } else {
            setGamerTagState(GamerTagState.UNKNOWN);
        }
    }
}
