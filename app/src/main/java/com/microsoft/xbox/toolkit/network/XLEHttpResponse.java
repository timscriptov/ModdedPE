package com.microsoft.xbox.toolkit.network;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEHttpResponse {
    public int callbackPtr;
    public String[] headerArray;
    public int requestTypeTag;
    public byte[] responseBytes;
    public int statusCode;
}
