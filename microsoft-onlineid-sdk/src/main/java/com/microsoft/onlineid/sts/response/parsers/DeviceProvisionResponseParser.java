package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceProvisionResponseParser extends BasePullParser {
    private StsError _error;
    private String _puid;

    public DeviceProvisionResponseParser(XmlPullParser underlyingParser) {
        super(underlyingParser, BuildConfig.VERSION_NAME, "DeviceAddResponse");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        if (Strings.equalsIgnoreCase(this._parser.getAttributeValue(BuildConfig.VERSION_NAME, "Success"), "true")) {
            nextStartTag("puid");
            this._puid = nextRequiredText();
            return;
        }
        StringCodeErrorParser parser = new StringCodeErrorParser(this._parser);
        parser.parse();
        this._error = new StsError(parser.getError());
    }

    public String getPuid() {
        verifyParseCalled();
        return this._puid;
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }
}
