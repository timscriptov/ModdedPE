package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;

import org.jetbrains.annotations.NotNull;

public class ThermalMonitor extends BroadcastReceiver {
    private final Context mContext;
    private boolean mLowPowerModeEnabled = false;

    public ThermalMonitor(@NotNull Context context) {
        mContext = context;
        context.registerReceiver(this, new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED));
        readPowerMode(context);
    }

    @Override
    public void finalize() {
        mContext.unregisterReceiver(this);
    }

    public boolean getLowPowerModeEnabled() {
        return mLowPowerModeEnabled;
    }

    public void onReceive(Context context, Intent intent) {
        readPowerMode(context);
    }

    @SuppressLint({"WrongConstant"})
    private void readPowerMode(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            mLowPowerModeEnabled = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isPowerSaveMode();
        }
    }
}