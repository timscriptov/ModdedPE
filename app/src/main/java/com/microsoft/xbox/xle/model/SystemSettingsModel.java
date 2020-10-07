package com.microsoft.xbox.xle.model;

import android.os.Build;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.serialization.Version;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.SmartglassSettings;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashSet;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SystemSettingsModel extends ModelBase<Version> {
    private final HashSet<String> hiddenMruItems;
    private final SingleEntryLoadingStatus smartglassSettingsLoadingStatus;
    private int latestVersion;
    private String marketUrl;
    private int minRequiredOSVersion;
    private int minVersion;
    private int[] remoteControlSpecialTitleIds;
    private SmartglassSettings smartglassSettings;
    private OnUpdateExistListener updateExistListener;

    private SystemSettingsModel() {
        minRequiredOSVersion = 0;
        minVersion = 0;
        latestVersion = 0;
        hiddenMruItems = new HashSet<>();
        smartglassSettingsLoadingStatus = new SingleEntryLoadingStatus();
    }

    public static SystemSettingsModel getInstance() {
        return SystemSettingsModelContainer.instance;
    }

    public void setOnUpdateExistListener(OnUpdateExistListener listener) {
        updateExistListener = listener;
    }

    public boolean getHasUpdate(int currentVersionCode) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return Build.VERSION.SDK_INT >= minRequiredOSVersion && getLatestVersion() > currentVersionCode;
    }

    public boolean getMustUpdate(int currentVersionCode) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return Build.VERSION.SDK_INT >= minRequiredOSVersion && getMinimumVersion() > currentVersionCode;
    }

    public int[] getRemoteControlSpecialTitleIds() {
        return remoteControlSpecialTitleIds;
    }

    public String getMarketUrl() {
        return marketUrl;
    }

    public boolean isInHiddenMruItems(String titleId) {
        return hiddenMruItems.contains(titleId);
    }

    public int getLatestVersion() {
        return latestVersion;
    }

    private int getMinimumVersion() {
        return minVersion;
    }

    public void loadAsync(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        DataLoadUtil.StartLoadFromUI(forceRefresh, lifetime, (Date) null, smartglassSettingsLoadingStatus, new GetSmartglassSettingsRunner(this));
    }

    public AsyncResult<SmartglassSettings> loadSystemSettings(boolean forceRefresh) {
        return DataLoadUtil.Load(forceRefresh, lifetime, (Date) null, smartglassSettingsLoadingStatus, new GetSmartglassSettingsRunner(this));
    }

    public void onGetSmartglassSettingsCompleted(@NotNull AsyncResult<SmartglassSettings> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            smartglassSettings = result.getResult();
            if (smartglassSettings != null) {
                minRequiredOSVersion = smartglassSettings.ANDROID_VERSIONMINOS;
                minVersion = smartglassSettings.ANDROID_VERSIONMIN;
                latestVersion = smartglassSettings.ANDROID_VERSIONLATEST;
                marketUrl = smartglassSettings.ANDROID_VERSIONURL;
                populateHiddenMruItems(smartglassSettings.HIDDEN_MRU_ITEMS);
                populateRemoteControlSpecialTitleIds(smartglassSettings.REMOTE_CONTROL_SPECIALS);
                if (updateExistListener == null) {
                    return;
                }
                if (getMustUpdate(ProjectSpecificDataProvider.getInstance().getVersionCode())) {
                    updateExistListener.onMustUpdate();
                } else if (getHasUpdate(ProjectSpecificDataProvider.getInstance().getVersionCode())) {
                    updateExistListener.onOptionalUpdate();
                }
            }
        }
    }

    private void populateHiddenMruItems(String list) {
        String[] buf;
        hiddenMruItems.clear();
        if (list != null && (buf = list.split(",")) != null) {
            for (String titleId : buf) {
                hiddenMruItems.add(titleId);
            }
        }
    }

    private void populateRemoteControlSpecialTitleIds(String commaDelimited) {
        String[] buf;
        if (commaDelimited != null && (buf = commaDelimited.split(",")) != null) {
            remoteControlSpecialTitleIds = new int[buf.length];
            int length = buf.length;
            int i = 0;
            int index = 0;
            while (i < length) {
                int id = 0;
                try {
                    id = Integer.parseInt(buf[i]);
                } catch (NumberFormatException e) {
                }
                remoteControlSpecialTitleIds[index] = id;
                i++;
                index++;
            }
        }
    }

    public interface OnUpdateExistListener {
        void onMustUpdate();

        void onOptionalUpdate();
    }

    private static class SystemSettingsModelContainer {
        public static SystemSettingsModel instance = new SystemSettingsModel();

        private SystemSettingsModelContainer() {
        }
    }

    private class GetSmartglassSettingsRunner extends IDataLoaderRunnable<SmartglassSettings> {
        private final SystemSettingsModel caller;

        public GetSmartglassSettingsRunner(SystemSettingsModel caller2) {
            caller = caller2;
        }

        public SmartglassSettings buildData() throws XLEException {
            return null;
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<SmartglassSettings> result) {
            caller.onGetSmartglassSettingsCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_SETTINGS;
        }
    }
}