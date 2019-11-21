package com.microsoft.xbox.idp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.microsoft.xbox.idp.ui.ErrorActivity;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;

public final class ErrorHelper implements Parcelable {
    public static final Creator<ErrorHelper> CREATOR = new Creator<ErrorHelper>() {
        public ErrorHelper createFromParcel(Parcel in) {
            return new ErrorHelper(in);
        }

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
        Activity getActivity();

        LoaderInfo getLoaderInfo(int i);

        LoaderManager getLoaderManager();

        void startActivityForResult(Intent intent, int i);
    }

    public static class ActivityResult {
        private final boolean tryAgain;

        public ActivityResult(boolean tryAgain2) {
            this.tryAgain = tryAgain2;
        }

        public boolean isTryAgain() {
            return this.tryAgain;
        }
    }

    public interface LoaderInfo {
        void clearCache(Object obj);

        LoaderCallbacks<?> getLoaderCallbacks();

        boolean hasCachedData(Object obj);
    }

    public ErrorHelper() {
        this.loaderId = -1;
        this.loaderArgs = null;
    }

    protected ErrorHelper(Parcel in) {
        this.loaderId = in.readInt();
        this.loaderArgs = in.readBundle();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.loaderId);
        dest.writeBundle(this.loaderArgs);
    }

    @SuppressLint("WrongConstant")
    private boolean isConnected() {
        NetworkInfo ni = ((ConnectivityManager) this.activityContext.getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public void setActivityContext(ActivityContext activityContext2) {
        this.activityContext = activityContext2;
    }

    public void startErrorActivity(ErrorScreen screen) {
        Intent intent = new Intent(this.activityContext.getActivity(), ErrorActivity.class);
        intent.putExtra(ErrorActivity.ARG_ERROR_TYPE, screen.type.getId());
        this.activityContext.startActivityForResult(intent, 63);
    }

    public <D> boolean initLoader(int id, Bundle args) {
        return initLoader(id, args, true);
    }

    public <D> boolean initLoader(int id, Bundle args, boolean checkNetwork) {
        Log.d(TAG, "initLoader");
        if (id != -1) {
            this.loaderId = id;
            this.loaderArgs = args;
            LoaderManager lm = this.activityContext.getLoaderManager();
            LoaderInfo loaderInfo = this.activityContext.getLoaderInfo(this.loaderId);
            Object resultKey = this.loaderArgs == null ? null : this.loaderArgs.get(KEY_RESULT_KEY);
            if ((resultKey == null ? false : loaderInfo.hasCachedData(resultKey)) || lm.getLoader(id) != null || !checkNetwork || isConnected()) {
                Log.d(TAG, "initializing loader #" + this.loaderId);
                lm.initLoader(id, args, loaderInfo.getLoaderCallbacks());
                return true;
            }
            Log.e(TAG, "Starting error activity: OFFLINE");
            startErrorActivity(ErrorScreen.OFFLINE);
            return false;
        }
        Log.e(TAG, "LOADER_NONE");
        return false;
    }

    public <D> boolean restartLoader(int id, Bundle args) {
        if (id == -1) {
            return false;
        }
        this.loaderId = id;
        this.loaderArgs = args;
        if (isConnected()) {
            this.activityContext.getLoaderManager().restartLoader(this.loaderId, this.loaderArgs, this.activityContext.getLoaderInfo(this.loaderId).getLoaderCallbacks());
            return true;
        }
        startErrorActivity(ErrorScreen.OFFLINE);
        return false;
    }

    public <D> boolean restartLoader() {
        if (this.loaderId == -1) {
            return false;
        }
        if (isConnected()) {
            this.activityContext.getLoaderManager().restartLoader(this.loaderId, this.loaderArgs, this.activityContext.getLoaderInfo(this.loaderId).getLoaderCallbacks());
            return true;
        }
        startErrorActivity(ErrorScreen.OFFLINE);
        return false;
    }

    public void deleteLoader() {
        if (this.loaderId != -1) {
            this.activityContext.getLoaderManager().destroyLoader(this.loaderId);
            Object resultKey = this.loaderArgs == null ? null : this.loaderArgs.get(KEY_RESULT_KEY);
            if (resultKey != null) {
                this.activityContext.getLoaderInfo(this.loaderId).clearCache(resultKey);
            }
            this.loaderId = -1;
            this.loaderArgs = null;
        }
    }

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
}
