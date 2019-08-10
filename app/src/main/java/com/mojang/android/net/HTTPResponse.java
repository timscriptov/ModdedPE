/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.android.net;

import org.apache.http.Header;

public class HTTPResponse {
    public static final int ABORTED = 2;
    public static final int DONE = 1;
    public static final int IN_PROGRESS = 0;
    public static final int TIME_OUT = 3;
    String body = "";
    Header[] headers;
    int responseCode = -100;
    int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int newStatus) {
        status = newStatus;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String newBody) {
        body = newBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int newResonseCode) {
        responseCode = newResonseCode;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] newHeaders) {
        headers = newHeaders;
    }
}
