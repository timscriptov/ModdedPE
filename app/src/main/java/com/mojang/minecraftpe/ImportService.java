package com.mojang.minecraftpe;

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

public class ImportService extends Service {
    static final int MSG_CORRELATION_CHECK = 672;
    static final int MSG_CORRELATION_RESPONSE = 837;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CORRELATION_CHECK:
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String deviceId = prefs.getString("deviceId", "?");
                    String lastSessionId = prefs.getString("LastDeviceSessionId", "");
                    if (deviceId != "?") {
                        try {
                            long timestamp = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).firstInstallTime;
                            Bundle b = new Bundle();
                            b.putLong("time", timestamp);
                            b.putString("deviceId", deviceId);
                            b.putString("sessionId", lastSessionId);
                            Message nmsg = Message.obtain(null, MSG_CORRELATION_RESPONSE);
                            nmsg.setData(b);
                            try {
                                msg.replyTo.send(nmsg);
                                return;
                            } catch (RemoteException e) {
                                return;
                            }
                        } catch (NameNotFoundException e2) {
                            return;
                        }
                    }
                    return;
                default:
                    super.handleMessage(msg);
                    return;
            }
        }
    }
}
