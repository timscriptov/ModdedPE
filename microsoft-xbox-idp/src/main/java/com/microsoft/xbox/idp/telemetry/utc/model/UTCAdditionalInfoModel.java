package com.microsoft.xbox.idp.telemetry.utc.model;

import com.google.gson.GsonBuilder;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;

import java.util.HashMap;

public class UTCAdditionalInfoModel extends UTCJsonBase {
    private HashMap<String, Object> additionalInfo = new HashMap<>();

    public void addValue(String key, Object value) {
        if (key != null && !this.additionalInfo.containsKey(key)) {
            this.additionalInfo.put(key, value);
        }
    }

    public HashMap<String, Object> getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setAdditionalInfo(HashMap<String, Object> additionalInfo2) {
        this.additionalInfo = additionalInfo2;
    }

    public String toJson() {
        String result = "";
        try {
            return new GsonBuilder().serializeNulls().create().toJson((Object) this.additionalInfo);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCAdditionalInfoModel.toJson");
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
            return result;
        }
    }

    public String toString() {
        return toJson();
    }
}
