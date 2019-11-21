package com.mojang.minecraftpe.input;

import android.os.Build.VERSION;
import android.view.InputDevice;
import java.io.File;

public class InputCharacteristics {
    public static boolean allControllersHaveDoubleTriggers() {
        boolean supportsL2;
        boolean supportsR2;
        boolean supportsDoubleTriggers = false;
        if (VERSION.SDK_INT < 19) {
            return true;
        }
        int[] ids = InputDevice.getDeviceIds();
        for (int device : ids) {
            InputDevice device2 = InputDevice.getDevice(device);
            if (!device2.isVirtual() && device2.getControllerNumber() > 0 && (device2.getSources() & 0x401) != 0) {
                boolean[] supportedTriggerKeys = device2.hasKeys(new int[]{102, 103, 104, 105});
                if (supportedTriggerKeys.length == 4) {
                    supportsDoubleTriggers = true;
                } else {
                    supportsDoubleTriggers = false;
                }
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
                    if (device2.getMotionRange(17) == null && device2.getMotionRange(23) == null) {
                        supportsL2 = false;
                    } else {
                        supportsL2 = true;
                    }
                    if (device2.getMotionRange(18) == null && device2.getMotionRange(22) == null) {
                        supportsR2 = false;
                    } else {
                        supportsR2 = true;
                    }
                    if (!supportsL2 || !supportsR2) {
                        supportsDoubleTriggers = false;
                    } else {
                        supportsDoubleTriggers = true;
                    }
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
        if (VERSION.SDK_INT < 19 || device == null || device.isVirtual() || device.getControllerNumber() <= 0 || (device.getSources() & 0x401) == 0) {
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
