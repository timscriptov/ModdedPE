package com.microsoft.xbox.idp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpHeaders {
    private final List<Header> headers = new ArrayList();

    public void add(String key, String value) {
        headers.add(new Header(key, value));
    }

    public Collection<Header> getAllHeaders() {
        return headers;
    }

    public Header getFirstHeader(String key) {
        if (key != null) {
            for (Header h : headers) {
                if (key.equals(h.key)) {
                    return h;
                }
            }
        }
        return null;
    }

    public Header getLastHeader(String key) {
        if (key != null) {
            for (int i = headers.size() - 1; i >= 0; i--) {
                Header h = headers.get(i);
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
        for (Header h : headers) {
            b.append(h);
        }
        b.append(" ]");
        return b.toString();
    }

    public static class Header {
        public final String key;
        private final String value;

        public Header(String key2, String value2) {
            key = key2;
            value = value2;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("{ ").append("\"").append(key).append("\": ").append("\"").append(value).append("\"").append(" }");
            return b.toString();
        }
    }
}
