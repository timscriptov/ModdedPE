package com.microsoft.xbox.idp.interop;

public class TokenAndSignature {
    private final long id;

    private static native void delete(long j);

    private static native String getAgeGroup(long j);

    private static native String getGamertag(long j);

    private static native String getPriviliges(long j);

    private static native String getReserved(long j);

    private static native String getSignature(long j);

    private static native String getToken(long j);

    private static native String getUserHash(long j);

    private static native String getWebAccountId(long j);

    private static native String getXuid(long j);

    TokenAndSignature(long id2) {
        this.id = id2;
    }

    public String getToken() {
        return getToken(this.id);
    }

    public String getSignature() {
        return getSignature(this.id);
    }

    public String getXuid() {
        return getXuid(this.id);
    }

    public String getGamertag() {
        return getGamertag(this.id);
    }

    public String getUserHash() {
        return getUserHash(this.id);
    }

    public String getAgeGroup() {
        return getAgeGroup(this.id);
    }

    public String getPriviliges() {
        return getPriviliges(this.id);
    }

    public String getWebAccountId() {
        return getWebAccountId(this.id);
    }

    public String getReserved() {
        return getReserved(this.id);
    }

    public void finalize() throws Throwable {
        delete(this.id);
        super.finalize();
    }
}
