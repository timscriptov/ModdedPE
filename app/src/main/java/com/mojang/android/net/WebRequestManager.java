/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.android.net;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class WebRequestManager {

    private HttpClient _httpClient = null;
    private Object _requestlock = new Object();
    private ArrayList<WebRequestData> _webRequests = new ArrayList<WebRequestData>();
    private IRequestCompleteCallback onRequestCompleteCallback;

    public WebRequestManager(IRequestCompleteCallback onRequestCompleteCallback) {
        this.onRequestCompleteCallback = onRequestCompleteCallback;
    }

    private WebRequestData _findWebRequest(int requestId) {
        synchronized (this._requestlock) {
            for (WebRequestData r : _webRequests) {
                if (r.requestId != requestId) continue;

                return r;
            }
        }

        return new WebRequestData(requestId).markError(Status.REQUEST_NOT_FOUND);
    }

    private void _init() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        SSLSocketFactory sslSocketFactory = null;

        try {
            sslSocketFactory = NoCertSSLSocketFactory.createDefault();
            registry.register(new Scheme("https", sslSocketFactory, 443));
        } catch (Exception e) {
            Log.e("ModdedPE", "Couldn\'t create SSLSocketFactory");
        }

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        _httpClient = new DefaultHttpClient(manager, params);
    }

    public int abortWebRequest(int requestId) {
        WebRequestData r = _findWebRequest(requestId);
        if (r.status != Status.REQUEST_NOT_FOUND) {
            synchronized (_requestlock) {
                r.abort();
                _webRequests.remove(r);
            }

            System.out.println("Requests left " + _webRequests.size());
        }

        return r.getStatusCode();
    }

    public String getWebRequestContent(int requestId) {
        return _findWebRequest(requestId).content;
    }

    public int getWebRequestStatus(int requestId) {
        return _findWebRequest(requestId).getStatusCode();
    }

    public void webRequest(int requestId, long voidptr, String uri, String method, String cookieData, String httpBody) {
        if (_httpClient == null) {
            _init();
        }
        HttpRequestBase httpRequest = null;

        if (method.equals("DELETE")) {
            httpRequest = new HttpDelete(uri);
        } else if (method.equals("PUT")) {
            HttpPut putRequest = new HttpPut(uri);

            if (!httpBody.equals("")) {
                try {
                    StringEntity se = new StringEntity(httpBody);
                    se.setContentType("application/json");
                    putRequest.setEntity(se);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                httpRequest = putRequest;
            }
        } else if (method.equals("GET")) {
            httpRequest = new HttpGet(uri);
        } else if (method.equals("POST")) {
            HttpPost postRequest = new HttpPost(uri);

            if (!httpBody.equals("")) {
                try {
                    StringEntity se = new StringEntity(httpBody);
                    se.setContentType("application/json");
                    postRequest.setEntity(se);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                httpRequest = postRequest;
            }
        } else {
            throw new InvalidParameterException("Unknown request method " + method);
        }

        httpRequest.addHeader("User-Agent", "MCPE/Curl");

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        httpRequest.setParams(httpParameters);

        if (cookieData != null && cookieData.length() > 0) {
            System.out.println("Setting cookie: (" + cookieData.length() + ") " + cookieData);
            httpRequest.addHeader("Cookie", cookieData);
        }

        final WebRequestData request = new WebRequestData(requestId, httpRequest, voidptr);

        synchronized (_requestlock) {
            for (WebRequestData w : _webRequests) {
                if (w.requestId == request.requestId) {
                    return;
                }
            }

            _webRequests.add(request);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                do {
                    try {
                        request.execute(_httpClient);

                        if (request.getStatusCode() == 503) {
                            request.waitForSeconds(request.retryTimeout);
                        } else {
                            synchronized (_requestlock) {
                                if (request.aborted) {
                                    onRequestCompleteCallback.onRequestComplete(request.requestId, request.voidptr, request.getStatusCode(), request.content);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (!request.aborted);
            }
        }).start();
    }

    public enum Status {
        REQUEST_NOT_FOUND,
        PENDING,
        FINISHED,
        FAIL_URLFORMAT,
        FAIL_PARSE,
        FAIL_TIMEOUT,
        FAIL_GENERAL,
        FAIL_CANCELLED;

        public int getCode() {
            return -ordinal();
        }

        public boolean isError() {
            if (this != PENDING && this != FINISHED) {
                return true;
            }

            return false;
        }
    }

    public interface IRequestCompleteCallback {
        public abstract void onRequestComplete(int requestId, long userData, int httpStatusOrNegativeError, String content);
    }

    class WebRequestData {
        public final int requestId;
        public volatile boolean aborted = false;
        public String content = "";
        public int retryTimeout = 5;
        public Status status = Status.PENDING;
        public long voidptr;

        private HttpRequestBase request;
        private HttpResponse response;

        public WebRequestData(int requestId) {
            this.requestId = requestId;
        }

        public WebRequestData(int requsetId, HttpRequestBase request, long voidptr) {
            this.requestId = requsetId;
            this.request = request;
            this.voidptr = voidptr;
        }

        public void abort() {
            aborted = true;

            if (request != null || !request.isAborted()) {
                request.abort();
            }
        }

        public void execute(HttpClient httpClient) {
            if (Status.PENDING != status) return;

            try {
                this.response = httpClient.execute(this.request);
            } catch (ClientProtocolException e1) {
                markError(Status.FAIL_URLFORMAT);
                e1.printStackTrace();
            } catch (IOException e1) {
                markError(Status.FAIL_GENERAL);
                e1.printStackTrace();
            } catch (Exception e1) {
                markError(Status.FAIL_GENERAL);
                e1.printStackTrace();
            }

            try {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 204) {
                    content = "";
                } else if (responseCode == 503) {
                    Header h = response.getLastHeader("Retry-After");
                    if (h != null) {
                        retryTimeout = Integer.valueOf(h.getValue()).intValue();
                    }
                } else {
                    HttpEntity entity = response.getEntity();
                    content = entity.toString();
                }

                status = Status.FINISHED;
            } catch (ParseException e) {
                e.printStackTrace();
                status = Status.FAIL_PARSE;
            }
        }

        public int getStatusCode() {
            if (Status.PENDING == status) {
                return response.getStatusLine().getStatusCode();
            }
            return status.getCode();
        }

        public WebRequestData markError(Status errorCode) {
            if (!errorCode.isError()) {
                throw new AssertionError();
            }
            status = errorCode;

            return this;
        }

        public void waitForSeconds(int seconds) {
            for (int a = 0; a < seconds * 5; a++) {
                if (aborted) return;

                try {
                    Thread.sleep((long) a * 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
