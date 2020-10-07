package com.microsoft.xbox.service.network.managers.xblshared;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProtectedRunnable implements Runnable {
    private final Runnable mRunnable;

    public ProtectedRunnable(Runnable runnable2) {
        mRunnable = runnable2;
    }

    public void run() {
        boolean success = false;
        int i = 0;
        while (!success && i < 10) {
            try {
                mRunnable.run();
                success = true;
            } catch (LinkageError e) {
                e.printStackTrace();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            i++;
        }
    }
}
