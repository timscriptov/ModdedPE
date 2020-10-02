package com.xbox.httpclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpClientRequest {
    private static final byte[] NO_BODY = new byte[0];
    private static OkHttpClient OK_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private Request okHttpRequest;
    private Request.Builder requestBuilder = new Request.Builder();

    public static boolean isNetworkAvailable(@NotNull Context context) {
        @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static HttpClientRequest createClientRequest() {
        return new HttpClientRequest();
    }

    public native void OnRequestCompleted(long j, HttpClientResponse httpClientResponse);

    public native void OnRequestFailed(long j, String str);

    public void setHttpUrl(String url) {
        requestBuilder = requestBuilder.url(url);
    }

    public void setHttpMethodAndBody(String method, String contentType, byte[] body) {
        if (body != null && body.length != 0) {
            requestBuilder = requestBuilder.method(method, RequestBody.create(MediaType.parse(contentType), body));
        } else if ("POST".equals(method) || "PUT".equals(method)) {
            requestBuilder = requestBuilder.method(method, RequestBody.create((MediaType) null, NO_BODY));
        } else {
            requestBuilder = requestBuilder.method(method, (RequestBody) null);
        }
    }

    public void setHttpHeader(String name, String value) {
        requestBuilder = requestBuilder.addHeader(name, value);
    }

    public void doRequestAsync(final long sourceCall) {
        OK_CLIENT.newCall(requestBuilder.build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                OnRequestFailed(sourceCall, e.getClass().getCanonicalName());
            }

            public void onResponse(Call call, Response response) throws IOException {
                OnRequestCompleted(sourceCall, new HttpClientResponse(response));
            }
        });
    }
}
