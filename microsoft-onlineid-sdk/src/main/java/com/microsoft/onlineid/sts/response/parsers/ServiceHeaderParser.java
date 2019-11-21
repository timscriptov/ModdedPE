package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServiceHeaderParser extends BasePullParser {
    private byte[] _encKeyNonce;
    private String _encryptedHeader;
    private Date _expires;
    private PassportParser _passportParser;
    private final SignatureValidator _validator;

    public ServiceHeaderParser(XmlPullParser underlyingParser) {
        this(underlyingParser, null);
    }

    public ServiceHeaderParser(XmlPullParser underlyingParser, SignatureValidator validator) {
        super(underlyingParser, AbstractSoapRequest.SoapNamespace, "Header");
        this._validator = validator;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String tag = getPrefixedTagName();
            String id = this._parser.getAttributeValue(AbstractSoapRequest.WsuNamespace, "Id");
            if (tag.equals("wsse:Security")) {
                SecurityParser parser = new SecurityParser(this._parser, this._validator);
                parser.parse();
                this._expires = parser.getResponseExpiry();
                this._encKeyNonce = parser.getEncKeyNonce();
            } else if (tag.equals("psf:pp")) {
                this._passportParser = new PassportParser(this._parser);
                this._passportParser.parse();
            } else if (tag.equals("psf:EncryptedPP")) {
                NodeScope encryptedPPScope = getLocation();
                encryptedPPScope.nextStartTag("EncryptedData");
                EncryptedSoapNodeParser parser2 = new EncryptedSoapNodeParser(this._validator != null ? this._validator.computeNodeDigest(this) : this._parser);
                parser2.parse();
                this._encryptedHeader = parser2.getCipherValue();
                encryptedPPScope.finish();
            } else if (this._validator == null || TextUtils.isEmpty(id)) {
                skipElement();
            } else {
                this._validator.computeNodeDigest(this);
            }
        }
    }

    public Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    public byte[] getEncKeyNonce() {
        verifyParseCalled();
        return this._encKeyNonce;
    }

    public PassportParser getPassportParser() {
        verifyParseCalled();
        return this._passportParser;
    }

    public String getEncryptedHeader() {
        verifyParseCalled();
        return this._encryptedHeader;
    }
}
