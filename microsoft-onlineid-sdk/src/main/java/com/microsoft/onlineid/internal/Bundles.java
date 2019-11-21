package com.microsoft.onlineid.internal;

import android.os.Bundle;
import com.microsoft.onlineid.internal.log.Logger;
import java.util.Map;
import java.util.Map.Entry;

public class Bundles {
    public static Bundle merge(Bundle... bundles) {
        Bundle result = new Bundle();
        for (Bundle input : bundles) {
            result.putAll(input);
        }
        return result;
    }

    public static Bundle fromStringMap(Map<String, String> map) {
        Bundle bundle = new Bundle();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                bundle.putString((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return bundle;
    }

    public static void log(String prefix, Bundle bundle) {
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                if (value instanceof Bundle) {
                    Logger.info(String.format("%s %s: %s (%s)", new Object[]{prefix, key, value.toString(), value.getClass().getName()}));
                    log(prefix + "  ", (Bundle) value);
                } else if (value != null) {
                    Logger.info(String.format("%s %s: %s (%s)", new Object[]{prefix, key, value.toString(), value.getClass().getName()}));
                } else {
                    Logger.info(String.format("%s %s: null", new Object[]{prefix, key}));
                }
            }
            return;
        }
        Logger.info(prefix + " (bundle was null)");
    }
}
