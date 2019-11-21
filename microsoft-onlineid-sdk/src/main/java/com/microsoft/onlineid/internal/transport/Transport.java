package com.microsoft.onlineid.internal.transport;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;

public class Transport {
    private static final String GetMethod = "GET";
    private static final String PostMethod = "POST";
    public static final String SdkIdentifier = "MsaAndroidSdk";
    private int _connectionTimeoutMilliseconds = 60000;
    private String _customUserAgentString;
    private HttpsURLConnectionWrapper _httpsURLConnectionWrapper;
    private int _readTimeoutMilliseconds = 30000;

    public void openPostRequest(URL targetUrl) throws NetworkException {
        if (targetUrl == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        try {
            initializeConnection(targetUrl);
            this._httpsURLConnectionWrapper.setRequestMethod(PostMethod);
            this._httpsURLConnectionWrapper.setDoOutput(true);
        } catch (IOException e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    public void openGetRequest(URL targetUrl) throws NetworkException {
        if (targetUrl == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        try {
            initializeConnection(targetUrl);
            this._httpsURLConnectionWrapper.setRequestMethod(GetMethod);
        } catch (ProtocolException e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    private void initializeConnection(URL targetUrl) throws NetworkException {
        if (this._httpsURLConnectionWrapper == null) {
            this._httpsURLConnectionWrapper = new HttpsURLConnectionWrapper(targetUrl);
        } else {
            this._httpsURLConnectionWrapper.setUrl(targetUrl);
        }
        try {
            this._httpsURLConnectionWrapper.openConnection();
            this._httpsURLConnectionWrapper.setConnectTimeout(this._connectionTimeoutMilliseconds);
            this._httpsURLConnectionWrapper.setReadTimeout(this._readTimeoutMilliseconds);
            this._httpsURLConnectionWrapper.setDoInput(true);
            this._httpsURLConnectionWrapper.setUseCaches(false);
            setUserAgent();
        } catch (IOException e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    private void setUserAgent() {
        Assertion.check(!TextUtils.isEmpty(this._customUserAgentString));
        this._httpsURLConnectionWrapper.addRequestProperty("User-Agent", mergeUserAgentStrings(System.getProperty("http.agent"), this._customUserAgentString));
    }

    public InputStream getResponseStream() throws NetworkException {
        try {
            return this._httpsURLConnectionWrapper.getResponseCode() == 200 ? this._httpsURLConnectionWrapper.getInputStream() : this._httpsURLConnectionWrapper.getErrorStream();
        } catch (Throwable io) {
            throw new NetworkException(io);
        }
    }

    public int getResponseCode() throws NetworkException {
        try {
            return this._httpsURLConnectionWrapper.getResponseCode();
        } catch (Throwable io) {
            throw new NetworkException(io);
        }
    }

    public long getResponseDate() {
        return this._httpsURLConnectionWrapper.getDate();
    }

    public OutputStream getRequestStream() throws NetworkException {
        if (this._httpsURLConnectionWrapper.getRequestMethod().equals(GetMethod)) {
            throw new NetworkException("A GET request cannot have an OutputStream");
        }
        try {
            return this._httpsURLConnectionWrapper.getOutputStream();
        } catch (Throwable io) {
            throw new NetworkException(io);
        }
    }

    public void closeConnection() {
        this._httpsURLConnectionWrapper.disconnect();
    }

    public void addRequestProperty(String field, String value) {
        this._httpsURLConnectionWrapper.addRequestProperty(field, value);
    }

    public int getConnectionTimeoutMilliseconds() {
        return this._connectionTimeoutMilliseconds;
    }

    public void setConnectionTimeoutMilliseconds(int newConnectionTimeout) {
        if (newConnectionTimeout < 0) {
            throw new IllegalArgumentException("Connection timeout value is out of range");
        }
        this._connectionTimeoutMilliseconds = newConnectionTimeout;
    }

    public int getReadTimeoutMilliseconds() {
        return this._readTimeoutMilliseconds;
    }

    public void setReadTimeoutMilliseconds(int newReadTimeout) {
        if (newReadTimeout < 0) {
            throw new IllegalArgumentException("Read timeout value is out of range");
        }
        this._readTimeoutMilliseconds = newReadTimeout;
    }

    public void setUseCaches(boolean newValue) {
        this._httpsURLConnectionWrapper.setUseCaches(newValue);
    }

    void appendCustomUserAgentString(String userAgentString) {
        this._customUserAgentString = mergeUserAgentStrings(this._customUserAgentString, userAgentString);
    }

    void setHttpsURLConnectionWrapper(HttpsURLConnectionWrapper httpsURLConnectionWrapper) {
        this._httpsURLConnectionWrapper = httpsURLConnectionWrapper;
    }

    public static String buildUserAgentString(Context appContext) {
        return mergeUserAgentStrings(appContext.getPackageName() + "/" + PackageInfoHelper.getCurrentAppVersionName(appContext), "MsaAndroidSdk/" + Resources.getSdkVersion(appContext));
    }

    public static String mergeUserAgentStrings(String userAgentString1, String userAgentString2) {
        if (TextUtils.isEmpty(userAgentString1)) {
            return userAgentString2;
        }
        if (TextUtils.isEmpty(userAgentString2)) {
            return userAgentString1;
        }
        return userAgentString1 + "; " + userAgentString2;
    }
}
