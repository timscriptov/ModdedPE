package com.mojang.minecraftpe;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ImportService extends Service {
    static final int MSG_CORRELATION_CHECK = 672;
    static final int MSG_CORRELATION_RESPONSE = 837;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == ImportService.MSG_CORRELATION_CHECK) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String string = defaultSharedPreferences.getString("deviceId", "?");
                String string2 = defaultSharedPreferences.getString("LastDeviceSessionId", "");
                if (string.equals("?")) {
                    return;
                }
                try {
                    long j = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).firstInstallTime;
                    Bundle bundle = new Bundle();
                    bundle.putLong("time", j);
                    bundle.putString("deviceId", string);
                    bundle.putString("sessionId", string2);
                    Message obtain = Message.obtain(null, ImportService.MSG_CORRELATION_RESPONSE);
                    obtain.setData(bundle);
                    msg.replyTo.send(obtain);
                    return;
                } catch (PackageManager.NameNotFoundException | RemoteException unused) {
                    return;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mMessenger.getBinder();
    }
}