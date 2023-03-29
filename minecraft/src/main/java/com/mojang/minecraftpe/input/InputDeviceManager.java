package com.mojang.minecraftpe.input;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class InputDeviceManager {
    @NotNull
    @Contract("_ -> new")
    public static InputDeviceManager create(Context ctx) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new JellyBeanDeviceManager(ctx);
        }
        return new DefaultDeviceManager();
    }

    public abstract void register();

    public abstract void unregister();

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
}