package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.IntegerCodeServerError;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FaultParser extends BasePullParser {
    private StsError _error;

    public FaultParser(XmlPullParser underlyingParser) {
        super(underlyingParser, AbstractSoapRequest.SoapNamespace, "Fault");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("S:Detail");
        NodeScope detailScope = getLocation();
        detailScope.nextStartTag("psf:error");
        Integer errorCode = null;
        Integer internalError = null;
        String errorMessage = null;
        NodeScope errorScope = getLocation();
        while (errorScope.nextStartTagNoThrow()) {
            String tag = getPrefixedTagName();
            if (tag.equals("psf:value")) {
                errorCode = Integer.valueOf(TextParsers.parseIntHex(nextRequiredText()));
            } else if (tag.equals("psf:internalerror")) {
                NodeScope internalErrorScope = getLocation();
                while (internalErrorScope.nextStartTagNoThrow()) {
                    tag = getPrefixedTagName();
                    if (tag.equals("psf:code")) {
                        internalError = Integer.valueOf(TextParsers.parseIntHex(nextRequiredText()));
                    } else if (tag.equals("psf:text")) {
                        errorMessage = this._parser.nextText();
                    } else {
                        skipElement();
                    }
                }
            } else {
                skipElement();
            }
        }
        detailScope.finish();
        if (errorCode == null) {
            throw new StsParseException("psf:value node does not exist.", new Object[0]);
        } else if (internalError == null) {
            throw new StsParseException("psf:code node does not exist.", new Object[0]);
        } else {
            this._error = new StsError(new IntegerCodeServerError(errorCode.intValue(), internalError.intValue(), errorMessage));
        }
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }
}
