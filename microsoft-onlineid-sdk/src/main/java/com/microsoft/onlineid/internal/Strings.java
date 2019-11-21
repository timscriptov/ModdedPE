package com.microsoft.onlineid.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Strings {
    public static final Charset Utf8Charset = Charset.forName("UTF-8");

    public static void verifyArgumentNotNullOrEmpty(String string, String argumentName) {
        Objects.verifyArgumentNotNull(string, argumentName);
        if (string.isEmpty()) {
            throw new IllegalArgumentException(argumentName + " must not be empty.");
        }
    }

    public static boolean equalsIgnoreCase(String string1, String string2) {
        if (string1 == string2) {
            return true;
        }
        if (string1 == null || string2 == null) {
            return false;
        }
        return string1.equalsIgnoreCase(string2);
    }

    public static String fromStream(InputStream inputStream, Charset charset) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream, charset);
        StringBuilder output = new StringBuilder();
        char[] buffer = new char[1024];
        while (true) {
            try {
                int length = reader.read(buffer);
                if (length < 0) {
                    break;
                }
                output.append(buffer, 0, length);
            } finally {
                reader.close();
            }
        }
        return output.toString();
    }

    public static String pluralize(long value, String singular, String plural) {
        StringBuilder append = new StringBuilder().append(value).append(" ");
        if (value != 1) {
            singular = plural;
        }
        return append.append(singular).toString();
    }
}
