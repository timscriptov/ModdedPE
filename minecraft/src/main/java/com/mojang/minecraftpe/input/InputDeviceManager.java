package com.mojang.minecraftpe.input;

import android.content.Context;
import android.util.Log;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public abstract class InputDeviceManager {
    public abstract void register();

    public abstract void unregister();

    @Contract("_ -> new")
    public static @NotNull InputDeviceManager create(Context context) {
        return new JellyBeanDeviceManager(context);
    }

    public static class DefaultDeviceManager extends InputDeviceManager {
        private DefaultDeviceManager() {
        }

        @Override
        public void register() {
            Log.w("ModdedPE", "INPUT Noop register device manager");
        }

        @Override
        public void unregister() {
            Log.w("ModdedPE", "INPUT Noop unregister device manager");
        }
    }
}