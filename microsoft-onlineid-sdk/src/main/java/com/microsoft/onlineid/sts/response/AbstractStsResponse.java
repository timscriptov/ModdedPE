package com.microsoft.onlineid.sts.response;

import android.util.Xml;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.RedactableResponse;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class AbstractStsResponse {
    public abstract StsError getError();

    protected abstract void parse(XmlPullParser xmlPullParser) throws IOException, StsParseException;

    public void parse(InputStream stream) throws IOException, StsParseException {
        try {
            XmlPullParser underlyingParser = Xml.newPullParser();
            Logger.info(new RedactableResponse(String.format(Locale.US, "%s: %s", new Object[]{getClass().getSimpleName(), Strings.fromStream(stream, Strings.Utf8Charset)})));
            underlyingParser.setInput(new StringReader(responseXml));
            parse(underlyingParser);
            if (getError() != null) {
                StsError error = getError();
                ClientAnalytics.get().logEvent("Server errors", error.getCode().name(), getClass().getSimpleName() + ": " + error.getOriginalErrorMessage());
            }
        } catch (XmlPullParserException ex) {
            throw new StsParseException("XML response could not be properly parsed.", ex, new Object[0]);
        }
    }

    public boolean succeeded() {
        return getError() == null;
    }
}
