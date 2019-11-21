package com.microsoft.onlineid.sts.response;

import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.DeviceAuthResponseParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;

public class DeviceAuthResponse extends AbstractSoapResponse {
    private final ClockSkewManager _clockSkewManager;
    private DeviceAuthResponseParser _parser;

    public DeviceAuthResponse(ClockSkewManager clockSkewManager) {
        this._clockSkewManager = clockSkewManager;
    }

    protected void parse(XmlPullParser underlyingParser) throws IOException, StsParseException {
        if (this._parser != null) {
            throw new IllegalStateException("Each response object may only parse its respone once.");
        }
        this._parser = new DeviceAuthResponseParser(underlyingParser, this._clockSkewManager);
        this._parser.parse();
    }

    public StsError getError() {
        return this._parser.getError();
    }

    public DAToken getDAToken() {
        return this._parser.getDAToken();
    }
}
