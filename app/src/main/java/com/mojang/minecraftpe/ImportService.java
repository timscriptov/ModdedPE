package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.appboy.Constants;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ImportService extends Service {
    static final int MSG_CORRELATION_CHECK = 672;
    static final int MSG_CORRELATION_RESPONSE = 837;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        public void handleMessage(@NotNull Message message) {
            if (message.what != ImportService.MSG_CORRELATION_CHECK) {
                super.handleMessage(message);
                return;
            }
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String string = defaultSharedPreferences.getString("deviceId", "?");
            String string2 = defaultSharedPreferences.getString("LastDeviceSessionId", "");
            if (!string.equals("?")) {
                try {
                    long j = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).firstInstallTime;
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constants.APPBOY_LOCATION_TIME_INTERVAL_KEY, j);
                    bundle.putString("deviceId", string);
                    bundle.putString("sessionId", string2);
                    Message obtain = Message.obtain(null, ImportService.MSG_CORRELATION_RESPONSE);
                    obtain.setData(bundle);
                    message.replyTo.send(obtain);
                } catch (NameNotFoundException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}