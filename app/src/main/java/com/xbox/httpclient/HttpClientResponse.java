package com.xbox.httpclient;

import java.io.IOException;

import okhttp3.Response;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

class HttpClientResponse {
    private Response response;

    public HttpClientResponse(Response sourceResponse) {
        response = sourceResponse;
    }

    public int getNumHeaders() {
        return response.headers().size();
    }

    public String getHeaderNameAtIndex(int index) {
        if (index < 0 || index >= response.headers().size()) {
            return null;
        }
        return response.headers().name(index);
    }

    public String getHeaderValueAtIndex(int index) {
        if (index < 0 || index >= response.headers().size()) {
            return null;
        }
        return response.headers().value(index);
    }

    public byte[] getResponseBodyBytes() {
        try {
            return response.body().bytes();
        } catch (IOException e) {
            return null;
        }
    }

    public int getResponseCode() {
        return response.code();
    }
}
