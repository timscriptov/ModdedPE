package com.microsoft.xbox.idp.util;

import java.io.InputStream;

public class HttpCall {
    private final long id;

    public interface Callback {
        void processResponse(int i, InputStream inputStream, HttpHeaders httpHeaders) throws Exception;
    }

    private static native long create(String str, String str2, String str3, boolean z);

    private static native void delete(long j);

    public native void getResponseAsync(Callback callback);

    public native void setContentTypeHeaderValue(String str);

    public native void setCustomHeader(String str, String str2);

    public native void setLongHttpCall(boolean z);

    public native void setRequestBody(String str);

    public native void setRequestBody(byte[] bArr);

    public native void setRetryAllowed(boolean z);

    public native void setXboxContractVersionHeaderValue(String str);

    public HttpCall(String method, String endpoint, String pathAndQuery) {
        this.id = create(method, endpoint, pathAndQuery, true);
    }

    public HttpCall(String method, String endpoint, String pathAndQuery, boolean addDefaultHeaders) {
        this.id = create(method, endpoint, pathAndQuery, addDefaultHeaders);
    }

    public void finalize() throws Throwable {
        delete(this.id);
        super.finalize();
    }
}
