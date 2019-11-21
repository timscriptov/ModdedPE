package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class BasePullParser {
    private boolean _parseCalled = false;
    protected final XmlPullParser _parser;
    protected final String _parserTag;
    protected final String _parserTagNamespace;
    private final NodeScope _scope;

    protected abstract void onParse() throws XmlPullParserException, IOException, StsParseException;

    public BasePullParser(XmlPullParser underlyingParser, String expectedNamespace, String expectedTag) {
        this._parser = underlyingParser;
        this._parserTag = expectedTag;
        this._parserTagNamespace = expectedNamespace;
        this._scope = getLocation();
    }

    public final void parse() throws IOException, StsParseException {
        try {
            if (this._parseCalled) {
                throw new IllegalStateException("Parse has already been called.");
            }
            this._parseCalled = true;
            if (this._scope.getDepth() == 0) {
                this._parser.require(0, null, null);
                this._parser.next();
            }
            this._parser.require(2, this._parserTagNamespace, this._parserTag);
            onParse();
            finish();
        } catch (XmlPullParserException ex) {
            throw new StsParseException("XML was either invalid or failed to parse.", ex, new Object[0]);
        }
    }

    protected boolean nextStartTagNoThrow() throws XmlPullParserException, IOException {
        return this._scope.nextStartTagNoThrow();
    }

    protected boolean nextStartTagNoThrow(String prefixedTag) throws XmlPullParserException, IOException {
        return this._scope.nextStartTagNoThrow(prefixedTag);
    }

    protected void nextStartTag(String prefixedTag) throws XmlPullParserException, IOException, StsParseException {
        this._scope.nextStartTag(prefixedTag);
    }

    protected String nextRequiredText() throws XmlPullParserException, IOException, StsParseException {
        return this._scope.nextRequiredText();
    }

    protected void skipElement() throws XmlPullParserException, IOException {
        this._scope.skipElement();
    }

    protected String readRawOuterXml() throws XmlPullParserException, IOException {
        StringBuilder contents = new StringBuilder();
        NodeScope currentScope = getLocation();
        while (currentScope.hasMore()) {
            switch (this._parser.getEventType()) {
                case ApiResult.ResultUINeeded /*2*/:
                    int i;
                    contents.append('<').append(getPrefixedTagName());
                    int nsStart = this._parser.getNamespaceCount(this._parser.getDepth() - 1);
                    int nsEnd = this._parser.getNamespaceCount(this._parser.getDepth());
                    for (i = nsStart; i < nsEnd; i++) {
                        contents.append(' ').append(getPrefixedNamespaceName(i)).append("=\"").append(this._parser.getNamespaceUri(i)).append('\"');
                    }
                    for (i = 0; i < this._parser.getAttributeCount(); i++) {
                        contents.append(' ').append(getPrefixedAttributeName(i)).append("=\"").append(this._parser.getAttributeValue(i)).append('\"');
                    }
                    contents.append('>');
                    break;
                case 3:
                    contents.append("</").append(getPrefixedTagName(this._parser)).append('>');
                    break;
                case 4:
                    contents.append(this._parser.getText());
                    break;
                default:
                    break;
            }
            this._parser.next();
        }
        contents.append("</").append(getPrefixedTagName(this._parser)).append('>');
        return contents.toString();
    }

    protected String getPrefixedTagName() throws XmlPullParserException {
        return getPrefixedTagName(this._parser);
    }

    protected static String getPrefixedTagName(XmlPullParser parser) throws XmlPullParserException {
        int eventType = parser.getEventType();
        if (eventType == 2 || eventType == 3) {
            String prefix = parser.getPrefix();
            String name = parser.getName();
            return TextUtils.isEmpty(prefix) ? name : prefix + ":" + name;
        } else {
            throw new XmlPullParserException("Tag name should only be retrieved on a start or end tag.");
        }
    }

    private String getPrefixedNamespaceName(int pos) throws XmlPullParserException {
        int eventType = this._parser.getEventType();
        if (eventType == 2 || eventType == 3) {
            int depth = this._parser.getDepth();
            if (pos < this._parser.getNamespaceCount(depth - 1) || pos >= this._parser.getNamespaceCount(depth)) {
                throw new XmlPullParserException("Invalid namespace location.");
            }
            String prefix = this._parser.getNamespacePrefix(pos);
            return TextUtils.isEmpty(prefix) ? "xmlns" : "xmlns:" + prefix;
        } else {
            throw new XmlPullParserException("Namespace name should only be retrieved on a start or end tag.");
        }
    }

    private String getPrefixedAttributeName(int index) throws XmlPullParserException {
        int eventType = this._parser.getEventType();
        if (eventType != 2 && eventType != 3) {
            throw new XmlPullParserException("Attribute should only be retrieved on a start or end tag.");
        } else if (index < 0 || index >= this._parser.getAttributeCount()) {
            throw new XmlPullParserException("Invalid attribute location.");
        } else {
            String prefix = this._parser.getAttributePrefix(index);
            String name = this._parser.getAttributeName(index);
            return TextUtils.isEmpty(prefix) ? name : prefix + ":" + name;
        }
    }

    private void finish() throws XmlPullParserException, IOException {
        this._scope.finish();
    }

    protected boolean hasMore() throws XmlPullParserException {
        return this._scope.hasMore();
    }

    protected NodeScope getLocation() {
        return new NodeScope(this._parser);
    }

    protected void verifyParseCalled() {
        if (!this._parseCalled) {
            throw new IllegalStateException("Cannot call this method without calling parse.");
        }
    }
}
