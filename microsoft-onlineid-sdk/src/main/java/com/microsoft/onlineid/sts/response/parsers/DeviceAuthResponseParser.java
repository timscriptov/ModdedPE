package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceAuthResponseParser extends BasePullParser {
    private int _authState;
    private final ClockSkewManager _clockSkewManager;
    private String _configVersion;
    private DAToken _daToken;
    private StsError _error;
    private Date _expires;
    private int _reqStatus;

    public DeviceAuthResponseParser(XmlPullParser underlyingParser, ClockSkewManager clockSkewManager) {
        super(underlyingParser, AbstractSoapRequest.SoapNamespace, "Envelope");
        this._clockSkewManager = clockSkewManager;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String tag = getPrefixedTagName();
            if (tag.equals("S:Header")) {
                ServiceHeaderParser parser = new ServiceHeaderParser(this._parser);
                parser.parse();
                this._expires = parser.getResponseExpiry();
                PassportParser passportParser = parser.getPassportParser();
                if (passportParser == null) {
                    throw new StsParseException("Missing passport node in device auth response.", new Object[0]);
                }
                this._authState = passportParser.getAuthState();
                this._reqStatus = passportParser.getReqStatus();
                this._configVersion = passportParser.getConfigVersion();
            } else if (tag.equals("S:Body")) {
                ServiceBodyParser parser2 = new ServiceBodyParser(this._parser);
                parser2.parse();
                this._error = parser2.getError();
                this._daToken = parser2.getDAToken();
            } else {
                skipElement();
            }
        }
        if (this._error == null && this._expires == null) {
            throw new StsParseException("S:Header tag not found", new Object[0]);
        } else if (this._error == null && this._daToken == null) {
            throw new StsParseException("S:Body tag not found", new Object[0]);
        } else {
            Date currentTime = this._clockSkewManager.getCurrentServerTime();
            if (this._expires != null && currentTime.after(this._expires)) {
                throw new StsParseException("Response is expired. Current time: %s Expiry Time: %s", currentTime.toString(), this._expires.toString());
            }
        }
    }

    Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    public String getConfigVersion() {
        verifyParseCalled();
        return this._configVersion;
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    public int getAuthState() {
        verifyParseCalled();
        return this._authState;
    }

    public int getReqStatus() {
        verifyParseCalled();
        return this._reqStatus;
    }
}
