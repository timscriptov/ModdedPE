package com.microsoft.xbox.service.network.managers;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;

import java.net.URI;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpDeleteWithRequestBody extends HttpPost {
    public HttpDeleteWithRequestBody(URI url) {
        super(url);
    }

    public String getMethod() {
        return HttpDelete.METHOD_NAME;
    }
}
