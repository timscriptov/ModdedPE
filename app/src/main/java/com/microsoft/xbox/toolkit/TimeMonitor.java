package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TimeMonitor {
    private final long NSTOMSEC = 1000000;
    private long endTicks = 0;
    private long startTicks = 0;

    public boolean getIsStarted() {
        return startTicks != 0;
    }

    public boolean getIsEnded() {
        return endTicks != 0;
    }

    public void reset() {
        startTicks = 0;
        endTicks = 0;
    }

    public void start() {
        startTicks = System.nanoTime();
        endTicks = 0;
    }

    public void stop() {
        if (startTicks != 0 && endTicks == 0) {
            endTicks = System.nanoTime();
        }
    }

    public long currentTime() {
        return (System.nanoTime() - startTicks) / 1000000;
    }

    public void saveCurrentTime() {
        if (getIsStarted()) {
            endTicks = System.nanoTime();
        }
    }

    public long getElapsedMs() {
        long end;
        if (!getIsStarted()) {
            return 0;
        }
        if (endTicks != 0) {
            end = endTicks;
        } else {
            end = System.nanoTime();
        }
        return (end - startTicks) / 1000000;
    }
}
