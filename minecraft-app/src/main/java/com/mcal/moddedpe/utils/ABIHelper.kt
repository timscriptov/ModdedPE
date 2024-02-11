package com.mcal.moddedpe.utils

import android.os.Build

object ABIHelper {
    fun getABI(): String {
        for (androidArch in Build.SUPPORTED_64_BIT_ABIS) {
            if (androidArch.contains("arm64-v8a")) {
                return "arm64-v8a"
            } else if (androidArch.contains("x86_64")) {
                return "x86"//"x86_64"
            }
        }
        for (androidArch in Build.SUPPORTED_32_BIT_ABIS) {
            if (androidArch.contains("armeabi-v7a")) {
                return "armeabi-v7a"
            } else if (androidArch.contains("x86")) {
                return "x86"
            }
        }
        @Suppress("DEPRECATION")
        return Build.CPU_ABI
    }
}
