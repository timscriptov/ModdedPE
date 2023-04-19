package com.xbox.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Okio;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class HttpClientResponse {
    private final long callHandle;
    private final Response response;

    public HttpClientResponse(long callHandle, Response response) {
        this.callHandle = callHandle;
        this.response = response;
    }

    public int getNumHeaders() {
        return response.headers().size();
    }

    public String getHeaderNameAtIndex(int i) {
        if (i < 0 || i >= response.headers().size()) {
            return null;
        }
        return response.headers().name(i);
    }

    public String getHeaderValueAtIndex(int i) {
        if (i < 0 || i >= response.headers().size()) {
            return null;
        }
        return response.headers().value(i);
    }

    public void getResponseBodyBytes() {
        try {
            final ResponseBody body = response.body();
            if (body != null) {
                body.source().readAll(Okio.sink(new NativeOutputStream(callHandle)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable th) {
            response.close();
            throw th;
        }
        response.close();
    }

    public int getResponseCode() {
        return response.code();
    }

    private final class NativeOutputStream extends OutputStream {
        private final long callHandle;

        public NativeOutputStream(long callHandle) {
            this.callHandle = callHandle;
        }

        private native void nativeWrite(long callHandle, byte[] b, int off, int len) throws IOException;

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            Objects.requireNonNull(b);
            if (off < 0 || len < 0 || off + len > b.length) {
                throw new IndexOutOfBoundsException();
            }
            nativeWrite(callHandle, b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) b});
        }
    }
}
