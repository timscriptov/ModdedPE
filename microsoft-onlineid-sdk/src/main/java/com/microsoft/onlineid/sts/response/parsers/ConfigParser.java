package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import com.microsoft.onlineid.internal.configuration.ISetting;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Editor;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConfigParser extends BasePullParser {
    static final String CfgNamespace = "http://schemas.microsoft.com/Passport/PPCRL";
    static final String DefaultNamespace = "http://www.w3.org/2000/09/xmldsig#";
    private final Editor _editor;
    private final Map<String, Endpoint> _endpointSettings;
    private final Map<String, ISetting<Integer>> _intSettings;
    private final Map<String, ISetting<Set<String>>> _stringSetSettings;
    private final Map<String, ISetting<String>> _stringSettings = new HashMap();

    public ConfigParser(XmlPullParser underlyingParser, Editor editor) {
        int i = 0;
        super(underlyingParser, DefaultNamespace, "Signature");
        this._editor = editor;
        addSetting(this._stringSettings, ServerConfig.Version);
        this._intSettings = new HashMap();
        for (Int setting : Int.values()) {
            addSetting(this._intSettings, setting);
        }
        this._endpointSettings = new HashMap();
        Endpoint[] values = Endpoint.values();
        int length = values.length;
        while (i < length) {
            addSetting(this._endpointSettings, values[i]);
            i++;
        }
        this._stringSetSettings = new HashMap();
        addSetting(this._stringSetSettings, ServerConfig.AndroidSsoCertificates);
    }

    protected <V, T extends ISetting<V>> void addSetting(Map<String, T> map, T setting) {
        map.put(setting.getSettingName(), setting);
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("cfg:Configuration");
        NodeScope configScope = getLocation();
        while (configScope.nextStartTagNoThrow()) {
            String name = getPrefixedTagName();
            if (name.equalsIgnoreCase("cfg:Settings") || name.equalsIgnoreCase("cfg:ServiceURIs") || name.equalsIgnoreCase("cfg:ServiceURIs1")) {
                parseSettings();
            } else {
                configScope.skipElement();
            }
        }
    }

    protected void parseSettings() throws IOException, XmlPullParserException, StsParseException {
        NodeScope settingsScope = getLocation();
        while (settingsScope.nextStartTagNoThrow()) {
            String elementName = this._parser.getName();
            if (!(tryParseStringSetting(elementName) || tryParseIntSetting(elementName) || tryParseEndpointSetting(elementName) || tryParseStringSetSetting(elementName))) {
                settingsScope.skipElement();
            }
        }
    }

    protected boolean tryParseStringSetting(String elementName) throws StsParseException, XmlPullParserException, IOException {
        ISetting setting = (ISetting) this._stringSettings.get(elementName);
        if (setting == null) {
            return false;
        }
        this._editor.setString(setting, nextRequiredText());
        return true;
    }

    protected boolean tryParseIntSetting(String elementName) throws StsParseException, XmlPullParserException, IOException {
        ISetting setting = (ISetting) this._intSettings.get(elementName);
        if (setting == null) {
            return false;
        }
        this._editor.setInt(setting, TextParsers.parseInt(nextRequiredText(), elementName));
        return true;
    }

    protected boolean tryParseEndpointSetting(String elementName) throws StsParseException, XmlPullParserException, IOException {
        Endpoint setting = (Endpoint) this._endpointSettings.get(elementName);
        if (setting == null) {
            return false;
        }
        this._editor.setUrl(setting, TextParsers.parseUrl(nextRequiredText(), elementName));
        return true;
    }

    protected boolean tryParseStringSetSetting(String elementName) throws StsParseException, XmlPullParserException, IOException {
        ISetting setting = (ISetting) this._stringSetSettings.get(elementName);
        if (setting == null) {
            return false;
        }
        String value = nextRequiredText();
        if (!TextUtils.isEmpty(value)) {
            Set values = new HashSet();
            SimpleStringSplitter splitter = new SimpleStringSplitter(',');
            splitter.setString(value);
            Iterator it = splitter.iterator();
            while (it.hasNext()) {
                values.add((String) it.next());
            }
            this._editor.setStringSet(setting, values);
        }
        return true;
    }
}
