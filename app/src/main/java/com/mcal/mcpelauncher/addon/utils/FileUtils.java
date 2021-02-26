package com.mcal.mcpelauncher.addon.utils;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static void copy(Context context, String asset_soname, String str2, String str3) {
        String so_dest = str2 + File.separator + str3;
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(so_dest);
        try {
            if (file.exists()) {
                boolean result;
                InputStream open = context.getResources().getAssets().open(asset_soname);
                InputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(open);
                BufferedInputStream bufferedInputStream2 = new BufferedInputStream(fileInputStream);
                //Log.d(TAG, "check is same file");
                result = isSameFile(bufferedInputStream, bufferedInputStream2);
                open.close();
                fileInputStream.close();
                bufferedInputStream.close();
                bufferedInputStream2.close();
                if (result) {
                    return;
                }
            }
            InputStream open2 = context.getResources().getAssets().open(asset_soname);
            FileOutputStream fileOutputStream = new FileOutputStream(so_dest);
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
            try {
                Runtime.getRuntime().exec("chmod 755 " + so_dest);
            } catch (Exception ignored) {
            }
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public static boolean isSameFile(@NotNull BufferedInputStream bufferedInputStream, @NotNull BufferedInputStream bufferedInputStream2) {
        try {
            int available = bufferedInputStream.available();
            int available2 = bufferedInputStream2.available();
            if (available != available2) {
                return false;
            }
            byte[] bArr = new byte[available];
            byte[] bArr2 = new byte[available2];
            bufferedInputStream.read(bArr);
            bufferedInputStream2.read(bArr2);
            for (available2 = 0; available2 < available; available2++) {
                if (bArr[available2] != bArr2[available2]) {
                    return false;
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
