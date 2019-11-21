package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.RedactableResponse;
import com.microsoft.onlineid.sts.Cryptography;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.SharedKeyGenerator;
import com.microsoft.onlineid.sts.SharedKeyGenerator.KeyPurpose;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.UserProperties.UserProperty;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServiceResponseParser extends BasePullParser {
    private int _authState;
    private String _configVersion;
    private DAToken _daToken;
    private byte[] _encKeyNonce;
    private StsError _error;
    private Date _expires;
    private Set<Integer> _flights;
    private String _inlineAuthUrl;
    private String _puid;
    private int _reqStatus;
    private final byte[] _sessionKey;
    private Ticket _ticket;
    private StsError _ticketError;
    private String _ticketInlineAuthUrl;
    private final ISecurityScope _ticketScope;
    private UserProperties _userProperties;

    public ServiceResponseParser(XmlPullParser parser, byte[] sessionKey, ISecurityScope ticketScope) {
        super(parser, AbstractSoapRequest.SoapNamespace, "Envelope");
        this._sessionKey = sessionKey;
        this._ticketScope = ticketScope;
    }

    public ServiceResponseParser(XmlPullParser parser, byte[] sessionKey) {
        this(parser, sessionKey, null);
    }

    protected void onParse() throws IOException, StsParseException, XmlPullParserException {
        PassportParser unencryptedPassportParser = null;
        String encryptedHeader = null;
        String encryptedBody = null;
        SignatureValidator validator = new SignatureValidator(this._sessionKey);
        while (nextStartTagNoThrow()) {
            String tag = getPrefixedTagName();
            if (tag.equals("S:Header")) {
                ServiceHeaderParser parser = new ServiceHeaderParser(this._parser, validator);
                parser.parse();
                this._expires = parser.getResponseExpiry();
                this._encKeyNonce = parser.getEncKeyNonce();
                unencryptedPassportParser = parser.getPassportParser();
                encryptedHeader = parser.getEncryptedHeader();
            } else if (tag.equals("S:Body")) {
                ServiceBodyParser parser2 = new ServiceBodyParser(validator.computeNodeDigest(this));
                parser2.parse();
                this._error = parser2.getError();
                encryptedBody = parser2.getEncryptedBody();
            } else {
                skipElement();
            }
        }
        if (this._encKeyNonce != null || validator.canValidate()) {
            validator.validate();
        }
        if (!TextUtils.isEmpty(encryptedHeader)) {
            String decryptedHeader = decryptEncryptedBlob(encryptedHeader);
            Logger.info(new RedactableResponse("Decrypted service response header: " + decryptedHeader));
            parseAndSaveFromPassport(decryptedHeader);
        } else if (unencryptedPassportParser != null) {
            saveFromPassport(unencryptedPassportParser);
        }
        if (this._error == null) {
            String decryptedBody = decryptEncryptedBlob(encryptedBody);
            Logger.info(new RedactableResponse("Decrypted service response body: " + decryptedBody));
            parseAndSaveFromTokenCollection(decryptedBody);
        }
    }

    private void parseAndSaveFromPassport(String pp) throws StsParseException, IOException, XmlPullParserException {
        XmlPullParser underlyingParser = Xml.newPullParser();
        underlyingParser.setInput(new StringReader(pp));
        PassportParser parser = new PassportParser(underlyingParser);
        parser.parse();
        saveFromPassport(parser);
    }

    private void saveFromPassport(PassportParser parser) throws StsParseException, IOException, XmlPullParserException {
        this._authState = parser.getAuthState();
        this._reqStatus = parser.getReqStatus();
        this._inlineAuthUrl = parser.getInlineAuthUrl();
        this._configVersion = parser.getConfigVersion();
        this._puid = parser.getPuid();
        this._userProperties = parser.getUserProperties();
        this._flights = parser.getFlights();
        if (this._userProperties != null && this._userProperties.get(UserProperty.CID) == null) {
            throw new StsParseException("CID not found.", new Object[0]);
        }
    }

    private void parseAndSaveFromTokenCollection(String responseTokenCollection) throws XmlPullParserException, IOException, StsParseException {
        XmlPullParser underlyingParser = Xml.newPullParser();
        underlyingParser.setInput(new StringReader(responseTokenCollection));
        TokenCollectionParser tokenCollectionParser = new TokenCollectionParser(underlyingParser, this._ticketScope);
        tokenCollectionParser.parse();
        this._daToken = tokenCollectionParser.getDAToken();
        this._ticket = tokenCollectionParser.getTicket();
        this._ticketError = tokenCollectionParser.getTicketError();
        this._ticketInlineAuthUrl = tokenCollectionParser.getTicketInlineAuthUrl();
    }

    private String decryptEncryptedBlob(String encryptedBlob) throws StsParseException {
        try {
            return new String(Cryptography.decryptWithAesCbcPcs5PaddingCipher(TextParsers.parseBase64(encryptedBlob), new SharedKeyGenerator(this._sessionKey).generateKey(KeyPurpose.STSDigest, this._encKeyNonce)), Strings.Utf8Charset);
        } catch (IllegalBlockSizeException e) {
            throw new StsParseException(e);
        } catch (BadPaddingException e2) {
            throw new StsParseException(e2);
        }
    }

    public byte[] getEncKeyNonce() {
        verifyParseCalled();
        return this._encKeyNonce;
    }

    public Ticket getTicket() {
        verifyParseCalled();
        return this._ticket;
    }

    public String getPuid() {
        verifyParseCalled();
        return this._puid;
    }

    public String getConfigVersion() {
        verifyParseCalled();
        return this._configVersion;
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
    }

    public Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    public UserProperties getUserProperties() {
        verifyParseCalled();
        return this._userProperties;
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

    public String getInlineAuthUrl() {
        verifyParseCalled();
        return this._inlineAuthUrl;
    }

    public StsError getTicketError() {
        verifyParseCalled();
        return this._ticketError;
    }

    public String getTicketInlineAuthUrl() {
        verifyParseCalled();
        return this._ticketInlineAuthUrl;
    }

    public Set<Integer> getFlights() {
        verifyParseCalled();
        return this._flights;
    }
}
