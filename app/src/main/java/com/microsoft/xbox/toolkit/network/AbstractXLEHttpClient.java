package com.microsoft.xbox.toolkit.network;

import com.appsflyer.share.Constants;
import com.microsoft.xbox.toolkit.XLEException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class AbstractXLEHttpClient {
    public abstract HttpResponse execute(HttpUriRequest httpUriRequest) throws ClientProtocolException, IOException;

    public XLEHttpStatusAndStream getHttpStatusAndStreamInternal(HttpUriRequest httpGet, boolean printStreamDebug) throws XLEException {
        XLEHttpStatusAndStream rv = new XLEHttpStatusAndStream();
        try {
            HttpResponse response = execute(httpGet);
            if (!(response == null || response.getStatusLine() == null)) {
                rv.statusLine = response.getStatusLine().toString();
                rv.statusCode = response.getStatusLine().getStatusCode();
            }
            if (!(response == null || response.getLastHeader(Constants.HTTP_REDIRECT_URL_HEADER_FIELD) == null)) {
                rv.redirectUrl = response.getLastHeader(Constants.HTTP_REDIRECT_URL_HEADER_FIELD).getValue();
            }
            if (response != null) {
                rv.headers = response.getAllHeaders();
            }
            HttpEntity entity = response == null ? null : response.getEntity();
            if (entity != null) {
                rv.stream = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
                entity.consumeContent();
                Header contentEncodingHeader = response.getFirstHeader(HTTP.CONTENT_ENCODING);
                if (contentEncodingHeader != null && contentEncodingHeader.getValue().equalsIgnoreCase("gzip")) {
                    rv.stream = new GZIPInputStream(rv.stream);
                }
            }
            return rv;
        } catch (Exception e) {
            httpGet.abort();
            if (!(rv == null || rv.stream == null)) {
                rv.close();
            }
            throw new XLEException(4, e);
        }
    }
}
