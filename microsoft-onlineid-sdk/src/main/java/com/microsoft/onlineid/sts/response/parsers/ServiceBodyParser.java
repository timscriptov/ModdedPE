package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import com.microsoft.onlineid.sts.response.parsers.TokenParser.SecurityTokenMode;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServiceBodyParser extends BasePullParser {
    private DAToken _daToken;
    private String _encryptedBody;
    private StsError _error;

    public ServiceBodyParser(XmlPullParser underlyingParser) {
        super(underlyingParser, AbstractSoapRequest.SoapNamespace, "Body");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String tag = getPrefixedTagName();
            if (tag.equals("S:Fault")) {
                FaultParser parser = new FaultParser(this._parser);
                parser.parse();
                this._error = parser.getError();
            } else if (tag.equals("EncryptedData")) {
                EncryptedSoapNodeParser parser2 = new EncryptedSoapNodeParser(this._parser);
                parser2.parse();
                this._encryptedBody = parser2.getCipherValue();
            } else if (tag.equals("wst:RequestSecurityTokenResponse")) {
                TokenParser tokenParser = new TokenParser(this._parser, SecurityTokenMode.ServiceRequest);
                tokenParser.parse();
                this._daToken = tokenParser.getDAToken();
            } else {
                skipElement();
            }
        }
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    public String getEncryptedBody() {
        verifyParseCalled();
        return this._encryptedBody;
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
    }
}
