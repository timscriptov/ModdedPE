package com.microsoft.onlineid.sts.response;

import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.DeviceProvisionResponseParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;

public class DeviceProvisionResponse extends AbstractStsResponse {
    private DeviceProvisionResponseParser _parser;

    protected void parse(XmlPullParser underlyingParser) throws IOException, StsParseException {
        if (this._parser != null) {
            throw new IllegalStateException("Each response object may only parse its respone once.");
        }
        this._parser = new DeviceProvisionResponseParser(underlyingParser);
        this._parser.parse();
    }

    public String getPuid() {
        return this._parser.getPuid();
    }

    public StsError getError() {
        return this._parser.getError();
    }
}
