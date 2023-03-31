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

    public HttpClientResponse(long j, Response response) {
        callHandle = j;
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

        public NativeOutputStream(long j) {
            this.callHandle = j;
        }

        private native void nativeWrite(long j, byte[] bArr, int i, int i2) throws IOException;

        @Override
        public void write(byte[] bArr) throws IOException {
            write(bArr, 0, bArr.length);
        }

        @Override
        public void write(byte[] bArr, int i, int i2) throws IOException {
            Objects.requireNonNull(bArr);
            if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
                throw new IndexOutOfBoundsException();
            }
            nativeWrite(callHandle, bArr, i, i2);
        }

        @Override
        public void write(int i) throws IOException {
            write(new byte[]{(byte) i});
        }
    }
}
