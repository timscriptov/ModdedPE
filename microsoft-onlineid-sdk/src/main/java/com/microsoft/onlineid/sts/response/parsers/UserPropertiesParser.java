package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.UserProperties.UserProperty;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class UserPropertiesParser extends BasePullParser {
    private final UserProperties _userProperties = new UserProperties();

    public UserPropertiesParser(XmlPullParser underlyingParser) {
        super(underlyingParser, AbstractSoapRequest.PsfNamespace, "credProperties");
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String name = this._parser.getAttributeValue(BuildConfig.VERSION_NAME, "Name");
            if (name == null) {
                skipElement();
            } else {
                try {
                    this._userProperties.put(UserProperty.valueOf(name), this._parser.nextText());
                } catch (IllegalArgumentException e) {
                    skipElement();
                }
            }
        }
    }

    public UserProperties getUserProperties() {
        verifyParseCalled();
        return this._userProperties;
    }
}
