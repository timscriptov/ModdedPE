package com.microsoft.xbox.toolkit.system;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.NotNull;

import java.net.NetworkInterface;
import java.util.Collections;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SystemUtil {
    private static final int MAX_SD_SCREEN_PIXELS = 384000;

    public static int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    public static int DIPtoPixels(float dip) {
        return (int) TypedValue.applyDimension(1, dip, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static int SPtoPixels(float sp) {
        return (int) TypedValue.applyDimension(2, sp, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().heightPixels;
    }

    @SuppressLint("WrongConstant")
    public static int getColorDepth() {
        PixelFormat.getPixelFormatInfo(1, (PixelFormat) null);
        //return null.bitsPerPixel;
        return 0;
    }

    public static float getScreenWidthInches() {
        return ((float) getScreenWidth()) / XboxTcuiSdk.getResources().getDisplayMetrics().xdpi;
    }

    public static float getScreenHeightInches() {
        return ((float) getScreenHeight()) / XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static float getYDPI() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static int getRotation() {
        return getDisplay().getRotation();
    }

    public static int getOrientation() {
        int rotation = getRotation();
        if (rotation == 0 || rotation == 2) {
            return 1;
        }
        return 2;
    }

    public static boolean isHDScreen() {
        return getScreenHeight() * getScreenWidth() > MAX_SD_SCREEN_PIXELS;
    }

    public static boolean isSlate() {
        return Math.sqrt(Math.pow((double) getScreenWidthInches(), 2.0d) + Math.pow((double) getScreenHeightInches(), 2.0d)) > 6.0d;
    }

    @NotNull
    public static String getDeviceType() {
        XLEAssert.assertTrue(false);
        return "";
    }

    private static Display getDisplay() {
        return ((WindowManager) XboxTcuiSdk.getSystemService("window")).getDefaultDisplay();
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static float getScreenWidthHeightAspectRatio() {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        if (screenWidth <= 0 || screenHeight <= 0) {
            return 0.0f;
        }
        if (screenWidth > screenHeight) {
            return ((float) screenWidth) / ((float) screenHeight);
        }
        return ((float) screenHeight) / ((float) screenWidth);
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId() {
        return Settings.Secure.getString(XboxTcuiSdk.getContentResolver(), "android_id");
    }

    public static String getDeviceModelName() {
        return Build.MODEL;
    }

    @NotNull
    public static String getMACAddress(String interfaceName) {
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (interfaceName == null || intf.getName().equalsIgnoreCase(interfaceName)) {
                    byte[] mac = intf.getHardwareAddress();
                    if (mac == null) {
                        return "";
                    }
                    StringBuilder buf = new StringBuilder();
                    for (int idx = 0; idx < mac.length; idx++) {
                        buf.append(String.format("%02X:", new Object[]{Byte.valueOf(mac[idx])}));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void TEST_randomSleep(int maxSeconds) {
        XLEAssert.assertTrue(false);
    }

    public static boolean TEST_randomFalseOutOf(int max) {
        XLEAssert.assertTrue(false);
        return true;
    }

    public static boolean isKindle() {
        String manufecturer = Build.MANUFACTURER;
        return manufecturer != null && "AMAZON".compareToIgnoreCase(manufecturer) == 0;
    }
}
