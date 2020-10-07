package com.microsoft.xbox.telemetry.utc;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ClientError extends CommonData {
    private static final int CLIENTERRORVERSION = 1;
    public String callStack;
    public String errorCode;
    public String errorName;
    public String errorText;
    public String pageName;

    public ClientError() {
        super(1);
    }
}
