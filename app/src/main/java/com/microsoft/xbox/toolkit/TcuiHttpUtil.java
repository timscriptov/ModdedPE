package com.microsoft.xbox.toolkit;

import android.util.Pair;

import com.microsoft.xbox.idp.util.HttpCall;

import org.apache.http.protocol.HTTP;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TcuiHttpUtil {
    public static <T> T getResponseSync(@NotNull HttpCall httpCall, final Class<T> returnClass) throws XLEException {
        final AtomicReference<Pair<Boolean, T>> notifier = new AtomicReference<>();
        notifier.set(new Pair(false, null));
        httpCall.getResponseAsync((httpStatus, stream, headers) -> {
            T result = (httpStatus >= 200 || httpStatus <= 299) ? GsonUtil.deserializeJson(stream, returnClass) : null;
            synchronized (notifier) {
                notifier.set(new Pair(true, result));
                notifier.notify();
            }
        });
        synchronized (notifier) {
            while (!(notifier.get().first).booleanValue()) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return notifier.get().second;
    }

    public static boolean getResponseSyncSucceeded(@NotNull HttpCall httpCall, final List<Integer> acceptableStatusCodes) {
        final AtomicReference<Boolean> notifier = new AtomicReference<>();
        httpCall.getResponseAsync((httpStatus, stream, headers) -> {
            synchronized (notifier) {
                notifier.set(Boolean.valueOf(acceptableStatusCodes.contains(Integer.valueOf(httpStatus))));
                notifier.notify();
            }
        });
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return notifier.get().booleanValue();
    }

    public static String getResponseBodySync(@NotNull HttpCall httpCall) throws XLEException {
        final AtomicReference<Pair<Boolean, String>> notifier = new AtomicReference<>();
        notifier.set(new Pair(false, null));
        httpCall.getResponseAsync((HttpCall.Callback) (httpStatus, stream, headers) -> {
            if (httpStatus < 200 || httpStatus > 299) {
                synchronized (notifier) {
                    notifier.set(new Pair(true, (Object) null));
                    notifier.notify();
                }
                return;
            }
            String responseBody = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HTTP.UTF_8), 4096);
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line + "\n");
                }
                responseBody = sb.toString();
            } catch (IOException ioe) {
                XLEAssert.assertTrue("Failed to read ShortCircuitProfileMessage string - " + ioe.getMessage(), false);
            }
            synchronized (notifier) {
                notifier.set(new Pair(true, responseBody));
                notifier.notify();
            }
        });
        synchronized (notifier) {
            while (!(notifier.get().first).booleanValue()) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return notifier.get().second;
    }

    public static <T> void throwIfNullOrFalse(T result) throws XLEException {
        if (result == null && !Boolean.getBoolean(result.toString())) {
            throw new XLEException(2);
        }
    }
}