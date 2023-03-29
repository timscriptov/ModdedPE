package com.xbox.httpclient;

import java.io.IOException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class HttpClientRequest {
    private static final byte[] NO_BODY = new byte[0];
    private static final OkHttpClient OK_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private Request.Builder requestBuilder = new Request.Builder();

    public native void OnRequestCompleted(long j, HttpClientResponse httpClientResponse);

    public native void OnRequestFailed(long j, String str, boolean z);

    public void setHttpUrl(String str) {
        this.requestBuilder = this.requestBuilder.url(str);
    }

    public void setHttpMethodAndBody(String str, long j, String str2, long j2) {
        MediaType mediaType = null;
        RequestBody requestBody = null;
        if (j2 == 0) {
            if ("POST".equals(str) || "PUT".equals(str)) {
                if (str2 != null) {
                    mediaType = MediaType.parse(str2);
                }
                requestBody = RequestBody.create(NO_BODY, mediaType);
            }
        } else {
            requestBody = new HttpClientRequestBody(j, str2, j2);
        }
        this.requestBuilder.method(str, requestBody);
    }

    public void setHttpHeader(String str, String str2) {
        this.requestBuilder = this.requestBuilder.addHeader(str, str2);
    }

    public void doRequestAsync(final long j) {
        OK_CLIENT.newCall(this.requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException iOException) {
                HttpClientRequest.this.OnRequestFailed(j, iOException.getClass().getCanonicalName(), iOException instanceof UnknownHostException);
            }

            @Override
            public void onResponse(Call call, Response response) {
                HttpClientRequest httpClientRequest = HttpClientRequest.this;
                httpClientRequest.OnRequestCompleted(j, new HttpClientResponse(j, response));
            }
        });
    }
}