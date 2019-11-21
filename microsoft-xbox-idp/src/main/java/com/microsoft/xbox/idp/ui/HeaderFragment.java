package com.microsoft.xbox.idp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.UserAccount;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.toolkit.BitmapLoader.Result;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;

public class HeaderFragment extends BaseFragment implements OnClickListener {
    static final boolean $assertionsDisabled = (!HeaderFragment.class.desiredAssertionStatus());
    private static final int LOADER_GET_PROFILE = 1;
    private static final int LOADER_USER_IMAGE_URL = 2;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onClickCloseHeader() {
        }
    };
    public static final String TAG = HeaderFragment.class.getSimpleName();
    private Callbacks callbacks = NO_OP_CALLBACKS;
    public final LoaderCallbacks<Result> imageCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int id, Bundle args) {
            Log.d(HeaderFragment.TAG, "Creating LOADER_USER_IMAGE_URL");
            Log.d(HeaderFragment.TAG, "url: " + HeaderFragment.this.userAccount.imageUrl);
            return new BitmapLoader(HeaderFragment.this.getActivity(), CacheUtil.getBitmapCache(), HeaderFragment.this.userAccount.imageUrl, HeaderFragment.this.userAccount.imageUrl);
        }

        @SuppressLint("WrongConstant")
        public void onLoadFinished(Loader<Result> loader, Result result) {
            Log.d(HeaderFragment.TAG, "LOADER_USER_IMAGE_URL finished");
            if (result.hasData()) {
                HeaderFragment.this.userImageView.setVisibility(0);
                HeaderFragment.this.userImageView.setImageBitmap((Bitmap) result.getData());
            } else if (result.hasException()) {
                HeaderFragment.this.userImageView.setVisibility(8);
                Log.w(HeaderFragment.TAG, "Failed to load user image with message: " + result.getException().getMessage());
            }
        }

        public void onLoaderReset(Loader<Result> loader) {
            HeaderFragment.this.userImageView.setImageBitmap(null);
        }
    };
    public UserAccount userAccount;
    LoaderCallbacks<ObjectLoader.Result<UserAccount>> userAccountCallbacks = new LoaderCallbacks<ObjectLoader.Result<UserAccount>>() {
        public Loader<ObjectLoader.Result<UserAccount>> onCreateLoader(int id, Bundle args) {
            Log.d(HeaderFragment.TAG, "Creating LOADER_GET_PROFILE");
            return new ObjectLoader(HeaderFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), new FragmentLoaderKey(HeaderFragment.class, 1), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall("GET", EndpointsFactory.get().accounts(), "/users/current/profile"), "4"));
        }

        @SuppressLint("WrongConstant")
        public void onLoadFinished(Loader<ObjectLoader.Result<UserAccount>> loader, ObjectLoader.Result<UserAccount> result) {
            Log.d(HeaderFragment.TAG, "LOADER_GET_PROFILE finished");
            if (result.hasData()) {
                HeaderFragment.this.userAccount = (UserAccount) result.getData();
                HeaderFragment.this.userEmail.setText(HeaderFragment.this.userAccount.email);
                if (!TextUtils.isEmpty(HeaderFragment.this.userAccount.firstName) || !TextUtils.isEmpty(HeaderFragment.this.userAccount.lastName)) {
                    HeaderFragment.this.userName.setVisibility(0);
                    HeaderFragment.this.userName.setText(HeaderFragment.this.getString(R.string.xbid_first_and_last_name_android, new Object[]{HeaderFragment.this.userAccount.firstName, HeaderFragment.this.userAccount.lastName}));
                } else {
                    HeaderFragment.this.userName.setVisibility(8);
                }
                HeaderFragment.this.getLoaderManager().initLoader(2, null, HeaderFragment.this.imageCallbacks);
                return;
            }
            Log.e(HeaderFragment.TAG, "Error getting UserAccount");
        }

        public void onLoaderReset(Loader<ObjectLoader.Result<UserAccount>> loader) {
        }
    };
    public TextView userEmail;
    public ImageView userImageView;
    public TextView userName;

    public interface Callbacks {
        void onClickCloseHeader();
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
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_header, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.xbid_close).setOnClickListener(this);
        this.userImageView = (ImageView) view.findViewById(R.id.xbid_user_image);
        this.userName = (TextView) view.findViewById(R.id.xbid_user_name);
        this.userEmail = (TextView) view.findViewById(R.id.xbid_user_email);
    }

    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            getLoaderManager().initLoader(1, args, this.userAccountCallbacks);
        } else {
            Log.e(TAG, "No arguments provided");
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.xbid_close) {
            this.callbacks.onClickCloseHeader();
        }
    }
}
