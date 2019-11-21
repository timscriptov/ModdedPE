package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PassportParser extends BasePullParser {
    private int _authState;
    private String _configVersion;
    private final Set<Integer> _flights = new HashSet();
    private String _inlineAuthUrl;
    private String _nonce;
    private String _puid;
    private int _reqStatus;
    private UserProperties _userProperties;

    public PassportParser(XmlPullParser underlyingParser) {
        super(underlyingParser, null, "pp");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String tag = getPrefixedTagName();
            if (tag.equals("psf:authstate")) {
                this._authState = TextParsers.parseIntHex(nextRequiredText());
            } else if (tag.equals("psf:reqstatus")) {
                this._reqStatus = TextParsers.parseIntHex(nextRequiredText());
            } else if (tag.equals("psf:inlineauthurl")) {
                this._inlineAuthUrl = nextRequiredText();
            } else if (tag.equals("psf:signChallenge")) {
                this._nonce = nextRequiredText();
            } else if (tag.equals("psf:configVersion")) {
                this._configVersion = nextRequiredText();
            } else if (tag.equals("psf:PUID")) {
                this._puid = nextRequiredText();
            } else if (tag.equals("psf:flights")) {
                for (String flight : nextRequiredText().split(",")) {
                    this._flights.add(Integer.valueOf(TextParsers.parseIntHex(flight)));
                }
            } else if (tag.equals("psf:credProperties")) {
                UserPropertiesParser parser = new UserPropertiesParser(this._parser);
                parser.parse();
                this._userProperties = parser.getUserProperties();
            } else {
                skipElement();
            }
        }
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

    public String getNonce() {
        verifyParseCalled();
        return this._nonce;
    }

    public String getConfigVersion() {
        verifyParseCalled();
        return this._configVersion;
    }

    public String getPuid() {
        verifyParseCalled();
        return this._puid;
    }

    public UserProperties getUserProperties() {
        verifyParseCalled();
        return this._userProperties;
    }

    public Set<Integer> getFlights() {
        verifyParseCalled();
        return this._flights;
    }
}
