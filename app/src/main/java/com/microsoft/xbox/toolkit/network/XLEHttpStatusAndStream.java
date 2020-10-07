package com.microsoft.xbox.toolkit.network;

import org.apache.http.Header;

import java.io.InputStream;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEHttpStatusAndStream {
    public Header[] headers = new Header[0];
    public String redirectUrl = null;
    public int statusCode = -1;
    public String statusLine = null;
    public InputStream stream = null;

    public void close() {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
