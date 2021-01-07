package com.microsoft.xbox.toolkit.network;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEHttpClient extends AbstractXLEHttpClient {
    DefaultHttpClient client;

    public XLEHttpClient(ClientConnectionManager clientConnectionManager, HttpParams httpParams) {
        this.client = new DefaultHttpClient(clientConnectionManager, httpParams);
    }

    public HttpResponse execute(HttpUriRequest httpUriRequest) throws ClientProtocolException, IOException {
        return this.client.execute(httpUriRequest, (HttpContext) new BasicHttpContext());
    }
}
