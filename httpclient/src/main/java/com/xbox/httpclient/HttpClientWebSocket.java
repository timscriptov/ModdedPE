package com.xbox.httpclient;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

/**
 * 29.03.2023
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public final class HttpClientWebSocket extends WebSocketListener {
    private static final OkHttpClient OK_CLIENT = new OkHttpClient();
    private final Headers.Builder headers = new Headers.Builder();
    private final long owner;
    private WebSocket socket;

    HttpClientWebSocket(long owner) {
        this.owner = owner;
    }

    public native void onBinaryMessage(ByteBuffer byteBuffer);

    public native void onClose(int i);

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
    }

    public native void onFailure();

    public native void onMessage(String str);

    public native void onOpen();

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void connect(String url, String header) {
        addHeader("Sec-WebSocket-Protocol", header);
        socket = OK_CLIENT.newWebSocket(new Request.Builder().url(url).headers(headers.build()).build(), this);
    }

    public boolean sendMessage(String str) {
        return socket.send(str);
    }

    public boolean sendBinaryMessage(ByteBuffer byteBuffer) {
        return socket.send(ByteString.of(byteBuffer));
    }

    public void disconnect(int i) {
        socket.close(i, null);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        onOpen();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        onFailure();
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        onClose(code);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        onMessage(text);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NonNull ByteString bytes) {
        onBinaryMessage(bytes.asByteBuffer());
    }

    protected void finalize() {
        socket.cancel();
    }
}
