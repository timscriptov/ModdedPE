package com.microsoft.xbox.xle.app;

import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.Nullable;
import org.spongycastle.crypto.tls.CipherSuite;

import java.net.URI;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ImageUtil {
    public static final int LARGE_PHONE = 640;
    public static final int LARGE_TABLET = 800;
    public static final int MEDIUM_PHONE = 300;
    public static final int MEDIUM_TABLET = 424;
    public static final int SMALL = 200;
    public static final int TINY = 100;
    public static final String resizeFormatter = "&w=%d&h=%d&format=png";
    public static final String resizeFormatterSizeOnly = "&w=%d&h=%d";
    public static final String resizeFormatterWithPadding = "&mode=padding&w=%d&h=%d&format=png";

    public static URI getURI(URI url, int width, int height) {
        return formatURI(url, width, height);
    }

    public static URI getURI(String url, int width, int heigth) {
        URI uri = formatString(url, width, heigth);
        if (uri == null) {
            return createUri(url);
        }
        return uri;
    }

    public static URI getTiny(URI url) {
        return formatURI(url, 100, 100);
    }

    public static URI getTiny(String url) {
        URI uri = formatString(url, 100, 100);
        if (uri == null) {
            return createUri(url);
        }
        return uri;
    }

    public static URI getTiny3X4(URI url) {
        return formatURI(url, 85, 120);
    }

    public static URI getTiny3X4(String url) {
        return formatString(url, 85, 120);
    }

    public static URI getTiny4X3(URI url) {
        return formatURI(url, 120, 90);
    }

    public static URI getTiny4X3(String url) {
        return formatString(url, 120, 90);
    }

    public static URI getTiny2X1(URI url) {
        return formatURI(url, CipherSuite.TLS_RSA_WITH_SEED_CBC_SHA, 84);
    }

    public static URI getTiny2X1(String url) {
        return formatString(url, CipherSuite.TLS_RSA_WITH_SEED_CBC_SHA, 84);
    }

    public static URI getSmall(URI url) {
        return formatURI(url, 200, 200);
    }

    public static URI getSmall(String url) {
        URI uri = formatString(url, 200, 200);
        if (uri != null || url == null) {
            return uri;
        }
        return createUri(url);
    }

    public static URI getSmall3X4(URI url) {
        return formatURI(url, 215, 294);
    }

    public static URI getSmall3X4(String url) {
        return formatString(url, 215, 294);
    }

    public static URI getSmall4X3(URI url) {
        return formatURI(url, 275, 216);
    }

    public static URI getSmall4X3(String url) {
        return formatString(url, 275, 216);
    }

    public static URI getSmall2X1(URI url) {
        return formatURI(url, 243, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA);
    }

    public static URI getSmall2X1(String url) {
        return formatString(url, 243, CipherSuite.TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA);
    }

    public static URI getMedium(URI url) {
        if (XboxTcuiSdk.getIsTablet()) {
            return formatURI(url, 424, 424);
        }
        return formatURI(url, 300, 300);
    }

    public static URI getMedium(String url) {
        URI uri;
        if (XboxTcuiSdk.getIsTablet()) {
            uri = formatString(url, 424, 424);
        } else {
            uri = formatString(url, 300, 300);
        }
        if (uri != null || url == null) {
            return uri;
        }
        return createUri(url);
    }

    public static URI getMedium3X4(URI url) {
        return formatURI(url, 426, LARGE_PHONE);
    }

    public static URI getMedium3X4(String url) {
        return formatString(url, 426, LARGE_PHONE);
    }

    public static URI getMedium4X3(URI url) {
        return formatURI(url, 562, 316);
    }

    public static URI getMedium4X3(String url) {
        return formatString(url, 562, 316);
    }

    public static URI getMedium2X1(URI url) {
        return formatURI(url, 480, 270);
    }

    public static URI getMedium2X1(String url) {
        return formatString(url, 480, 270);
    }

    public static URI getLarge(URI url) {
        if (XboxTcuiSdk.getIsTablet()) {
            return formatURI(url, LARGE_TABLET, LARGE_TABLET);
        }
        return formatURI(url, LARGE_PHONE, LARGE_PHONE);
    }

    public static URI getLarge(String url) {
        URI uri;
        if (XboxTcuiSdk.getIsTablet()) {
            uri = formatString(url, LARGE_TABLET, LARGE_TABLET);
        } else {
            uri = formatString(url, LARGE_PHONE, LARGE_PHONE);
        }
        if (uri != null || url == null) {
            return uri;
        }
        return createUri(url);
    }

    public static URI getLarge3X4(URI url) {
        return formatURI(url, 720, 1080);
    }

    public static URI getLarge3X4(String url) {
        return formatString(url, 720, 1080);
    }

    private static URI formatURI(URI url, int width, int height) {
        if (url == null) {
            return null;
        }
        URI newURI = formatString(url.toString(), width, height);
        if (newURI != null) {
            return newURI;
        }
        return url;
    }

    private static URI formatString(String url, int width, int height) {
        if (url == null || !url.contains("images-eds")) {
            return null;
        }
        boolean hasWidth = url.contains("&w=");
        boolean hasHeight = url.contains("&h=");
        if (hasWidth && hasHeight) {
            return createUri(url.replaceAll("w=[0-9]+", "w=" + width).replaceAll("h=[0-9]+", "h=" + height));
        }
        if (hasWidth) {
            return createUri(url.replaceAll("w=[0-9]+", "w=" + width) + "&h=" + height);
        }
        if (hasHeight) {
            return createUri(url.replaceAll("h=[0-9]+", "h=" + height) + "&w=" + width);
        }
        if (url.contains("format=")) {
            return createUri(url + String.format(resizeFormatterSizeOnly, new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        }
        return createUri(url + String.format(resizeFormatter, new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
    }

    private static URI createUri(String url) {
        if (url == null) {
            return null;
        }
        try {
            return URI.create(url);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static URI getUri(String url, ImageType imgType) {
        if (imgType == null) {
            return getSmall(url);
        }
        switch (imgType) {
            case TINY:
                return getTiny(url);
            case TINY_3X4:
                return getTiny3X4(url);
            case TINY_4X3:
                return getTiny4X3(url);
            case SMALL:
                return getSmall(url);
            case SMALL_3X4:
                return getSmall3X4(url);
            case SMALL_4X3:
                return getSmall4X3(url);
            case MEDIUM:
                return getMedium(url);
            case MEDIUM_3X4:
                return getMedium3X4(url);
            case MEDIUM_4X3:
                return getMedium4X3(url);
            case LARGE:
                return getLarge(url);
            case LARGE_3X4:
                return getLarge3X4(url);
            default:
                return getSmall(url);
        }
    }

    public static URI getUri(URI url, ImageType imgType) {
        if (imgType == null) {
            return getSmall(url);
        }
        switch (imgType) {
            case TINY:
                return getTiny(url);
            case TINY_3X4:
                return getTiny3X4(url);
            case TINY_4X3:
                return getTiny4X3(url);
            case SMALL:
                return getSmall(url);
            case SMALL_3X4:
                return getSmall3X4(url);
            case SMALL_4X3:
                return getSmall4X3(url);
            case MEDIUM:
                return getMedium(url);
            case MEDIUM_3X4:
                return getMedium3X4(url);
            case MEDIUM_4X3:
                return getMedium4X3(url);
            case LARGE:
                return getLarge(url);
            case LARGE_3X4:
                return getLarge3X4(url);
            default:
                return getSmall(url);
        }
    }

    public enum ImageType {
        TINY,
        TINY_3X4,
        TINY_4X3,
        SMALL,
        SMALL_3X4,
        SMALL_4X3,
        MEDIUM,
        MEDIUM_3X4,
        MEDIUM_4X3,
        LARGE,
        LARGE_3X4;

        @Nullable
        public static ImageType fromString(String val) {
            try {
                return valueOf(val);
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
        }
    }
}
