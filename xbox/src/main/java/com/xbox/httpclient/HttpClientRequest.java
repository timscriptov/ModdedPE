package com.xbox.httpclient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

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
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class HttpClientRequest {
    private static final byte[] NO_BODY = new byte[0];
    private static final OkHttpClient OK_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private Request.Builder requestBuilder = new Request.Builder();

    public native void OnRequestCompleted(long j, HttpClientResponse httpClientResponse);

    public native void OnRequestFailed(long j, String str, boolean z);

    public void setHttpUrl(String str) {
        requestBuilder = requestBuilder.url(str);
    }

    public void setHttpMethodAndBody(String str, long j, String str2, long j2) {
        RequestBody httpClientRequestBody = null;
        if (j2 == 0) {
            if (HttpPost.METHOD_NAME.equals(str) || HttpPut.METHOD_NAME.equals(str)) {
                httpClientRequestBody = RequestBody.create(NO_BODY, str2 != null ? MediaType.parse(str2) : null);
            }
        } else {
            httpClientRequestBody = new HttpClientRequestBody(j, str2, j2);
        }
        requestBuilder.method(str, httpClientRequestBody);
    }

    public void setHttpHeader(String str, String str2) {
        requestBuilder = requestBuilder.addHeader(str, str2);
    }

    public void doRequestAsync(final long j) {
        OK_CLIENT.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException iOException) {
                OnRequestFailed(j, iOException.getClass().getCanonicalName(), iOException instanceof UnknownHostException);
            }

            @Override
            public void onResponse(Call call, Response response) {
                HttpClientRequest httpClientRequest = HttpClientRequest.this;
                httpClientRequest.OnRequestCompleted(j, new HttpClientResponse(j, response));
            }
        });
    }
}