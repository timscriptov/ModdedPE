package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

import org.jetbrains.annotations.NotNull;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class XLEAsyncTask<Result> {
    public XLEAsyncTask chainedTask = null;
    protected boolean cancelled = false;
    protected boolean isBusy = false;
    private Runnable doBackgroundAndPostExecuteRunnable = null;
    private XLEThreadPool threadPool = null;

    public XLEAsyncTask(XLEThreadPool threadPool2) {
        threadPool = threadPool2;
        doBackgroundAndPostExecuteRunnable = () -> {
            final Result r;
            if (!cancelled) {
                r = doInBackground();
            } else {
                r = null;
            }
            ThreadManager.UIThreadPost(() -> {
                isBusy = false;
                if (!cancelled) {
                    onPostExecute(r);
                    if (chainedTask != null) {
                        chainedTask.execute();
                    }
                }
            });
        };
    }

    public static void executeAll(@NotNull XLEAsyncTask... tasks) {
        if (tasks.length > 0) {
            for (int i = 0; i < tasks.length - 1; i++) {
                tasks[i].chainedTask = tasks[i + 1];
            }
            tasks[0].execute();
        }
    }

    public abstract Result doInBackground();

    public abstract void onPostExecute(Result result);

    public abstract void onPreExecute();

    public void cancel() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        cancelled = true;
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        cancelled = false;
        isBusy = true;
        onPreExecute();
        executeBackground();
    }

    public boolean getIsBusy() {
        return isBusy && !cancelled;
    }

    public void executeBackground() {
        cancelled = false;
        threadPool.run(doBackgroundAndPostExecuteRunnable);
    }
}
