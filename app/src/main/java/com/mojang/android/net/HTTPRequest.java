/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.android.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.SM;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HTTPRequest {
    String mCookieData = "";
    HttpRequestBase mHTTPRequest = null;
    String mRequestBody = "";
    String mRequestContentType = "text/plain";
    HTTPResponse mResponse = new HTTPResponse();
    String mURL = "";

    public void setURL(String url) {
        mURL = url;
    }

    public void setRequestBody(String requestBody) {
        mRequestBody = requestBody;
    }

    public void setCookieData(String cookieData) {
        mCookieData = cookieData;
    }

    public void setContentType(String conentType) {
        mRequestContentType = conentType;
    }

    public HTTPResponse send(String method) {
        createHTTPRequest(method);
        addHeaders();
        if (mResponse.getStatus() == 2) {
            return mResponse;
        }
        try {
            HttpResponse response = HTTPClientManager.getHTTPClient().execute(mHTTPRequest);
            mResponse.setResponseCode(response.getStatusLine().getStatusCode());
            mResponse.setBody(EntityUtils.toString(response.getEntity()));
            mResponse.setStatus(1);
            mResponse.setHeaders(response.getAllHeaders());
            return mResponse;
        } catch (ConnectTimeoutException e) {
            mResponse.setStatus(3);
        } catch (ClientProtocolException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        mHTTPRequest = null;
        return mResponse;
    }

    public synchronized void abort() {
        mResponse.setStatus(2);
        if (mHTTPRequest != null) {
            mHTTPRequest.abort();
        }
    }

    private synchronized void createHTTPRequest(String method) {
        if (method.equals("DELETE")) {
            mHTTPRequest = new HttpDelete(mURL);
        } else if (method.equals(HttpPut.METHOD_NAME)) {
            HttpPut putRequest = new HttpPut(mURL);
            addBodyToRequest(putRequest);
            mHTTPRequest = putRequest;
        } else if (method.equals("GET")) {
            this.mHTTPRequest = new HttpGet(mURL);
        } else if (method.equals("POST")) {
            HttpPost postRequest = new HttpPost(mURL);
            addBodyToRequest(postRequest);
            mHTTPRequest = postRequest;
        } else {
            throw new InvalidParameterException("Unknown request method " + method);
        }
    }

    private void addBodyToRequest(HttpEntityEnclosingRequestBase request) {
        if (mRequestBody != "") {
            try {
                StringEntity se = new StringEntity(mRequestBody);
                se.setContentType(mRequestContentType);
                request.setEntity(se);
                request.addHeader("Content-Type", mRequestContentType);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void addHeaders() {
        mHTTPRequest.addHeader(HTTP.USER_AGENT, "MCPE/Android");
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        mHTTPRequest.setParams(httpParameters);
        if (mCookieData != null && mCookieData.length() > 0) {
            mHTTPRequest.addHeader(SM.COOKIE, mCookieData);
        }
        mHTTPRequest.addHeader("Charset", "utf-8");
    }
}
