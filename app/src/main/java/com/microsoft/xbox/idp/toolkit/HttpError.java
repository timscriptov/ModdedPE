package com.microsoft.xbox.idp.toolkit;

import java.io.InputStream;
import java.util.Scanner;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpError {
    private static final String INPUT_START_TOKEN = "\\A";
    private final int errorCode;
    private final String errorMessage;
    private final int httpStatus;

    public HttpError(int errorCode2, int httpStatus2, String errorMessage2) {
        errorCode = errorCode2;
        httpStatus = httpStatus2;
        errorMessage = errorMessage2;
    }

    public HttpError(int errorCode2, int httpStatus2, InputStream stream) {
        errorCode = errorCode2;
        httpStatus = httpStatus2;
        Scanner errorScanner = new Scanner(stream).useDelimiter(INPUT_START_TOKEN);
        errorMessage = errorScanner.hasNext() ? errorScanner.next() : "";
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("errorCode: ").append(errorCode).append(", httpStatus: ").append(httpStatus).append(", errorMessage: ").append(errorMessage);
        return sb.toString();
    }
}
