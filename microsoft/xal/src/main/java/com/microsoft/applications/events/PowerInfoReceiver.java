package com.microsoft.applications.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * 05.10.2025
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
class PowerInfoReceiver extends BroadcastReceiver {
    private final HttpClient m_parent;

    public PowerInfoReceiver(HttpClient httpClient) {
        this.m_parent = httpClient;
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            return;
        }

        int status = intent.getIntExtra("status", -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;

        this.m_parent.onPowerChange(isCharging, isFull);
    }
}
