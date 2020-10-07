package com.microsoft.xbox.toolkit;

import android.os.Handler;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ThreadManager {
    public static Handler Handler;
    public static Thread UIThread;

    public static void UIThreadPost(Runnable runnable) {
        UIThreadPostDelayed(runnable, 0);
    }

    public static void UIThreadPostDelayed(Runnable runnable, long delayMS) {
        Handler.postDelayed(runnable, delayMS);
    }

    public static void UIThreadSend(final Runnable runnable) {
        if (UIThread == Thread.currentThread()) {
            runnable.run();
            return;
        }
        final Ready actionComplete = new Ready();
        Handler.post(() -> {
            runnable.run();
            actionComplete.setReady();
        });
        actionComplete.waitForReady();
    }
}
