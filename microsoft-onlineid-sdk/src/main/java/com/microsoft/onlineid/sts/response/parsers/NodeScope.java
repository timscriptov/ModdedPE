package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import com.microsoft.onlineid.sdk.R;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class NodeScope {
    private final int _depth;
    private final XmlPullParser _parser;

    NodeScope(XmlPullParser parser) {
        this._parser = parser;
        this._depth = parser.getDepth();
    }

    boolean hasMore() throws XmlPullParserException {
        switch (this._parser.getEventType()) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return false;
            case 3:
                if (this._depth == this._parser.getDepth()) {
                    return false;
                }
                break;
        }
        return true;
    }

    void finish() throws XmlPullParserException, IOException {
        while (hasMore()) {
            this._parser.next();
        }
    }

    int getDepth() {
        return this._depth;
    }

    protected void skipElement() throws XmlPullParserException, IOException {
        int startDepth = this._parser.getDepth();
        if (startDepth == this._depth) {
            finish();
            return;
        }
        int eventType = this._parser.getEventType();
        while (true) {
            if (startDepth != this._parser.getDepth() || eventType != 3) {
                eventType = this._parser.next();
            } else {
                return;
            }
        }
    }

    boolean nextStartTagNoThrow() throws XmlPullParserException, IOException {
        while (hasMore()) {
            if (this._parser.next() == 2) {
                return true;
            }
        }
        return false;
    }

    boolean nextStartTagNoThrow(String prefixedTag) throws XmlPullParserException, IOException {
        while (nextStartTagNoThrow()) {
            if (BasePullParser.getPrefixedTagName(this._parser).equals(prefixedTag)) {
                return true;
            }
            skipElement();
        }
        return false;
    }

    void nextStartTag(String prefixedTag) throws XmlPullParserException, IOException, StsParseException {
        if (!nextStartTagNoThrow(prefixedTag)) {
            throw new StsParseException("Required node \"%s\" is missing.", prefixedTag);
        }
    }

    String nextRequiredText() throws XmlPullParserException, IOException, StsParseException {
        String tag = this._parser.getName();
        String innerText = this._parser.nextText();
        if (!TextUtils.isEmpty(innerText)) {
            return innerText;
        }
        throw new StsParseException(String.format(Locale.US, "Expected text of %s is empty", new Object[]{tag}), new Object[0]);
    }
}
