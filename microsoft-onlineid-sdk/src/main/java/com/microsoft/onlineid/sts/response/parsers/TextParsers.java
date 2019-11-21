package com.microsoft.onlineid.sts.response.parsers;

import android.util.Base64;
import com.microsoft.onlineid.internal.Integers;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.net.MalformedURLException;
import java.net.URL;

class TextParsers {
    TextParsers() {
    }

    static byte[] parseBase64(String blob) throws StsParseException {
        try {
            return Base64.decode(blob, 2);
        } catch (NullPointerException e) {
            throw new StsParseException(e);
        } catch (IllegalArgumentException e2) {
            throw new StsParseException(e2);
        }
    }

    static int parseIntHex(String hexInt) throws StsParseException {
        try {
            return Integers.parseIntHex(hexInt);
        } catch (IllegalArgumentException e) {
            throw new StsParseException(e);
        }
    }

    static int parseInt(String value, String errorMessage) throws StsParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new StsParseException(errorMessage, e, new Object[0]);
        }
    }

    static URL parseUrl(String value, String errorMessage) throws StsParseException {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new StsParseException(errorMessage, e, new Object[0]);
        }
    }
}
