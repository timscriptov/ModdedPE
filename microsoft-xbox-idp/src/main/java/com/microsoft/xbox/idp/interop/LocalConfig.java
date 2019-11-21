package com.microsoft.xbox.idp.interop;

public final class LocalConfig {
    private final long id = create();

    private static native long create();

    private static native void delete(long j);

    private static native String getCid(long j);

    public void finalize() throws Throwable {
        delete(this.id);
        super.finalize();
    }

    public String getCid() {
        return getCid(this.id);
    }
}
