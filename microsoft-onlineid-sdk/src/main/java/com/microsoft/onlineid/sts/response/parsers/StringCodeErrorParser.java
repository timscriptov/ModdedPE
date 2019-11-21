package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Integers;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sts.StringCodeServerError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class StringCodeErrorParser extends BasePullParser {
    private String _code;
    private StringCodeServerError _error;
    private Integer _subCode;

    public StringCodeErrorParser(XmlPullParser parser) throws XmlPullParserException {
        super(parser, null, null);
    }

    public StringCodeServerError getError() {
        verifyParseCalled();
        return this._error;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String tagName = this._parser.getName();
            if (tagName.equals("Error")) {
                this._code = this._parser.getAttributeValue(BuildConfig.VERSION_NAME, "Code");
            } else if (tagName.equals("ErrorSubcode")) {
                try {
                    this._subCode = Integer.valueOf(Integers.parseIntHex(this._parser.nextText()));
                } catch (IllegalArgumentException ex) {
                    throw new StsParseException("Hex error code could not be parsed: %s.", ex, hexCode);
                }
            } else {
                skipElement();
            }
        }
        if (this._code == null) {
            throw new StsParseException("Required node \"Error\" is missing or empty.", new Object[0]);
        } else if (this._subCode == null) {
            throw new StsParseException("Required node \"ErrorSubcode\" is missing.", new Object[0]);
        } else {
            this._error = new StringCodeServerError(this._code, this._subCode.intValue());
        }
    }
}
