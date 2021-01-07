package com.microsoft.xbox.toolkit;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEThread extends Thread {
    public XLEThread(Runnable runnable, String str) {
        super(runnable, str);
        setUncaughtExceptionHandler(XLEUnhandledExceptionHandler.Instance);
    }
}
