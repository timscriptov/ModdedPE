package com.microsoft.onlineid.internal;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.userdata.TelephonyManagerReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Uris {
    static final String EmailDelimiter = ",";
    static final String EmailParam = "email";
    static final String MktParam = "mkt";
    static final String PhoneParam = "phone";

    public static String mapToSortedQueryString(Map<String, String> map) {
        Builder builder = new Builder();
        for (Entry<String, String> pair : new TreeMap(map).entrySet()) {
            builder.appendQueryParameter((String) pair.getKey(), (String) pair.getValue());
        }
        return builder.build().getEncodedQuery();
    }

    public static Map<String, String> queryStringToMap(String queryString) {
        Map<String, String> result = new HashMap();
        if (!TextUtils.isEmpty(queryString)) {
            Uri uri = Uri.parse("?" + queryString);
            for (String parameterName : uri.getQueryParameterNames()) {
                result.put(parameterName, uri.getQueryParameter(parameterName));
            }
        }
        return result;
    }

    public static Uri appendMarketQueryString(Context applicationContext, Uri original) {
        if (TextUtils.isEmpty(original.getQueryParameter(MktParam))) {
            String mkt = Resources.getString(applicationContext, "app_market");
            Builder buildUpon = original.buildUpon();
            String str = MktParam;
            if (TextUtils.isEmpty(mkt)) {
                mkt = "en";
            }
            return buildUpon.appendQueryParameter(str, mkt).build();
        }
        Logger.warning("Given URL already has mkt parameter set.");
        return original;
    }

    public static Uri appendPhoneDigits(TelephonyManagerReader telephonyManagerReader, Uri original) {
        if (TextUtils.isEmpty(original.getQueryParameter(PhoneParam))) {
            String phoneNumber = telephonyManagerReader.getPhoneNumber();
            return original.buildUpon().appendQueryParameter(PhoneParam, TextUtils.isEmpty(phoneNumber) ? BuildConfig.VERSION_NAME : phoneNumber.replaceAll("[^\\d]+", BuildConfig.VERSION_NAME)).build();
        }
        Logger.warning("Given URL already has phone parameter set.");
        return original;
    }
}
