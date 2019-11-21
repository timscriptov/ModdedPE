package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SecurityTokenParser extends BasePullParser {
    private String _tokenBlob;

    public SecurityTokenParser(XmlPullParser underlyingParser) {
        super(underlyingParser, AbstractSoapRequest.WstNamespace, "RequestedSecurityToken");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String tagName = getPrefixedTagName();
            boolean z;
            if (tagName.equals("EncryptedData")) {
                if (this._tokenBlob == null) {
                    z = true;
                } else {
                    z = false;
                }
                Assertion.check(z);
                this._tokenBlob = readRawOuterXml();
            } else if (tagName.equals("wsse:BinarySecurityToken")) {
                if (this._tokenBlob == null) {
                    z = true;
                } else {
                    z = false;
                }
                Assertion.check(z);
                this._tokenBlob = nextRequiredText();
            } else {
                skipElement();
            }
        }
    }

    public String getTokenBlob() {
        verifyParseCalled();
        return this._tokenBlob;
    }
}
