package com.microsoft.onlineid.internal.transport;

import com.microsoft.onlineid.internal.Assertion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpsURLConnectionWrapper {
    private HttpsURLConnection _connection;
    private URL _url;

    public HttpsURLConnectionWrapper(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        this._url = url;
    }

    public void openConnection() throws IOException {
        this._connection = (HttpsURLConnection) this._url.openConnection();
    }

    public void setRequestMethod(String method) throws ProtocolException {
        verifyConnectionIsOpened();
        this._connection.setRequestMethod(method);
    }

    public void setConnectTimeout(int timeout) {
        verifyConnectionIsOpened();
        this._connection.setConnectTimeout(timeout);
    }

    public void setReadTimeout(int timeout) {
        verifyConnectionIsOpened();
        this._connection.setReadTimeout(timeout);
    }

    public void setDoInput(boolean doInput) {
        verifyConnectionIsOpened();
        this._connection.setDoInput(doInput);
    }

    public String getRequestMethod() {
        verifyConnectionIsOpened();
        return this._connection.getRequestMethod();
    }

    public void setDoOutput(boolean doOutput) {
        verifyConnectionIsOpened();
        this._connection.setDoOutput(doOutput);
    }

    public OutputStream getOutputStream() throws IOException {
        verifyConnectionIsOpened();
        return this._connection.getOutputStream();
    }

    public int getResponseCode() throws IOException {
        verifyConnectionIsOpened();
        return this._connection.getResponseCode();
    }

    public InputStream getInputStream() throws IOException {
        verifyConnectionIsOpened();
        return this._connection.getInputStream();
    }

    public InputStream getErrorStream() {
        verifyConnectionIsOpened();
        return this._connection.getErrorStream();
    }

    public long getDate() {
        verifyConnectionIsOpened();
        return this._connection.getDate();
    }

    public void disconnect() {
        verifyConnectionIsOpened();
        this._connection.disconnect();
    }

    public int getContentLength() {
        verifyConnectionIsOpened();
        return this._connection.getContentLength();
    }

    public void addRequestProperty(String field, String value) {
        verifyConnectionIsOpened();
        this._connection.addRequestProperty(field, value);
    }

    public void setUseCaches(boolean newValue) {
        verifyConnectionIsOpened();
        this._connection.setUseCaches(newValue);
    }

    public void setUrl(URL url) {
        try {
            disconnect();
        } catch (IllegalStateException e) {
            Assertion.check(false);
        }
        this._url = url;
    }

    private void verifyConnectionIsOpened() {
        if (this._connection == null) {
            throw new IllegalStateException("openConnection should have been called first");
        }
    }
}
