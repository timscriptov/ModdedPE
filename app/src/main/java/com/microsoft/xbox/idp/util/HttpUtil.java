package com.microsoft.xbox.idp.util;

import android.net.Uri;
import android.text.TextUtils;

import com.microsoft.aad.adal.WebRequestHandler;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class HttpUtil {

    public static Uri.Builder getImageSizeUrlParams(Uri.@NotNull Builder builder, @NotNull ImageSize imageSize) {
        return builder.appendQueryParameter("w", Integer.toString(imageSize.w)).appendQueryParameter("h", Integer.toString(imageSize.h));
    }

    public static @NotNull String getEndpoint(@NotNull Uri uri) {
        return uri.getScheme() + "://" + uri.getEncodedAuthority();
    }

    public static @NotNull String getPathAndQuery(@NotNull Uri uri) {
        String encodedPath = uri.getEncodedPath();
        String encodedQuery = uri.getEncodedQuery();
        String encodedFragment = uri.getEncodedFragment();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(encodedPath);
        if (!TextUtils.isEmpty(encodedQuery)) {
            stringBuffer.append("?");
            stringBuffer.append(encodedQuery);
        }
        if (!TextUtils.isEmpty(encodedFragment)) {
            stringBuffer.append("#");
            stringBuffer.append(encodedFragment);
        }
        return stringBuffer.toString();
    }

    @Contract("_, _ -> param1")
    public static @NotNull HttpCall appendCommonParameters(@NotNull HttpCall httpCall, String str) {
        httpCall.setXboxContractVersionHeaderValue(str);
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

        private ImageSize(int i, int i2) {
            this.w = i;
            this.h = i2;
        }
    }
}
