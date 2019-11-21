package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import com.microsoft.onlineid.sts.response.parsers.TokenParser.SecurityTokenMode;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TokenCollectionParser extends BasePullParser {
    private DAToken _daToken;
    private Ticket _ticket;
    private StsError _ticketError;
    private String _ticketInlineAuthUrl;
    private final ISecurityScope _ticketScope;

    public TokenCollectionParser(XmlPullParser underlyingParser, ISecurityScope ticketScope) {
        super(underlyingParser, AbstractSoapRequest.WstNamespace, "RequestSecurityTokenResponseCollection");
        this._ticketScope = ticketScope;
    }

    public TokenCollectionParser(XmlPullParser underlyingParser) {
        this(underlyingParser, null);
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow("wst:RequestSecurityTokenResponse")) {
            boolean z;
            TokenParser parser = new TokenParser(this._parser, this._ticketScope, SecurityTokenMode.ServiceRequest);
            parser.parse();
            if (parser.getDAToken() != null) {
                Assertion.check(this._daToken == null);
                this._daToken = parser.getDAToken();
            }
            if (parser.getTicketError() != null) {
                if (this._ticketError == null) {
                    z = true;
                } else {
                    z = false;
                }
                Assertion.check(z);
                this._ticketError = parser.getTicketError();
                this._ticketInlineAuthUrl = parser.getTicketInlineAuthUrl();
            }
            if (parser.getTicket() != null) {
                if (this._ticket == null) {
                    z = true;
                } else {
                    z = false;
                }
                Assertion.check(z);
                this._ticket = parser.getTicket();
            }
        }
        if (this._ticketScope != null && this._ticketError == null && this._ticket == null) {
            throw new StsParseException("No ticket or ticket error found.", new Object[0]);
        }
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
    }

    public Ticket getTicket() {
        verifyParseCalled();
        return this._ticket;
    }

    public StsError getTicketError() {
        verifyParseCalled();
        return this._ticketError;
    }

    public String getTicketInlineAuthUrl() {
        verifyParseCalled();
        return this._ticketInlineAuthUrl;
    }
}
