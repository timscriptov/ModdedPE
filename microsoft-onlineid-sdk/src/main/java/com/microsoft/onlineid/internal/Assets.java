package com.microsoft.onlineid.internal;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Assets {
    public static String readAsset(Context applicationContext, String path) throws IOException {
        Throwable th;
        StringBuilder contents = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = applicationContext.getAssets().open(path);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream));
            try {
                contents.append(reader2.readLine());
                while (true) {
                    String line = reader2.readLine();
                    if (line == null) {
                        break;
                    }
                    contents.append('\n');
                    contents.append(line);
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e2) {
                    }
                }
                return contents.toString();
            } catch (Throwable th2) {
                th = th2;
                reader = reader2;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e3) {
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (inputStream != null) {
                inputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
            throw th;
        }
    }
}
