package com.microsoft.xbox.idp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.GsonBuilder;
import com.mcal.xbox.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.UserAccount;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;

import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HeaderFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = HeaderFragment.class.getSimpleName();
    private static final int LOADER_GET_PROFILE = 1;
    private static final int LOADER_USER_IMAGE_URL = 2;
    private static final Callbacks NO_OP_CALLBACKS = () -> {
    };
    public UserAccount userAccount;
    public AppCompatTextView userEmail;
    public AppCompatImageView userImageView;
    public final LoaderManager.LoaderCallbacks<BitmapLoader.Result> imageCallbacks = new LoaderManager.LoaderCallbacks<BitmapLoader.Result>() {
        @Contract("_, _ -> new")
        public @NotNull Loader<BitmapLoader.Result> onCreateLoader(int i, Bundle bundle) {
            Log.d(HeaderFragment.TAG, "Creating LOADER_USER_IMAGE_URL");
            Log.d(HeaderFragment.TAG, "url: " + userAccount.imageUrl);
            return new BitmapLoader(getActivity(), CacheUtil.getBitmapCache(), userAccount.imageUrl, userAccount.imageUrl);
        }

        @SuppressLint("WrongConstant")
        public void onLoadFinished(Loader<BitmapLoader.Result> loader, BitmapLoader.@NotNull Result result) {
            Log.d(HeaderFragment.TAG, "LOADER_USER_IMAGE_URL finished");
            if (result.hasData()) {
                userImageView.setVisibility(0);
                userImageView.setImageBitmap(result.getData());
            } else if (result.hasException()) {
                userImageView.setVisibility(8);
                Log.w(HeaderFragment.TAG, "Failed to load user image with message: " + result.getException().getMessage());
            }
        }

        public void onLoaderReset(Loader<BitmapLoader.Result> loader) {
            userImageView.setImageBitmap(null);
        }
    };
    public AppCompatTextView userName;
    LoaderManager.LoaderCallbacks<ObjectLoader.Result<UserAccount>> userAccountCallbacks = new LoaderManager.LoaderCallbacks<ObjectLoader.Result<UserAccount>>() {
        public void onLoaderReset(Loader<ObjectLoader.Result<UserAccount>> loader) {
        }

        @Contract("_, _ -> new")
        public @NotNull Loader<ObjectLoader.Result<UserAccount>> onCreateLoader(int i, Bundle bundle) {
            Log.d(HeaderFragment.TAG, "Creating LOADER_GET_PROFILE");
            return new ObjectLoader(getActivity(), CacheUtil.getObjectLoaderCache(), new FragmentLoaderKey(HeaderFragment.class, 1), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall(HttpGet.METHOD_NAME, EndpointsFactory.get().accounts(), "/users/current/profile"), "4"));
        }

        @SuppressLint("WrongConstant")
        public void onLoadFinished(Loader<ObjectLoader.Result<UserAccount>> loader, ObjectLoader.@NotNull Result<UserAccount> result) {
            Log.d(HeaderFragment.TAG, "LOADER_GET_PROFILE finished");
            if (result.hasData()) {
                userEmail.setText(userAccount.email);
                if (!TextUtils.isEmpty(userAccount.firstName) || !TextUtils.isEmpty(userAccount.lastName)) {
                    userName.setVisibility(0);
                    userName.setText(getString(R.string.xbid_first_and_last_name_android, userAccount.firstName, userAccount.lastName));
                } else {
                    userName.setVisibility(8);
                }
                getLoaderManager().initLoader(2, null, imageCallbacks);
                return;
            }
            Log.e(HeaderFragment.TAG, "Error getting UserAccount");
        }
    };
    private Callbacks callbacks = NO_OP_CALLBACKS;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    public void onDetach() {
        super.onDetach();
        callbacks = NO_OP_CALLBACKS;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(@NotNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_header, viewGroup, false);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        view.findViewById(R.id.xbid_close).setOnClickListener(this);
        userImageView = view.findViewById(R.id.xbid_user_image);
        userName = view.findViewById(R.id.xbid_user_name);
        userEmail = view.findViewById(R.id.xbid_user_email);
    }

    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null) {
            getLoaderManager().initLoader(1, arguments, userAccountCallbacks);
        } else {
            Log.e(TAG, "No arguments provided");
        }
    }

    public void onClick(@NotNull View view) {
        if (view.getId() == R.id.xbid_close) {
            callbacks.onClickCloseHeader();
        }
    }

    public interface Callbacks {
        void onClickCloseHeader();
    }
}
