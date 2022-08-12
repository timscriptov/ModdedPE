package com.mojang.minecraftpe;

import android.os.Bundle;
import android.util.Log;

public class InstrumentationRegistryHelper {
    public static boolean getIsRunningInAppCenter() {
        try {
            String string = ((Bundle) Class.forName("android.support.test.InstrumentationRegistry").getMethod("getArguments", new Class[0]).invoke(null, new Object[0])).getString("RUNNING_IN_APP_CENTER");
            if ("1".equals(string)) {
                Log.i("ModdedPE", "Automation: getIsRunningInAppCenter - we are RunningInAppCenter, response from android.support.test.InstrumentationRegistry");
                return true;
            }
            Log.i("ModdedPE", "Automation: getIsRunningInAppCenter - we are NOT RunningInAppCenter, response from android.support.test.InstrumentationRegistry was [" + string + "]");
            return false;
        } catch (ClassNotFoundException unused) {
            Log.i("ModdedPE", "Automation: getIsRunningInAppCenter - determined that we are NOT RunningInAppCenter because android.support.test.InstrumentationRegistry was not found");
        } catch (Exception e) {
            Log.i("ModdedPE", "Automation: GetIsRunningInAppCenter - Caught a " + e.getClass().getName() + " checking " + "android.support.test.InstrumentationRegistry");
        }
        return false;
    }
}