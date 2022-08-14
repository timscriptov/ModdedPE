package com.mojang.minecraftpe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;

import com.mojang.minecraftpe.platforms.Platform;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

@SuppressLint({"DefaultLocale"})
public class HardwareInformation {
    private final Context context;

    HardwareInformation(Context context) {
        this.context = context;
    }

    @NotNull
    public static String getDeviceModelName() {
        return Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL;
    }

    public String getAndroidVersion() {
        if (((MainActivity) this.context).isChromebook()) {
            return "ChromeOS " + Build.VERSION.RELEASE;
        }
        return "Android " + Build.VERSION.RELEASE;
    }


    public static @NotNull String getLocale() {
        return Locale.getDefault().toString();
    }

    public static String getCPUType() {
        return Platform.createPlatform(false).getABIS();
    }

    @NotNull
    @Contract(pure = true)
    public static String getCPUName() {
        return "unknown";
    }

    @NotNull
    @Contract(pure = true)
    public static String getCPUFeatures() {
        return "unknown";
    }

    public static int getNumCores() {
        return 1;
    }

    @NotNull
    @Contract(" -> new")
    public static CPUInfo getCPUInfo() {
        Map<String, String> list = new HashMap<>();
        int processorCount = 0;
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                Pattern pattern = Pattern.compile("(\\w*)\\s*:\\s([^\\n]*)");
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find() && matcher.groupCount() == 2) {
                        if (!list.containsKey(matcher.group(1))) {
                            list.put(matcher.group(1), matcher.group(2));
                        }
                        if (matcher.group(1).contentEquals("processor")) {
                            processorCount++;
                        }
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new CPUInfo(list, processorCount);
    }

    @SuppressLint("HardwareIds")
    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    public static String getBoard() {
        return Build.BOARD;
    }

    public String getSecureId() {
        return "SecureId";
    }

    public String getInstallerPackageName() {
        return "installer.package.name";
    }

    public int getSignaturesHashCode() {
        return 0;
    }

    public boolean getIsRooted() {
        return false;
    }

    public static class CPUInfo {
        private final Map<String, String> cpuLines;
        private final int numberCPUCores;

        public CPUInfo(Map<String, String> cpuLines, int numberCPUCores) {
            this.cpuLines = cpuLines;
            this.numberCPUCores = numberCPUCores;
        }

        public String getCPULine(String line) {
            if (this.cpuLines.containsKey(line)) {
                return cpuLines.get(line);
            }
            return "";
        }

        public int getNumberCPUCores() {
            return numberCPUCores;
        }
    }
}