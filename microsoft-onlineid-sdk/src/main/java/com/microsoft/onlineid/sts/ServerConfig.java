package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.internal.configuration.AbstractSettings;
import com.microsoft.onlineid.internal.configuration.Environment;
import com.microsoft.onlineid.internal.configuration.ISetting;
import com.microsoft.onlineid.internal.configuration.Setting;
import com.microsoft.onlineid.internal.configuration.Settings;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServerConfig extends AbstractSettings {
    public static Setting<Set<String>> AndroidSsoCertificates = new Setting("AndroidSsoCerts", new HashSet(Arrays.asList(new String[]{"sX6CAbEo4edMwCNRCrfqA6wn3eUNMtgQ6hV3dY8cwJg=", "g2b69yfcSDF6LzoMN/oSfz81YZTPuy9LYo7H5qGnXA8=", "uSUTbz6nwKGVFpChqzE5ENqB9AmUqFNC7GIoiPEocFE=", "oHlCFSeKVn6IevbN4BWl6IQU72QPfas4VaPneWWL53g=", "fVOTUco5wnynBkCeWptrBi25v43D2MqmE3Bnrn9otec=", "KEg2GpweMt8dPi7Wp7nmelJc+KE7Fk+ABslHlXj3Rt4=", "7r0PFuYpr4uDgb/t/dZJYF/pD3Y/XLe6Rz657vlNmvE=", "Mb5ACW+THNfxHV4mLSssQ3xEOF+07LwQE9ladDWBb5w=", "6EPuPaEZXWr7icqjznQnsI/AH9h4ok+lbpYsNccdXnA=", "rQZmwqojBROk1Pc/okKkWGzY7WcmodDdVCUHcv2blgU="})));
    public static final String DefaultConfigVersion = "1";
    private static String Domain = (Settings.isDebugBuild() ? "live-int.com" : "live.com");
    public static Setting<String> EnvironmentName = new Setting("environment", Settings.isDebugBuild() ? "int" : "production");
    public static Setting<Integer> NgcCloudPinLength = new Setting("cloud_pin_length", Integer.valueOf(4));
    static final String StorageName = "ServerConfig";
    public static Setting<String> Version = new Setting("ConfigVersion", DefaultConfigVersion);

    public static class Editor extends com.microsoft.onlineid.internal.configuration.AbstractSettings.Editor {
        private Editor(android.content.SharedPreferences.Editor editor) {
            super(editor);
        }

        public Editor clear() {
            super.clear();
            return this;
        }

        public Editor setInt(ISetting<? extends Integer> setting, int value) {
            super.setInt(setting, value);
            return this;
        }

        public Editor setString(ISetting<? extends String> setting, String value) {
            super.setString(setting, value);
            return this;
        }

        public Editor setStringSet(ISetting<? extends Set<String>> setting, Set<String> value) {
            super.setStringSet(setting, value);
            return this;
        }

        public Editor setBoolean(ISetting<? extends Boolean> setting, boolean value) {
            super.setBoolean(setting, value);
            return this;
        }

        public Editor setUrl(Endpoint setting, URL value) {
            this._editor.putString(setting.getSettingName(), value.toExternalForm());
            return this;
        }
    }

    public enum Endpoint implements ISetting<URL> {
        ;
        
        private final URL _defaultValue;
        private final String _settingName;

        static {
            String toExternalForm;
            String str = "Configuration";
            String str2 = "ConfigUrl";
            if (Settings.isDebugBuild()) {
                toExternalForm = KnownEnvironment.Int.getEnvironment().getConfigUrl().toExternalForm();
            } else {
                toExternalForm = KnownEnvironment.Production.getEnvironment().getConfigUrl().toExternalForm();
            }
            Configuration = new Endpoint(str, 0, str2, toExternalForm);
            Sts = new Endpoint("Sts", 1, "WLIDSTS_WCF", "https://login." + ServerConfig.Domain + ":443/RST2.srf");
            DeviceProvision = new Endpoint("DeviceProvision", 2, "DeviceAddService", "https://login." + ServerConfig.Domain + "/ppsecure/deviceaddcredential.srf");
            ManageApprover = new Endpoint("ManageApprover", 3, "ManageApprover", "https://login." + ServerConfig.Domain + "/ManageApprover.srf");
            ManageLoginKeys = new Endpoint("ManageLoginKeys", 4, "ManageLoginKeys", "https://login." + ServerConfig.Domain + "/ManageLoginKeys.srf");
            ListSessions = new Endpoint("ListSessions", 5, "ListSessions", "https://login." + ServerConfig.Domain + "/ListSessions.srf");
            ApproveSession = new Endpoint("ApproveSession", 6, "ApproveSession", "https://login." + ServerConfig.Domain + "/ApproveSession.srf");
            ConnectMsa = new Endpoint("ConnectMsa", 7, "CPConnect", "https://login." + ServerConfig.Domain + "/ppsecure/InlineConnect.srf?id=80601");
            ConnectPartner = new Endpoint("ConnectPartner", 8, "CompleteAccountConnect", "https://login." + ServerConfig.Domain + "/ppsecure/InlineConnect.srf?id=80604");
            SignInMsa = new Endpoint("SignInMsa", 9, "CPSignInAuthUp", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80601");
            SignInPartner = new Endpoint("SignInPartner", 10, "CompleteAccountSignIn", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80604");
            SignupMsa = new Endpoint("SignupMsa", 11, "SignupMsa", "https://signup." + ServerConfig.Domain + "/signup?id=80601");
            SignupPartner = new Endpoint("SignupPartner", 12, "SignupPartner", "https://signup." + ServerConfig.Domain + "/signup?id=80604");
            SignupWReplyMsa = new Endpoint("SignupWReplyMsa", 13, "SignupWReplyMsa", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80601&actionid=7");
            SignupWReplyPartner = new Endpoint("SignupWReplyPartner", 14, "SignupWReplyPartner", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80604&actionid=7");
            Refresh = new Endpoint("Refresh", 15, "URL_AccountSettings", "https://account." + ServerConfig.Domain + "/");
            RemoteConnect = new Endpoint("RemoteConnect", 16, "RemoteConnect", "https://login." + ServerConfig.Domain + "/RemoteConnectClientAuth.srf");
            $VALUES = new Endpoint[]{Configuration, Sts, DeviceProvision, ManageApprover, ManageLoginKeys, ListSessions, ApproveSession, ConnectMsa, ConnectPartner, SignInMsa, SignInPartner, SignupMsa, SignupPartner, SignupWReplyMsa, SignupWReplyPartner, Refresh, RemoteConnect};
        }

        private Endpoint(String settingName, String defaultValue) {
            this._settingName = settingName;
            try {
                this._defaultValue = new URL(defaultValue);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Default value for ServerConfig.Url with name '" + settingName + "' is not a valid URL.");
            }
        }

        public String getSettingName() {
            return this._settingName;
        }

        public URL getDefaultValue() {
            return this._defaultValue;
        }
    }

    public enum Int implements ISetting<Integer> {
        ConnectTimeout("ConnectTimeout", 10000),
        SendTimeout("SendTimeout", 30000),
        ReceiveTimeout("ReceiveTimeout", 30000),
        BackupSlaveCount("BackupSlaveCount", 3),
        MaxSecondsBetweenBackups("MaxSecondsBetweenBackups", 259200),
        MinSecondsBetweenConfigDownloads("MinSecondsBetweenConfigDownloads", 28800),
        MaxTriesForSsoRequestToSingleService("MaxTriesForSsoRequestToSingleService", 2),
        MaxTriesForSsoRequestWithFallback("MaxTriesForSsoRequestWithFallback", 4);
        
        private final Integer _defaultValue;
        private final String _settingName;

        private Int(String settingName, int defaultValue) {
            this._settingName = settingName;
            this._defaultValue = Integer.valueOf(defaultValue);
        }

        public String getSettingName() {
            return this._settingName;
        }

        public Integer getDefaultValue() {
            return this._defaultValue;
        }
    }

    public enum KnownEnvironment {
        Production("production", "https://go.microsoft.com/fwlink/?LinkId=398559"),
        Int("int", "https://go.microsoft.com/fwlink/?LinkId=398560");
        
        private final Environment _environment;

        private KnownEnvironment(String name, String configUrl) {
            try {
                this._environment = new Environment(name, new URL(configUrl));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid known environment URL: " + configUrl);
            }
        }

        public Environment getEnvironment() {
            return this._environment;
        }
    }

    public ServerConfig(Context applicationContext) {
        super(applicationContext, StorageName);
    }

    public Editor edit() {
        return new Editor(this._preferences.edit());
    }

    public int getInt(ISetting<? extends Integer> setting) {
        return super.getInt(setting);
    }

    public String getString(ISetting<? extends String> setting) {
        return super.getString(setting);
    }

    public Set<String> getStringSet(ISetting<? extends Set<String>> setting) {
        return super.getStringSet(setting);
    }

    protected boolean getBoolean(ISetting<? extends Boolean> setting) {
        return super.getBoolean(setting);
    }

    public URL getUrl(Endpoint setting) {
        try {
            String value = this._preferences.getString(setting.getSettingName(), null);
            return value != null ? new URL(value) : setting.getDefaultValue();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Stored URL for setting " + setting.getSettingName() + " is invalid.", ex);
        }
    }

    public Environment getEnvironment() {
        return new Environment(getString(EnvironmentName), getUrl(Endpoint.Configuration));
    }

    public Environment getDefaultEnvironment() {
        return new Environment((String) EnvironmentName.getDefaultValue(), Endpoint.Configuration.getDefaultValue());
    }

    public Integer getNgcCloudPinLength() {
        return Integer.valueOf(getInt(NgcCloudPinLength));
    }

    public boolean markDownloadNeeded() {
        return edit().setString(Version, DefaultConfigVersion).commit();
    }
}
