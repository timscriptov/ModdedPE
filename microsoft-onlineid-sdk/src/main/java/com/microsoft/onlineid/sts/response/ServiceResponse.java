package com.microsoft.onlineid.sts.response;

import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.ServiceResponseParser;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;

public class ServiceResponse extends AbstractSoapResponse {
    private final ClockSkewManager _clockSkewManager;
    private final byte[] _decryptionSessionKey;
    private ServiceResponseParser _parser;
    private final ISecurityScope _ticketScope;

    public ServiceResponse(byte[] sessionKey, ISecurityScope scope, ClockSkewManager clockSkewManager) {
        this._decryptionSessionKey = sessionKey;
        this._ticketScope = scope;
        this._clockSkewManager = clockSkewManager;
    }

    public ServiceResponse(byte[] sessionKey, ClockSkewManager clockSkewManager) {
        this(sessionKey, null, clockSkewManager);
    }

    protected void parse(XmlPullParser underlyingParser) throws StsParseException, IOException {
        if (this._parser != null) {
            throw new IllegalStateException("Each response object may only parse its respone once.");
        }
        this._parser = new ServiceResponseParser(underlyingParser, this._decryptionSessionKey, this._ticketScope);
        this._parser.parse();
        validateExpirationTime();
    }

    public Ticket getTicket() {
        return this._parser.getTicket();
    }

    public StsError getTicketError() {
        return this._parser.getTicketError();
    }

    public DAToken getDAToken() {
        return this._parser.getDAToken();
    }

    public UserProperties getUserProperties() {
        return this._parser.getUserProperties();
    }

    public String getPuid() {
        return this._parser.getPuid();
    }

    public Set<Integer> getFlights() {
        return this._parser.getFlights();
    }

    public String getConfigVersion() {
        return this._parser.getConfigVersion();
    }

    public StsError getError() {
        StsError error = this._parser.getError();
        if (error == null) {
            return this._parser.getTicketError();
        }
        return error;
    }

    protected void validateExpirationTime() throws StsParseException {
        Date currentTime = this._clockSkewManager.getCurrentServerTime();
        Date expiryTime = this._parser.getResponseExpiry();
        if (expiryTime != null && currentTime.after(expiryTime)) {
            throw new StsParseException("Response is expired. Current time: %s Expiry Time: %s", currentTime.toString(), expiryTime.toString());
        }
    }

    public String getInlineAuthUrl() {
        String url = this._parser.getInlineAuthUrl();
        if (TextUtils.isEmpty(url)) {
            return this._parser.getTicketInlineAuthUrl();
        }
        return url;
    }
}
