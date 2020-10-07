package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEThread extends Thread {
    public XLEThread(Runnable runnable, String name) {
        super(runnable, name);
        setUncaughtExceptionHandler(XLEUnhandledExceptionHandler.Instance);
    }
}
