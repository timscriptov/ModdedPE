package com.microsoft.xbox.idp.interop;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.xbox.idp.ui.ErrorActivity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class ErrorHelper implements Parcelable {
    public static final Parcelable.Creator<ErrorHelper> CREATOR = new Parcelable.Creator<ErrorHelper>() {
        @NotNull
        @Contract("_ -> new")
        public ErrorHelper createFromParcel(Parcel in) {
            return new ErrorHelper(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public ErrorHelper[] newArray(int size) {
            return new ErrorHelper[size];
        }
    };
    public static final String KEY_RESULT_KEY = "KEY_RESULT_KEY";
    public static final int LOADER_NONE = -1;
    public static final int RC_ERROR_SCREEN = 63;
    private static final String TAG = ErrorHelper.class.getSimpleName();
    private ActivityContext activityContext;
    public Bundle loaderArgs;
    public int loaderId;

    public interface ActivityContext {
        AppCompatActivity getActivity();

        LoaderInfo getLoaderInfo(int i);

        LoaderManager getLoaderManager();

        void startActivityForResult(Intent intent, int i);
    }

    public interface LoaderInfo {
        void clearCache(Object obj);

        LoaderManager.LoaderCallbacks<?> getLoaderCallbacks();

        boolean hasCachedData(Object obj);
    }

    public ErrorHelper() {
        loaderId = -1;
        loaderArgs = null;
    }

    protected ErrorHelper(@NotNull Parcel in) {
        loaderId = in.readInt();
        loaderArgs = in.readBundle();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeInt(loaderId);
        dest.writeBundle(loaderArgs);
    }

    private boolean isConnected() {
        @SuppressLint("WrongConstant") NetworkInfo ni = ((ConnectivityManager) activityContext.getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public void setActivityContext(ActivityContext activityContext2) {
        activityContext = activityContext2;
    }

    public void startErrorActivity(@NotNull ErrorActivity.ErrorScreen screen) {
        Intent intent = new Intent(this.activityContext.getActivity(), ErrorActivity.class);
        intent.putExtra(ErrorActivity.ARG_ERROR_TYPE, screen.type.getId());
        activityContext.startActivityForResult(intent, 63);
    }

    public <D> boolean initLoader(int id, Bundle args) {
        return initLoader(id, args, true);
    }

    public <D> boolean initLoader(int id, Bundle args, boolean checkNetwork) {
        Log.d(TAG, "initLoader");
        if (id != -1) {
            loaderId = id;
            loaderArgs = args;
            LoaderManager lm = activityContext.getLoaderManager();
            LoaderInfo loaderInfo = activityContext.getLoaderInfo(loaderId);
            Object resultKey = loaderArgs == null ? null : loaderArgs.get(KEY_RESULT_KEY);
            if ((resultKey != null && loaderInfo.hasCachedData(resultKey)) || lm.getLoader(id) != null || !checkNetwork || isConnected()) {
                Log.d(TAG, "initializing loader #" + loaderId);
                lm.initLoader(id, args, loaderInfo.getLoaderCallbacks());
                return true;
            }
            Log.e(TAG, "Starting error activity: OFFLINE");
            startErrorActivity(ErrorActivity.ErrorScreen.OFFLINE);
            return false;
        }
        Log.e(TAG, "LOADER_NONE");
        return false;
    }

    public <D> boolean restartLoader(int id, Bundle args) {
        if (id == -1) {
            return false;
        }
        loaderId = id;
        loaderArgs = args;
        if (isConnected()) {
            activityContext.getLoaderManager().restartLoader(loaderId, loaderArgs, activityContext.getLoaderInfo(loaderId).getLoaderCallbacks());
            return true;
        }
        startErrorActivity(ErrorActivity.ErrorScreen.OFFLINE);
        return false;
    }

    public <D> boolean restartLoader() {
        if (loaderId == -1) {
            return false;
        }
        if (isConnected()) {
            activityContext.getLoaderManager().restartLoader(loaderId, loaderArgs, activityContext.getLoaderInfo(loaderId).getLoaderCallbacks());
            return true;
        }
        startErrorActivity(ErrorActivity.ErrorScreen.OFFLINE);
        return false;
    }

    public void deleteLoader() {
        if (loaderId != -1) {
            activityContext.getLoaderManager().destroyLoader(loaderId);
            Object resultKey = loaderArgs == null ? null : loaderArgs.get(KEY_RESULT_KEY);
            if (resultKey != null) {
                activityContext.getLoaderInfo(loaderId).clearCache(resultKey);
            }
            loaderId = -1;
            loaderArgs = null;
        }
    }

    @Nullable
    @Contract(pure = true)
    public ActivityResult getActivityResult(int requestCode, int resultCode, Intent data) {
        boolean z = true;
        if (requestCode != 63) {
            return null;
        }
        if (resultCode != 1) {
            z = false;
        }
        return new ActivityResult(z);
    }

    public static class ActivityResult {
        private final boolean mTryAgain;

        public ActivityResult(boolean tryAgain) {
            mTryAgain = tryAgain;
        }

        public boolean isTryAgain() {
            return mTryAgain;
        }
    }
}