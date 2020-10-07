package com.microsoft.xbox.toolkit;

import android.app.ActivityManager;
import android.os.Debug;

import com.microsoft.xboxtcui.XboxTcuiSdk;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MemoryMonitor {
    public static final int KB_TO_BYTES = 1024;
    public static final int MB_TO_BYTES = 1048576;
    public static final int MB_TO_KB = 1024;
    private static MemoryMonitor instance = new MemoryMonitor();
    private Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();

    private MemoryMonitor() {
    }

    public static MemoryMonitor instance() {
        return instance;
    }

    public static synchronized int getTotalPss() {
        int totalPss;
        synchronized (MemoryMonitor.class) {
            Debug.getMemoryInfo(instance.memoryInfo);
            totalPss = instance.memoryInfo.getTotalPss();
        }
        return totalPss;
    }

    public synchronized int getDalvikFreeMb() {
        return getDalvikFreeKb() / 1024;
    }

    public synchronized int getDalvikFreeKb() {
        Debug.getMemoryInfo(memoryInfo);
        return (((ActivityManager) XboxTcuiSdk.getSystemService("activity")).getMemoryClass() * 1024) - getDalvikUsedKb();
    }

    public synchronized int getDalvikUsedKb() {
        Debug.getMemoryInfo(memoryInfo);
        return memoryInfo.dalvikPss;
    }

    public synchronized int getUsedKb() {
        Debug.getMemoryInfo(memoryInfo);
        return memoryInfo.dalvikPss + memoryInfo.nativePss;
    }

    public int getMemoryClass() {
        return ((ActivityManager) XboxTcuiSdk.getSystemService("activity")).getLargeMemoryClass();
    }
}
