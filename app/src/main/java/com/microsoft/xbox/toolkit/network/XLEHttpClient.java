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
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEHttpClient extends AbstractXLEHttpClient {
    DefaultHttpClient client;

    public XLEHttpClient(ClientConnectionManager connectionManager, HttpParams params) {
        client = new DefaultHttpClient(connectionManager, params);
    }

    public HttpResponse execute(HttpUriRequest get) throws ClientProtocolException, IOException {
        return client.execute(get, (HttpContext) new BasicHttpContext());
    }
}
