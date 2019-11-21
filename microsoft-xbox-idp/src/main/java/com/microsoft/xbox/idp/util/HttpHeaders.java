package com.microsoft.xbox.idp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpHeaders {
    private final List<Header> headers = new ArrayList();

    public static class Header {
        public final String key;
        private final String value;

        public Header(String key2, String value2) {
            this.key = key2;
            this.value = value2;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("{ ").append("\"").append(this.key).append("\": ").append("\"").append(this.value).append("\"").append(" }");
            return b.toString();
        }
    }

    public void add(String key, String value) {
        this.headers.add(new Header(key, value));
    }

    public Collection<Header> getAllHeaders() {
        return this.headers;
    }

    public Header getFirstHeader(String key) {
        if (key != null) {
            for (Header h : this.headers) {
                if (key.equals(h.key)) {
                    return h;
                }
            }
        }
        return null;
    }

    public Header getLastHeader(String key) {
        if (key != null) {
            for (int i = this.headers.size() - 1; i >= 0; i--) {
                Header h = (Header) this.headers.get(i);
                if (key.equals(h.key)) {
                    return h;
                }
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[ ");
        for (Header h : this.headers) {
            b.append(h);
        }
        b.append(" ]");
        return b.toString();
    }
}
