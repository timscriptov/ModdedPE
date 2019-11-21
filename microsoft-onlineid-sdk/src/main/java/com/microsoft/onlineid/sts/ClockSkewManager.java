package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.util.Date;

public class ClockSkewManager {
    private final TypedStorage _storage;

    public ClockSkewManager(Context applicationContext) {
        this._storage = new TypedStorage(applicationContext);
    }

    protected ClockSkewManager(TypedStorage storage) {
        this._storage = storage;
    }

    public void onTimestampReceived(long serverTime) {
        setSkewMilliseconds(getCurrentClientTime().getTime() - serverTime);
    }

    public Date getCurrentServerTime() {
        return toServerTime(getCurrentClientTime());
    }

    public Date toClientTime(Date serverTime) {
        return new Date(serverTime.getTime() + getSkewMilliseconds());
    }

    public Date toServerTime(Date clientTime) {
        return new Date(clientTime.getTime() - getSkewMilliseconds());
    }

    private void setSkewMilliseconds(long skew) {
        this._storage.writeClockSkew(skew);
    }

    public long getSkewMilliseconds() {
        return this._storage.readClockSkew();
    }

    protected Date getCurrentClientTime() {
        return new Date();
    }
}
