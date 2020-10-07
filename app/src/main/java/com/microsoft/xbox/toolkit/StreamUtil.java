package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class StreamUtil {
    @Nullable
    public static byte[] CreateByteArray(InputStream stream) {
        ByteArrayOutputStream rv = new ByteArrayOutputStream();
        try {
            CopyStream(rv, stream);
            return rv.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static void CopyStream(OutputStream output, @NotNull InputStream input) throws IOException {
        byte[] buffer = new byte[16384];
        while (true) {
            int readlen = input.read(buffer);
            if (readlen > 0) {
                output.write(buffer, 0, readlen);
            } else {
                output.flush();
                return;
            }
        }
    }

    @Nullable
    public static String ReadAsString(InputStream stream) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    return builder.toString();
                }
                builder.append(line);
                builder.append(10);
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static void consumeAndClose(InputStream stream) throws IOException {
        InputStream s = new BufferedInputStream(stream);
        do {
            try {
            } finally {
                s.close();
            }
        } while (s.read() != -1);
    }

    @NotNull
    @Contract("null -> fail")
    public static byte[] HexStringToByteArray(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException("hexString invalid");
        }
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        XLEAssert.assertTrue(hexString.length() % 2 == 0);
        byte[] rv = new byte[(hexString.length() / 2)];
        for (int i = 0; i < hexString.length(); i += 2) {
            rv[i / 2] = Byte.parseByte(hexString.substring(i, i + 2), 16);
        }
        return rv;
    }
}
