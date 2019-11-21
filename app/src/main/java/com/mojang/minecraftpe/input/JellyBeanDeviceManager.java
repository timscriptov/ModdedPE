package com.mojang.minecraftpe.input;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;

@TargetApi(16)
public class JellyBeanDeviceManager extends InputDeviceManager implements InputDeviceListener {
    private final InputManager inputManager;

    public native void onInputDeviceAddedNative(int i);
    public native void onInputDeviceChangedNative(int i);
    public native void onInputDeviceRemovedNative(int i);
    public native void setCreteControllerNative(int i, boolean z);
    public native void setDoubleTriggersSupportedNative(boolean z);

    @SuppressLint("WrongConstant")
    JellyBeanDeviceManager(Context ctx) {
        inputManager = (InputManager) ctx.getSystemService("input");
    }

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
