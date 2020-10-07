package com.microsoft.xbox.telemetry.utc;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class PageAction extends CommonData {
    private static final int PAGEACTIONVERSION = 1;
    public String actionName;
    public String pageName;

    public PageAction() {
        super(1);
    }
}
