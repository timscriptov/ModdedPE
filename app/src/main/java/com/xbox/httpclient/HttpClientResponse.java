package com.xbox.httpclient;

import java.io.IOException;
import okhttp3.Response;

class HttpClientResponse {
    private Response response;

    public HttpClientResponse(Response sourceResponse) {
        this.response = sourceResponse;
    }

    public int getNumHeaders() {
        return this.response.headers().size();
    }

    public String getHeaderNameAtIndex(int index) {
        if (index < 0 || index >= this.response.headers().size()) {
            return null;
        }
        return this.response.headers().name(index);
    }

    public String getHeaderValueAtIndex(int index) {
        if (index < 0 || index >= this.response.headers().size()) {
            return null;
        }
        return this.response.headers().value(index);
    }

    public byte[] getResponseBodyBytes() {
        try {
            return this.response.body().bytes();
        } catch (IOException e) {
            return null;
        }
    }

    public int getResponseCode() {
        return this.response.code();
    }
}
