package com.mojang.minecraftpe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.NonNull;

public class ThermalMonitor extends BroadcastReceiver {
    private final Context mContext;
    private boolean mLowPowerModeEnabled = false;

    public ThermalMonitor(@NonNull Context context) {
        this.mContext = context;
        context.registerReceiver(this, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
        readPowerMode(context);
    }

    protected void finalize() {
        this.mContext.unregisterReceiver(this);
    }

    public boolean getLowPowerModeEnabled() {
        return this.mLowPowerModeEnabled;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        readPowerMode(context);
    }

    private void readPowerMode(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.mLowPowerModeEnabled = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isPowerSaveMode();
        }
    }
}