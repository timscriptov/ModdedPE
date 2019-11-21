package com.microsoft.xbox.idp.telemetry.utc.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;

import java.util.HashMap;

public class UTCAccessibilityInfoModel extends UTCJsonBase {
    private HashMap<String, Object> info = new HashMap<>();

    public void addValue(String key, Object value) {
        if (key != null && !this.info.containsKey(key)) {
            this.info.put(key, value);
        }
    }

    public HashMap<String, Object> getInfo() {
        return this.info;
    }

    public void setInfo(HashMap<String, Object> info2) {
        this.info = info2;
    }

    public String toJson() {
        String result = "";
        try {
            return new GsonBuilder().serializeNulls().create().toJson((Object) getInfo());
        } catch (JsonIOException e) {
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
            return result;
        }
    }

    public String toString() {
        return toJson();
    }
}
