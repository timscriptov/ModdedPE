package com.microsoft.xbox.telemetry.utc;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ServiceError extends CommonData {
    private static final int SERVICEERRORVERSION = 1;
    public String errorCode;
    public String errorName;
    public String errorText;
    public String pageName;

    public ServiceError() {
        super(1);
    }
}
