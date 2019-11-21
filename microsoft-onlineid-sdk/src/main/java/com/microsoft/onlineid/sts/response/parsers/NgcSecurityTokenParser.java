package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class NgcSecurityTokenParser extends BasePullParser {
    private String _tokenBlob;

    public NgcSecurityTokenParser(XmlPullParser underlyingParser) {
        super(underlyingParser, AbstractSoapRequest.WstNamespace, "RequestedSecurityToken");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("EncryptedData");
        EncryptedSoapNodeParser esnParser = new EncryptedSoapNodeParser(this._parser);
        esnParser.parse();
        this._tokenBlob = esnParser.getCipherValue();
    }

    public String getTokenBlob() {
        verifyParseCalled();
        return this._tokenBlob;
    }
}
