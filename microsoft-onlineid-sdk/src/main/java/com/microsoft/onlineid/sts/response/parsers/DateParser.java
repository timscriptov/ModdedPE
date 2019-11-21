package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DateParser extends BasePullParser {
    private Date _date;
    private final DateType _type;

    enum DateType {
        SecondsSinceEpoch {
            public Date parse(String timeString) throws StsParseException {
                try {
                    return new Date(1000 * Long.parseLong(timeString));
                } catch (IllegalArgumentException ex) {
                    throw new StsParseException("Cannot parse date node: %s", ex, timeString);
                }
            }
        },
        Iso8601DateTimeIgnoreTimeZone {
            public Date parse(String timeString) throws StsParseException {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return dateFormat.parse(timeString);
                } catch (ParseException e) {
                    throw new StsParseException(e);
                }
            }
        };

        public abstract Date parse(String str) throws StsParseException;
    }

    public DateParser(XmlPullParser parser, DateType type) throws XmlPullParserException {
        super(parser, null, null);
        this._type = type;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        this._date = this._type.parse(nextRequiredText());
    }

    Date getDate() {
        verifyParseCalled();
        return this._date;
    }
}
