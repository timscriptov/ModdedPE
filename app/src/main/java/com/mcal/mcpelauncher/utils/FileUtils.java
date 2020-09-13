package com.mcal.mcpelauncher.utils;

import android.content.Context;
import android.os.Build;

import com.mcal.mcpelauncher.data.Preferences;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static void extract(@NotNull Context context, String so) {
        String path = context.getFilesDir().getAbsolutePath();
        String abi = Build.CPU_ABI;
        if (abi.contains("armeabi")) {
            FileUtils.extract(context, Build.CPU_ABI + "/" + so, path + Build.CPU_ABI + "/" + so);
        } else if (abi.contains("arm64")) {
            FileUtils.extract(context, Build.CPU_ABI + "/" + so, path + Build.CPU_ABI + "/" + so);
        } else if (abi.contains("x86")) {
            FileUtils.extract(context, Build.CPU_ABI + "/" + so, path + Build.CPU_ABI + "/" + so);
        } else if (abi.contains("x86_64")) {
            FileUtils.extract(context, Build.CPU_ABI + "/" + so, path + Build.CPU_ABI + "/" + so);
        }
    }

    public static void extract(Context context, String input, String output) {
        File file = new File(output);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            InputStream open2 = context.getResources().getAssets().open(input);
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            byte[] bArr = new byte[7168];
            while (true) {
                int read = open2.read(bArr);
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.close();
            open2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
