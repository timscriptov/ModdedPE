package com.xbox.httpclient;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public final class HttpClientRequestBody extends RequestBody {
    private final long callHandle;
    private final long contentLength;
    private final MediaType contentType;

    public HttpClientRequestBody(long callHandle, String contentType, long contentLength) {
        this.callHandle = callHandle;
        this.contentType = contentType != null ? MediaType.parse(contentType) : null;
        this.contentLength = contentLength;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        bufferedSink.writeAll(Okio.source(new NativeInputStream(callHandle)));
    }

    private final class NativeInputStream extends InputStream {
        private final long callHandle;
        private long offset = 0;

        public NativeInputStream(long callHandle) {
            this.callHandle = callHandle;
        }

        private native int nativeRead(long callHandle, long offset, byte[] b, long off, long len) throws IOException;

        @Override
        public int read() throws IOException {
            byte[] bArr = new byte[1];
            read(bArr);
            return bArr[0];
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            Objects.requireNonNull(b);
            if (off < 0 || len < 0 || off + len > b.length) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            int nativeRead = nativeRead(callHandle, offset, b, off, len);
            if (nativeRead == -1) {
                return -1;
            }
            offset += nativeRead;
            return nativeRead;
        }

        @Override
        public long skip(long offset) throws IOException {
            this.offset += offset;
            return offset;
        }
    }
}