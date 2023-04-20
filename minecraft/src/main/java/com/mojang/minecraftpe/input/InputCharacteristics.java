package com.mojang.minecraftpe.input;

import android.view.InputDevice;

import androidx.core.view.InputDeviceCompat;

import java.io.File;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class InputCharacteristics {
    public static boolean allControllersHaveDoubleTriggers() {
        boolean supportsL2;
        boolean supportsR2;
        boolean supportsDoubleTriggers = false;
        int[] ids = InputDevice.getDeviceIds();
        for (int device : ids) {
            InputDevice device2 = InputDevice.getDevice(device);
            if (!device2.isVirtual() && device2.getControllerNumber() > 0 && (device2.getSources() & InputDeviceCompat.SOURCE_GAMEPAD) != 0) {
                boolean[] supportedTriggerKeys = device2.hasKeys(102, 103, 104, 105);
                supportsDoubleTriggers = supportedTriggerKeys.length == 4;
                int x = 0;
                while (true) {
                    if (x >= supportedTriggerKeys.length) {
                        break;
                    } else if (!supportedTriggerKeys[x]) {
                        supportsDoubleTriggers = false;
                        break;
                    } else {
                        x++;
                    }
                }
                if (!supportsDoubleTriggers && supportedTriggerKeys[0] && supportedTriggerKeys[1]) {
                    supportsL2 = device2.getMotionRange(17) != null || device2.getMotionRange(23) != null;
                    supportsR2 = device2.getMotionRange(18) != null || device2.getMotionRange(22) != null;
                    supportsDoubleTriggers = supportsL2 && supportsR2;
                }
                if (supportsDoubleTriggers && device2.getName().contains("EI-GP20")) {
                    supportsDoubleTriggers = false;
                }
                if (!supportsDoubleTriggers) {
                    return supportsDoubleTriggers;
                }
            }
        }
        return supportsDoubleTriggers;
    }

    public static boolean isCreteController(int deviceId) {
        InputDevice device = InputDevice.getDevice(deviceId);
        if (device == null || device.isVirtual() || device.getControllerNumber() <= 0 || (device.getSources() & InputDeviceCompat.SOURCE_GAMEPAD) == 0) {
            return false;
        }
        if (!(device.getVendorId() == 1118) || !(device.getProductId() == 736)) {
            return false;
        }
        for (String filePath : new String[]{"/system/usr/keylayout/Vendor_045e_Product_02e0.kl", "/data/system/devices/keylayout/Vendor_045e_Product_02e0.kl"}) {
            if (new File(filePath).exists()) {
                return false;
            }
        }
        return true;
    }
}