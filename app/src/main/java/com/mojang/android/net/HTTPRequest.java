package com.mojang.android.net;

import android.text.TextUtils;

import com.mcal.mcpelauncher.BuildConfig;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HTTPRequest {

    public static boolean debugNet = BuildConfig.DEBUG;

    public String url, requestBody, cookieData, contentType;

    public void setURL(String url) {
        if (debugNet) System.out.println("URL: " + url);
        this.url = url;
    }

    public void setRequestBody(String body) {
        if (debugNet) System.out.println("Body: " + body);
        this.requestBody = body;
    }

    public void setCookieData(String cookie) {
        if (debugNet) System.out.println("Cookie: " + cookie);
        this.cookieData = cookie;
    }

    public void setContentType(String contentType) {
        if (debugNet) System.out.println("Content type: " + contentType);
        this.contentType = contentType;
    }

    public HTTPResponse send(String mode) {
        if (debugNet) System.out.println("Send: " + mode);
        InputStream is = null;
        try {
            int status = 0;
            URL urlObj = new URL(this.url);

            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestProperty("User-Agent", "BlockLauncher");
            conn.setRequestProperty("Cookie", cookieData);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestMethod(mode);

            conn.connect();
            OutputStream os = null;
            os = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write(this.requestBody);
            writer.close();

            try {
                status = conn.getResponseCode();
                is = conn.getInputStream();
            } catch (Exception e) {
                is = conn.getErrorStream();
            }

            if (is == null) throw new Exception("Null input stream");

            ByteArrayOutputStream realos = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int count;
            while ((count = is.read(buffer)) != -1) {
                realos.write(buffer, 0, count);
            }
            String returnString = new String(realos.toByteArray(), "UTF-8");
            return new HTTPResponse(HTTPResponse.STATUS_SUCCESS, status, returnString, toApacheHeaders(conn));
        } catch (Exception e) {
            e.printStackTrace();
            return new HTTPResponse(HTTPResponse.STATUS_FAIL, 0, null, new Header[0]);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void abort() {
        if (debugNet) System.out.println("Abort");
    }

    @NotNull
    private static Header[] toApacheHeaders(@NotNull HttpURLConnection conn) {
        Map<String, List<String>> headers = conn.getHeaderFields();
        int headerSize = headers.containsKey(null) ? headers.size() - 1 : headers.size();
        Header[] headersOut = new Header[headerSize];
        int headerIndex = 0;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() == null) continue;
            headersOut[headerIndex++] = new BasicHeader(entry.getKey(), TextUtils.join(",", entry.getValue()));
        }
        return headersOut;
    }
}
 
