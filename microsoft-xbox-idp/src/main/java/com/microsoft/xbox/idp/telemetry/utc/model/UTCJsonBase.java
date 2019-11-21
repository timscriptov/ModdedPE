package com.microsoft.xbox.idp.telemetry.utc.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;

public abstract class UTCJsonBase {
    public String toJson() {
        String jsonData = "";
        try {
            return new GsonBuilder().serializeNulls().create().toJson((Object) this);
        } catch (JsonIOException e) {
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
            return jsonData;
        }
    }
}
