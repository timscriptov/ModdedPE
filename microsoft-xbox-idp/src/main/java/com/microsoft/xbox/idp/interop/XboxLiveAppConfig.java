package com.microsoft.xbox.idp.interop;

public class XboxLiveAppConfig {
    private final long id = create();

    private static native long create();

    private static native void delete(long j);

    private static native String getEnvironment(long j);

    private static native int getOverrideTitleId(long j);

    private static native String getProxy(long j);

    private static native String getSandbox(long j);

    private static native String getScid(long j);

    private static native int getTitleId(long j);

    private static native void setEnvironment(long j, String str);

    private static native void setSandbox(long j, String str);

    public int getTitleId() {
        return getTitleId(this.id);
    }

    public int getOverrideTitleId() {
        return getOverrideTitleId(this.id);
    }

    public String getScid() {
        return getScid(this.id);
    }

    public String getEnvironment() {
        return getEnvironment(this.id);
    }

    public void setEnvironment(String value) {
        setEnvironment(this.id, value);
    }

    public String getSandbox() {
        return getSandbox(this.id);
    }

    public void setSandbox(String value) {
        setSandbox(this.id, value);
    }

    public String getProxy() {
        return getProxy(this.id);
    }

    public void finalize() throws Throwable {
        super.finalize();
        delete(this.id);
    }
}
