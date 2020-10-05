package com.microsoft.xbox.idp.util;

import android.net.Uri;
import android.text.TextUtils;

import com.microsoft.aad.adal.WebRequestHandler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpUtil {

    public static Uri.Builder getImageSizeUrlParams(@NotNull Uri.Builder b, @NotNull ImageSize sz) {
        return b.appendQueryParameter("w", Integer.toString(sz.w)).appendQueryParameter("h", Integer.toString(sz.h));
    }

    @NotNull
    public static String getEndpoint(@NotNull Uri uri) {
        return uri.getScheme() + "://" + uri.getEncodedAuthority();
    }

    @NotNull
    public static String getPathAndQuery(@NotNull Uri uri) {
        String path = uri.getEncodedPath();
        String query = uri.getEncodedQuery();
        String fragment = uri.getEncodedFragment();
        StringBuffer sb = new StringBuffer();
        sb.append(path);
        if (!TextUtils.isEmpty(query)) {
            sb.append("?").append(query);
        }
        if (!TextUtils.isEmpty(fragment)) {
            sb.append("#").append(fragment);
        }
        return sb.toString();
    }

    @NotNull
    @Contract("_, _ -> param1")
    public static HttpCall appendCommonParameters(@NotNull HttpCall httpCall, String version) {
        httpCall.setXboxContractVersionHeaderValue(version);
        httpCall.setContentTypeHeaderValue(WebRequestHandler.HEADER_ACCEPT_JSON);
        httpCall.setRetryAllowed(true);
        return httpCall;
    }

    public enum ImageSize {
        SMALL(64, 64),
        MEDIUM(208, 208),
        LARGE(424, 424);

        public final int h;
        public final int w;

        private ImageSize(int w2, int h2) {
            this.w = w2;
            this.h = h2;
        }
    }
}
