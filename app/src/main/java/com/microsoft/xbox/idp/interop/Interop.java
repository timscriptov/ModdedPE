package com.microsoft.xbox.idp.interop;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Interop {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getApplicationContext() {
        return context;
    }

    public enum ErrorType {
        BAN(0),
        CREATION(1),
        OFFLINE(2),
        CATCHALL(3);

        private final int id;

        ErrorType(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }
    }
}
