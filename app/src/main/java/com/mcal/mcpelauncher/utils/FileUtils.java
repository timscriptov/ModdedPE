package com.mcal.mcpelauncher.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mcal.pesdk.utils.ABIInfo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void extract(@NotNull Context context, String so) {
        String abi = ABIInfo.getABI();
        File lib = new File(context.getCacheDir().getPath() + "/lib");
        if (!lib.exists()) {
            lib.mkdir();
        }

        File path = new File(lib + "/" + ABIInfo.getABI());
        if (!path.exists()) {
            path.mkdir();
        }
        extract(context, ABIInfo.getABI() + "/" + so, path + "/" + so);
    }

    public static void extract(@NotNull Context context, String input, String output) {
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
