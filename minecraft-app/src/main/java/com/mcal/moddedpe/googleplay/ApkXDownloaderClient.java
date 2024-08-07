package com.mcal.moddedpe.googleplay;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Messenger;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.vending.expansion.downloader.*;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.APKExpansionPolicy;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.mojang.minecraftpe.ActivityListener;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.packagesource.PackageSource;
import com.mojang.minecraftpe.packagesource.PackageSourceListener;

import java.io.File;

public class ApkXDownloaderClient extends PackageSource implements IDownloaderClient, ActivityListener {
    public static final byte[] SALT = {78, -97, 80, -51, 45, -99, 108, 52, -42, 25, 48, 24, -76, -105, 9, 38, -43, 81, 6, 14};
    private static final String LOG_TAG = "ApkXDownloaderClient";
    private static String licenseKey;
    private static String notificationChannelId;
    private final MainActivity mActivity;
    private final PackageSourceListener mListener;
    private final NotificationManager mNotificationManager;
    private final StorageManager mStorageManager;
    private IStub mDownloaderClientStub;
    private IDownloaderService mRemoteService;

    // Define constants for OBB states
    private static final int OBB_STATE_MOUNTED = 1;
    private static final int OBB_STATE_UNMOUNTED = 2;
    private static final int OBB_STATE_ERROR_INTERNAL = 20;
    private static final int OBB_STATE_ERROR_COULD_NOT_MOUNT = 21;
    private static final int OBB_STATE_ERROR_COULD_NOT_UNMOUNT = 22;
    private static final int OBB_STATE_ERROR_NOT_MOUNTED = 23;
    private static final int OBB_STATE_ERROR_ALREADY_MOUNTED = 24;
    private static final int OBB_STATE_ERROR_PERMISSION_DENIED = 25;

    // Define constants for download states
    private static final int DOWNLOAD_STATE_IDLE = 1;
    private static final int DOWNLOAD_STATE_FETCHING_URL = 2;
    private static final int DOWNLOAD_STATE_CONNECTING = 3;
    private static final int DOWNLOAD_STATE_DOWNLOADING = 4;
    private static final int DOWNLOAD_STATE_COMPLETED = 5;
    private static final int DOWNLOAD_STATE_PAUSED_NETWORK_UNAVAILABLE = 6;
    private static final int DOWNLOAD_STATE_PAUSED_BY_REQUEST = 7;
    private static final int DOWNLOAD_STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION = 8;
    private static final int DOWNLOAD_STATE_PAUSED_NEED_CELLULAR_PERMISSION = 9;
    private static final int DOWNLOAD_STATE_PAUSED_ROAMING = 10;
    private static final int DOWNLOAD_STATE_PAUSED_NETWORK_SETUP_FAILURE = 11;
    private static final int DOWNLOAD_STATE_PAUSED_SDCARD_UNAVAILABLE = 12;
    private static final int DOWNLOAD_STATE_PAUSED_SDCARD_FULL = 13;
    private static final int DOWNLOAD_STATE_PAUSED_WAITING_TO_RETRY = 14;
    private static final int DOWNLOAD_STATE_FAILED_UNLICENSED = 15;
    private static final int DOWNLOAD_STATE_FAILED_FETCHING_URL = 16;
    private static final int DOWNLOAD_STATE_FAILED_SDCARD_FULL = 17;
    private static final int DOWNLOAD_STATE_FAILED_CANCELED = 18;
    private static final int DOWNLOAD_STATE_FAILED = 19;

    public ApkXDownloaderClient(@NonNull MainActivity activity, String googlePlayLicenseKey, PackageSourceListener listener) {
        licenseKey = googlePlayLicenseKey;
        mListener = listener;
        mActivity = activity;
        activity.addListener(this);
        notificationChannelId = String.format("%1$s_APKXDownload", mActivity.getCallingPackage());
        mNotificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mStorageManager = (StorageManager) mActivity.getSystemService(Context.STORAGE_SERVICE);
    }

    public static int convertOBBStateToMountState(int state) {
        if (state != OBB_STATE_MOUNTED) {
            if (state != OBB_STATE_UNMOUNTED) {
                return switch (state) {
                    case OBB_STATE_ERROR_INTERNAL -> 4;
                    case OBB_STATE_ERROR_COULD_NOT_MOUNT -> 2;
                    case OBB_STATE_ERROR_COULD_NOT_UNMOUNT -> 3;
                    case OBB_STATE_ERROR_NOT_MOUNTED -> 5;
                    case OBB_STATE_ERROR_ALREADY_MOUNTED -> 1;
                    case OBB_STATE_ERROR_PERMISSION_DENIED -> 6;
                    default -> 0;
                };
            }
            return 8;
        }
        return 7;
    }

    public static int convertStateToFailedReason(int state) {
        return switch (state) {
            case DOWNLOAD_STATE_FAILED_UNLICENSED -> 2;
            case DOWNLOAD_STATE_FAILED_FETCHING_URL -> 3;
            case DOWNLOAD_STATE_FAILED_SDCARD_FULL -> 4;
            case DOWNLOAD_STATE_FAILED_CANCELED -> 5;
            default -> 0;
        };
    }

    public static int convertStateToPausedReason(int state) {
        return switch (state) {
            case DOWNLOAD_STATE_PAUSED_NETWORK_UNAVAILABLE -> 1;
            case DOWNLOAD_STATE_PAUSED_BY_REQUEST -> 2;
            case DOWNLOAD_STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION -> 3;
            case DOWNLOAD_STATE_PAUSED_NEED_CELLULAR_PERMISSION -> 4;
            case DOWNLOAD_STATE_PAUSED_ROAMING -> 5;
            case DOWNLOAD_STATE_PAUSED_NETWORK_SETUP_FAILURE -> 6;
            case DOWNLOAD_STATE_PAUSED_SDCARD_UNAVAILABLE -> 7;
            case DOWNLOAD_STATE_PAUSED_SDCARD_FULL -> 8;
            case DOWNLOAD_STATE_PAUSED_WAITING_TO_RETRY -> 9;
            default -> 0;
        };
    }

    public static String getLicenseKey() {
        return licenseKey;
    }

    public static String getNotificationChannelId() {
        return notificationChannelId;
    }

    @Override
    public void destructor() {
        Log.i(LOG_TAG, "destructor");
        mActivity.removeListener(this);
    }

    @Override
    public String getMountPath(String path) {
        StorageManager storageManager = mStorageManager;
        if (storageManager == null) {
            return null;
        }
        return storageManager.getMountedObbPath(path);
    }

    @Override
    public String getDownloadDirectoryPath() {
        return Helpers.getSaveFilePath(mActivity);
    }

    public String getOBBFilePath(String filename) {
        return Helpers.generateSaveFileName(mActivity, filename);
    }

    @Override
    public void mountFiles(final String filename) {
        if (filename == null || filename.isEmpty()) {
            Log.e(LOG_TAG, String.format("mountFiles - filename '%s' is empty.", filename));
            return;
        }
        String oBBFilePath = getOBBFilePath(filename);
        Log.d(LOG_TAG, String.format("mountFiles - path: '%s'.", oBBFilePath));
        if (!new File(oBBFilePath).exists()) {
            Log.e(LOG_TAG, String.format("mountFiles - path '%s' does not exist.", oBBFilePath));
        } else {
            mStorageManager.mountObb(oBBFilePath, null, new OnObbStateChangeListener() {
                @Override
                public void onObbStateChange(String path, int state) {
                    super.onObbStateChange(path, state);
                    Log.d(ApkXDownloaderClient.LOG_TAG, String.format("onObbStateChange - path: '%s', state: %d.", path, state));
                    int convertOBBStateToMountState = ApkXDownloaderClient.convertOBBStateToMountState(state);
                    boolean isObbMounted = mStorageManager.isObbMounted(path);
                    String mountPath = getMountPath(path);
                    Log.d(ApkXDownloaderClient.LOG_TAG, String.format("onObbStateChange - source path: '%s', new path: '%s'.", path, mountPath));
                    if (mountPath == null) {
                        mountPath = "";
                    }
                    if (isObbMounted && mountPath.isEmpty()) {
                        convertOBBStateToMountState = 4;
                    }
                    mListener.onMountStateChanged(mountPath, convertOBBStateToMountState);
                }
            });
        }
    }

    @Override
    public void unmountFiles(final String filename) {
        if (filename == null || filename.isEmpty()) {
            Log.w(LOG_TAG, String.format("unmountFiles - filename '%s' is empty.", filename));
            return;
        }
        String oBBFilePath = getOBBFilePath(filename);
        Log.d(LOG_TAG, String.format("unmountFiles - path: '%s'.", oBBFilePath));
        if (!new File(oBBFilePath).exists()) {
            Log.w(LOG_TAG, String.format("unmountFiles - path '%s' does not exist.", oBBFilePath));
        } else {
            mStorageManager.unmountObb(oBBFilePath, false, null);
        }
    }

    @Override
    public void downloadFiles(final String filename, final long filesize, final boolean verifyName, final boolean verifySize) {
        @SuppressLint("HardwareIds")
        String string = Settings.Secure.getString(mActivity.getContentResolver(), "android_id");
        MainActivity mainActivity = mActivity;
        final APKExpansionPolicy aPKExpansionPolicy = new APKExpansionPolicy(mainActivity, new AESObfuscator(SALT, mainActivity.getPackageName(), string));
        aPKExpansionPolicy.resetPolicy();
        new LicenseChecker(mActivity, aPKExpansionPolicy, licenseKey).checkAccess(new LicenseCheckerCallback() {
            @Override
            public void allow(int policyReason) {
                Log.i(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - allow: %d.", policyReason));
                Log.i(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - Expecting to find file name: '%s', size: %d.", filename, filesize));
                int expansionURLCount = aPKExpansionPolicy.getExpansionURLCount();
                for (int i = 0; i < expansionURLCount; i++) {
                    Log.i(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - File name: '%s', size: %d.", aPKExpansionPolicy.getExpansionFileName(i), aPKExpansionPolicy.getExpansionFileSize(i)));
                }
                if (verifyName && (expansionURLCount == 0 || !filename.equalsIgnoreCase(aPKExpansionPolicy.getExpansionFileName(0)))) {
                    Log.e(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - Verification failed. File name: '%s', found name: '%s'.", filename, aPKExpansionPolicy.getExpansionFileName(0)));
                    mListener.onDownloadStateChanged(false, false, false, false, true, 0, 6);
                } else if (!verifySize || (expansionURLCount != 0 && filesize == aPKExpansionPolicy.getExpansionFileSize(0))) {
                    if (Helpers.canWriteOBBFile(mActivity)) {
                        launchDownloader();
                    } else {
                        mListener.onDownloadStateChanged(false, false, false, false, true, 0, 1);
                    }
                } else {
                    Log.e(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - Verification failed. File size: '%s', found size: '%s'.", filesize, aPKExpansionPolicy.getExpansionFileSize(0)));
                    mListener.onDownloadStateChanged(false, false, false, false, true, 0, 6);
                }
            }

            @Override
            public void dontAllow(int policyReason) {
                Log.i(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - dontAllow: %d", policyReason));
                mListener.onDownloadStateChanged(false, false, false, false, true, 0, policyReason != 291 ? policyReason != 561 ? 0 : 2 : 7);
            }

            @Override
            public void applicationError(int errorCode) {
                Log.i(ApkXDownloaderClient.LOG_TAG, String.format("LicenseCheckerCallback - error: %d", errorCode));
                mListener.onDownloadStateChanged(false, false, false, false, true, 0, 8);
            }
        });
    }

    @Override
    public void pauseDownload() {
        IDownloaderService iDownloaderService = mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.requestPauseDownload();
        }
    }

    @Override
    public void resumeDownload() {
        IDownloaderService iDownloaderService = mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.requestContinueDownload();
        }
    }

    @Override
    public void resumeDownloadOnCell() {
        IDownloaderService iDownloaderService = mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.setDownloadFlags(1);
            mRemoteService.requestContinueDownload();
        }
    }

    @Override
    public void abortDownload() {
        IDownloaderService iDownloaderService = mRemoteService;
        if (iDownloaderService != null) {
            iDownloaderService.requestAbortDownload();
        }
    }

    public void launchDownloader() {
        mActivity.runOnUiThread(new Runnable() {
            final IDownloaderClient client = ApkXDownloaderClient.this;

            @Override
            public void run() {
                deleteObbFiles();
                try {
                    mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(client, ApkXDownloaderService.class);
                    mDownloaderClientStub.connect(mActivity);
                    Intent intent = mActivity.getIntent();
                    Intent intent2 = new Intent(mActivity, mActivity.getClass());
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.setAction(intent.getAction());
                    if (intent.getCategories() != null) {
                        for (String str : intent.getCategories()) {
                            intent2.addCategory(str);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        String stringResource = PackageSource.getStringResource(StringResourceId.NOTIFICATIONCHANNEL_NAME);
                        String stringResource2 = PackageSource.getStringResource(StringResourceId.NOTIFICATIONCHANNEL_DESCRIPTION);
                        NotificationChannel notificationChannel = new NotificationChannel(ApkXDownloaderClient.getNotificationChannelId(), stringResource, NotificationManager.IMPORTANCE_LOW);
                        notificationChannel.setDescription(stringResource2);
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }
                    int startDownloadServiceIfRequired = DownloaderClientMarshaller.startDownloadServiceIfRequired(mActivity, PendingIntent.getActivity(mActivity, 0, intent2, PendingIntent.FLAG_IMMUTABLE), ApkXDownloaderService.class);
                    mListener.onDownloadStarted();
                    Log.i(ApkXDownloaderClient.LOG_TAG, String.format("launchDownloader - startResult %d", startDownloadServiceIfRequired));
                    if (startDownloadServiceIfRequired == 0) {
                        mListener.onDownloadStateChanged(false, false, false, true, false, 0, 0);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(ApkXDownloaderClient.LOG_TAG, String.format("launchDownloader - cannot find own package: %s", e.toString()));
                    e.printStackTrace();
                }
            }
        });
    }

    public void deleteObbFiles() {
        final File saveFilePath = new File(Helpers.getSaveFilePath(mActivity));
        final File[] files = saveFilePath.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(".obb")) {
                    Log.i(LOG_TAG, String.format("deleteObbFiles - deleting file %s", name));
                    if (!file.delete()) {
                        Log.e(LOG_TAG, String.format("deleteObbFiles - failed to delete file %s", name));
                    }
                }
            }
        }
    }

    @Override
    public void onServiceConnected(Messenger m) {
        Log.i(LOG_TAG, "onServiceConnected");
        IDownloaderService CreateProxy = DownloaderServiceMarshaller.CreateProxy(m);
        mRemoteService = CreateProxy;
        CreateProxy.onClientUpdated(mDownloaderClientStub.getMessenger());
    }

    @Override
    public void onDownloadStateChanged(int state) {
        boolean needCellularPermission;
        boolean progressIndeterminate;
        boolean isPaused;
        boolean isCompleted;
        int pausedReason;
        boolean isFailed;
        int failedReason;
        String stringResource = PackageSource.getStringResource(StringResourceId.fromInt(Helpers.getDownloaderStringResourceIDFromState(state)));
        switch (state) {
            case DOWNLOAD_STATE_IDLE, DOWNLOAD_STATE_FETCHING_URL, DOWNLOAD_STATE_CONNECTING -> {
                needCellularPermission = false;
                progressIndeterminate = true;
                isPaused = false;
                isCompleted = false;
                isFailed = false;
                pausedReason = 0;
                failedReason = 0;
            }
            case DOWNLOAD_STATE_DOWNLOADING -> {
                needCellularPermission = false;
                progressIndeterminate = false;
                isPaused = false;
                isCompleted = false;
                isFailed = false;
                pausedReason = 0;
                failedReason = 0;
            }
            case DOWNLOAD_STATE_COMPLETED -> {
                needCellularPermission = false;
                progressIndeterminate = false;
                isPaused = false;
                isCompleted = true;
                isFailed = false;
                pausedReason = 0;
                failedReason = 0;
            }
            case DOWNLOAD_STATE_PAUSED_NETWORK_UNAVAILABLE, DOWNLOAD_STATE_PAUSED_BY_REQUEST,
                 DOWNLOAD_STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION,
                 DOWNLOAD_STATE_PAUSED_NEED_CELLULAR_PERMISSION, DOWNLOAD_STATE_PAUSED_ROAMING,
                 DOWNLOAD_STATE_PAUSED_NETWORK_SETUP_FAILURE, DOWNLOAD_STATE_PAUSED_SDCARD_UNAVAILABLE,
                 DOWNLOAD_STATE_PAUSED_SDCARD_FULL, DOWNLOAD_STATE_PAUSED_WAITING_TO_RETRY -> {
                int convertStateToPausedReason = convertStateToPausedReason(state);
                needCellularPermission = state == DOWNLOAD_STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION || state == DOWNLOAD_STATE_PAUSED_NEED_CELLULAR_PERMISSION;
                pausedReason = convertStateToPausedReason;
                progressIndeterminate = false;
                isPaused = true;
                isCompleted = false;
                isFailed = false;
                failedReason = 0;
            }
            case DOWNLOAD_STATE_FAILED_UNLICENSED, DOWNLOAD_STATE_FAILED_FETCHING_URL,
                 DOWNLOAD_STATE_FAILED_SDCARD_FULL, DOWNLOAD_STATE_FAILED_CANCELED, DOWNLOAD_STATE_FAILED -> {
                failedReason = convertStateToFailedReason(state);
                needCellularPermission = false;
                progressIndeterminate = false;
                isPaused = false;
                isCompleted = false;
                isFailed = true;
                pausedReason = 0;
            }
            default -> {
                needCellularPermission = false;
                progressIndeterminate = true;
                isPaused = true;
                isCompleted = false;
                isFailed = false;
                pausedReason = 0;
                failedReason = 0;
            }
        }
        Log.i(LOG_TAG, String.format("onDownloadStateChanged - state: %s", stringResource));
        mListener.onDownloadStateChanged(needCellularPermission, progressIndeterminate, isPaused, isCompleted, isFailed, pausedReason, failedReason);
    }

    @Override
    public void onDownloadProgress(@NonNull DownloadProgressInfo progress) {
        long overallProgress = progress.mOverallProgress;
        long overallTotal = progress.mOverallTotal;
        float kbpsSpeed = progress.mCurrentSpeed;
        long timeRemainingMilliseconds = progress.mTimeRemaining;
        Log.i(LOG_TAG, String.format("onDownloadProgress - %d / %d", overallProgress, overallTotal));
        mListener.onDownloadProgress(overallProgress, overallTotal, kbpsSpeed, timeRemainingMilliseconds);
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume");
        IStub iStub = mDownloaderClientStub;
        if (iStub != null) {
            iStub.connect(mActivity);
        }
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, "onStop");
        IStub iStub = mDownloaderClientStub;
        if (iStub != null) {
            iStub.disconnect(mActivity);
        }
    }

    @Override
    public void onShowedAds() {

    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        mActivity.removeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "onActivityResult");
    }
}