package com.microsoft.xbox.toolkit.network;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.spongycastle.asn1.cmp.PKIFailureInfo;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpClientFactory {
    private static final int CONNECTION_PER_ROUTE = 16;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 40;
    private static final int MAX_TOTAL_CONNECTIONS = 32;
    public static HttpClientFactory networkOperationsFactory = new HttpClientFactory();
    public static HttpClientFactory noRedirectNetworkOperationsFactory = new HttpClientFactory(false);
    public static HttpClientFactory textureFactory = new HttpClientFactory(true);
    private AbstractXLEHttpClient client;
    private AbstractXLEHttpClient clientWithTimeoutOverride;
    private ClientConnectionManager connectionManager;
    private Object httpSyncObject;
    private HttpParams params;

    public HttpClientFactory() {
        this(false);
    }

    public HttpClientFactory(boolean allowRedirects) {
        connectionManager = null;
        httpSyncObject = new Object();
        client = null;
        clientWithTimeoutOverride = null;
        params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, false);
        HttpClientParams.setRedirecting(params, allowRedirects);
        if (XboxLiveEnvironment.Instance().getProxyEnabled()) {
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("itgproxy.redmond.corp.microsoft.com", 80));
        }
        HttpConnectionParams.setConnectionTimeout(params, 40000);
        HttpConnectionParams.setSoTimeout(params, 40000);
        HttpConnectionParams.setSocketBufferSize(params, PKIFailureInfo.certRevoked);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(16));
        ConnManagerParams.setMaxTotalConnections(params, 32);
        connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
    }

    public AbstractXLEHttpClient getHttpClient(int timeoutOverride) {
        AbstractXLEHttpClient abstractXLEHttpClient;
        synchronized (httpSyncObject) {
            if (timeoutOverride <= 0) {
                if (client == null) {
                    client = new XLEHttpClient(connectionManager, params);
                }
                abstractXLEHttpClient = client;
            } else if (clientWithTimeoutOverride == null) {
                HttpParams localParams = params.copy();
                HttpConnectionParams.setConnectionTimeout(localParams, timeoutOverride * 1000);
                HttpConnectionParams.setSoTimeout(localParams, timeoutOverride * 1000);
                abstractXLEHttpClient = new XLEHttpClient(connectionManager, localParams);
            } else {
                abstractXLEHttpClient = clientWithTimeoutOverride;
            }
        }
        return abstractXLEHttpClient;
    }

    public ClientConnectionManager getClientConnectionManager() {
        return connectionManager;
    }

    public HttpParams getHttpParams() {
        return params;
    }

    public void setHttpClient(AbstractXLEHttpClient httpClient) {
    }
}
