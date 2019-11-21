package com.mojang.minecraftpe.input;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;

public abstract class InputDeviceManager {

    public static class DefaultDeviceManager extends InputDeviceManager {
        private DefaultDeviceManager() {
        }

        public void register() {
            Log.w("ModdedPE", "INPUT Noop register device manager");
        }

        public void unregister() {
                Log.w("ModdedPE", "INPUT Noop unregister device manager");
        }
    }

    public abstract void register();

    public abstract void unregister();

    public static InputDeviceManager create(Context ctx) {
        if (VERSION.SDK_INT >= 16) {
            return new JellyBeanDeviceManager(ctx);
        }
        return new DefaultDeviceManager();
    }
}
