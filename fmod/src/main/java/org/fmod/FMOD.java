package org.fmod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;

public class FMOD {
    private static Context gContext;
    private static PluginBroadcastReceiver gPluginBroadcastReceiver = new PluginBroadcastReceiver();

    public static native void OutputAAudioHeadphonesChanged();

    public static void init(Context context) {
        gContext = context;
        if (context != null) {
            gContext.registerReceiver(gPluginBroadcastReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
        }
    }

    public static void close() {
        Context context = gContext;
        if (context != null) {
            context.unregisterReceiver(gPluginBroadcastReceiver);
        }
        gContext = null;
    }

    public static boolean checkInit() {
        return gContext != null;
    }

    public static @Nullable AssetManager getAssetManager() {
        Context context = gContext;
        if (context != null) {
            return context.getAssets();
        }
        return null;
    }

    public static boolean supportsLowLatency() {
        int outputBlockSize = getOutputBlockSize();
        boolean zLowLatencyFlag = lowLatencyFlag();
        boolean zProAudioFlag = proAudioFlag();
        boolean z = outputBlockSize > 0 && outputBlockSize <= 1024;
        boolean zIsBluetoothOn = isBluetoothOn();
        Log.i("fmod", "FMOD::supportsLowLatency                 : Low latency = " + zLowLatencyFlag + ", Pro Audio = " + zProAudioFlag + ", Bluetooth On = " + zIsBluetoothOn + ", Acceptable Block Size = " + z + " (" + outputBlockSize + ")");
        return z && zLowLatencyFlag && !zIsBluetoothOn;
    }

    public static boolean lowLatencyFlag() {
        if (gContext != null) {
            return gContext.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
        }
        return false;
    }

    public static boolean proAudioFlag() {
        if (gContext != null) {
            return gContext.getPackageManager().hasSystemFeature("android.hardware.audio.pro");
        }
        return false;
    }

    public static boolean supportsAAudio() {
        return Build.VERSION.SDK_INT >= 27;
    }

    public static int getOutputSampleRate() {
        String property;
        if (gContext == null || (property = ((AudioManager) gContext.getSystemService(Context.AUDIO_SERVICE)).getProperty("android.media.property.OUTPUT_SAMPLE_RATE")) == null) {
            return 0;
        }
        return Integer.parseInt(property);
    }

    public static int getOutputBlockSize() {
        String property;
        if (gContext == null || (property = ((AudioManager) gContext.getSystemService(Context.AUDIO_SERVICE)).getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")) == null) {
            return 0;
        }
        return Integer.parseInt(property);
    }

    public static boolean isBluetoothOn() {
        Context context = gContext;
        if (context == null) {
            return false;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isBluetoothA2dpOn() || audioManager.isBluetoothScoOn();
    }

    public static int fileDescriptorFromUri(String str) {
        if (gContext != null) {
            try {
                return gContext.getContentResolver().openFileDescriptor(Uri.parse(str), "r").detachFd();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    static class PluginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FMOD.OutputAAudioHeadphonesChanged();
        }
    }
}
