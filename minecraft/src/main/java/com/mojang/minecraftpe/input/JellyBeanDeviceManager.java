package com.mojang.minecraftpe.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.input.InputManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class JellyBeanDeviceManager extends InputDeviceManager implements InputManager.InputDeviceListener {
    private final InputManager inputManager;

    @SuppressLint("WrongConstant")
    JellyBeanDeviceManager(@NotNull Context ctx) {
        inputManager = (InputManager) ctx.getSystemService("input");
    }

    public native void onInputDeviceAddedNative(int i);

    public native void onInputDeviceChangedNative(int i);

    public native void onInputDeviceRemovedNative(int i);

    public native void setCreteControllerNative(int i, boolean z);

    public native void setDoubleTriggersSupportedNative(boolean z);

    public void register() {
        int[] ids = inputManager.getInputDeviceIds();
        inputManager.registerInputDeviceListener(this, null);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        for (int i = 0; i < ids.length; i++) {
            setCreteControllerNative(ids[i], InputCharacteristics.isCreteController(ids[i]));
        }
    }

    public void unregister() {
        inputManager.unregisterInputDeviceListener(this);
    }

    public void onInputDeviceAdded(int deviceId) {
        onInputDeviceAddedNative(deviceId);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        setCreteControllerNative(deviceId, InputCharacteristics.isCreteController(deviceId));
    }

    public void onInputDeviceChanged(int deviceId) {
        onInputDeviceChangedNative(deviceId);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        setCreteControllerNative(deviceId, InputCharacteristics.isCreteController(deviceId));
    }

    public void onInputDeviceRemoved(int deviceId) {
        onInputDeviceRemovedNative(deviceId);
        setDoubleTriggersSupportedNative(InputCharacteristics.allControllersHaveDoubleTriggers());
        setCreteControllerNative(deviceId, InputCharacteristics.isCreteController(deviceId));
    }
}