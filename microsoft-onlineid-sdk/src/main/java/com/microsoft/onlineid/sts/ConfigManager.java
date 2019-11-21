package com.microsoft.onlineid.sts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.configuration.Environment;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.client.ServiceFinder;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.ServerConfig.Editor;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.ConfigParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConfigManager {
    private final Context _applicationContext;
    private ServerConfig _config;
    private ServiceFinder _serviceFinder;
    private TypedStorage _storage;

    public ConfigManager(Context applicationContext) {
        this._applicationContext = applicationContext;
    }

    protected ServerConfig getConfig() {
        if (this._config == null) {
            this._config = new ServerConfig(this._applicationContext);
        }
        return this._config;
    }

    protected TypedStorage getStorage() {
        if (this._storage == null) {
            this._storage = new TypedStorage(this._applicationContext);
        }
        return this._storage;
    }

    protected ServiceFinder getServiceFinder() {
        if (this._serviceFinder == null) {
            this._serviceFinder = new ServiceFinder(this._applicationContext);
        }
        return this._serviceFinder;
    }

    protected TransportFactory getTransportFactory() {
        return new TransportFactory(this._applicationContext);
    }

    public boolean switchEnvironment(Environment newEnvironment) {
        if (newEnvironment.equals(getConfig().getEnvironment())) {
            return true;
        }
        return downloadConfiguration(newEnvironment);
    }

    public boolean isClientConfigVersionOlder(String clientConfigVersion) {
        try {
            return compareVersions(clientConfigVersion, getCurrentConfigVersion()) < 0;
        } catch (NumberFormatException ex) {
            Logger.warning("Invalid client version: " + clientConfigVersion, ex);
            return false;
        }
    }

    public boolean hasConfigBeenUpdatedRecently(long configLastDownloadedTime) {
        return (System.currentTimeMillis() - configLastDownloadedTime) / 1000 < ((long) getConfig().getInt(Int.MinSecondsBetweenConfigDownloads));
    }

    public String getCurrentConfigVersion() {
        return getConfig().getString(ServerConfig.Version);
    }

    public boolean update() {
        return downloadConfiguration(getConfig().getEnvironment());
    }

    public boolean updateIfFirstDownloadNeeded() {
        if (compareVersions(getCurrentConfigVersion(), ServerConfig.DefaultConfigVersion) == 0) {
            Environment environment = getConfig().getEnvironment();
            PrebundledConfiguration bundledConfig = loadPrebundledConfiguration(environment);
            if ((bundledConfig == null || bundledConfig.isExpired() || getServiceFinder().doesUntrustedPotentialMasterExist()) && !downloadConfiguration(environment)) {
                getConfig().markDownloadNeeded();
                return false;
            }
        }
        return true;
    }

    public boolean updateIfNeeded(String desiredVersion) {
        if (hasConfigBeenUpdatedRecently(getStorage().readConfigLastDownloadedTime())) {
            return true;
        }
        Logger.info(String.format(Locale.US, "Checking for config update from version \"%s\" to version \"%s\"", new Object[]{getCurrentConfigVersion(), desiredVersion}));
        try {
            if (compareVersions(desiredVersion, getCurrentConfigVersion()) > 0) {
                return downloadConfiguration(getConfig().getEnvironment());
            }
            return true;
        } catch (NumberFormatException ex) {
            Logger.warning("Invalid server configuration requested: " + desiredVersion, ex);
            return false;
        }
    }

    protected boolean downloadConfiguration(Environment environment) {
        Exception ex;
        boolean result = false;
        Logger.info("Downloading new PPCRL config file (" + environment.getEnvironmentName() + ").");
        Transport transport = getTransportFactory().createTransport();
        try {
            transport.openGetRequest(environment.getConfigUrl());
            int responseCode = transport.getResponseCode();
            if (responseCode == 200) {
                result = parseConfig(transport.getResponseStream(), environment);
            } else {
                Logger.error("Failed to download ppcrlconfig due to HTTP response code " + responseCode);
            }
            transport.closeConnection();
        } catch (Exception e) {
            ex = e;
            try {
                Logger.error("Failed to download ppcrlconfig.", ex);
                ClientAnalytics.get().logException(ex);
                if (result) {
                    Logger.error("Failed to update ppcrlconfig (parseConfig() returned false).");
                } else {
                    Logger.info("Successfully downloaded ppcrlconfig version: " + getCurrentConfigVersion());
                    getStorage().writeConfigLastDownloadedTime();
                }
                return result;
            } finally {
                transport.closeConnection();
            }
        } catch (Exception e2) {
            ex = e2;
            Logger.error("Failed to download ppcrlconfig.", ex);
            ClientAnalytics.get().logException(ex);
            if (result) {
                Logger.info("Successfully downloaded ppcrlconfig version: " + getCurrentConfigVersion());
                getStorage().writeConfigLastDownloadedTime();
            } else {
                Logger.error("Failed to update ppcrlconfig (parseConfig() returned false).");
            }
            return result;
        } catch (Exception e22) {
            ex = e22;
            Logger.error("Failed to download ppcrlconfig.", ex);
            ClientAnalytics.get().logException(ex);
            if (result) {
                Logger.error("Failed to update ppcrlconfig (parseConfig() returned false).");
            } else {
                Logger.info("Successfully downloaded ppcrlconfig version: " + getCurrentConfigVersion());
                getStorage().writeConfigLastDownloadedTime();
            }
            return result;
        } catch (Exception e222) {
            ex = e222;
            Logger.error("Failed to download ppcrlconfig.", ex);
            ClientAnalytics.get().logException(ex);
            if (result) {
                Logger.info("Successfully downloaded ppcrlconfig version: " + getCurrentConfigVersion());
                getStorage().writeConfigLastDownloadedTime();
            } else {
                Logger.error("Failed to update ppcrlconfig (parseConfig() returned false).");
            }
            return result;
        }
        if (result) {
            Logger.info("Successfully downloaded ppcrlconfig version: " + getCurrentConfigVersion());
            getStorage().writeConfigLastDownloadedTime();
        } else {
            Logger.error("Failed to update ppcrlconfig (parseConfig() returned false).");
        }
        return result;
    }

    protected PrebundledConfiguration loadPrebundledConfiguration(Environment environment) {
        PrebundledConfiguration configFile = findNewestPrebundledConfiguration(environment);
        if (configFile == null) {
            return null;
        }
        InputStream stream = null;
        try {
            stream = configFile.getConfigFileStream();
            if (parseConfig(stream, environment)) {
                Logger.info("Succesfully loaded prebundled config file " + configFile.getFilePath() + ".xml (" + getCurrentConfigVersion() + ", " + environment.getEnvironmentName() + ", " + configFile.getConfigDate() + ").");
                if (configFile.isExpired()) {
                    Logger.info("Prebundled config file potentially expired (" + configFile.getConfigDate() + "), attempting download.");
                }
                if (stream == null) {
                    return configFile;
                }
                try {
                    stream.close();
                    return configFile;
                } catch (IOException e) {
                    return configFile;
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e2) {
                }
            }
            return null;
        } catch (Exception e3) {
            Logger.error("Failed to load prebundled configuration.", e3);
            ClientAnalytics.get().logException(e3);
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e4) {
                }
            }
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e5) {
                }
            }
        }
    }

    protected PrebundledConfiguration findNewestPrebundledConfiguration(Environment environment) {
        List<PrebundledConfiguration> possibleConfigurationFiles = getPossiblePrebundledConfigurationFiles(environment);
        Iterator<PrebundledConfiguration> iterator = possibleConfigurationFiles.iterator();
        while (iterator.hasNext()) {
            if (!((PrebundledConfiguration) iterator.next()).exists()) {
                iterator.remove();
            }
        }
        if (possibleConfigurationFiles.isEmpty()) {
            Logger.warning("No prebundled configuration file was found.");
            return null;
        }
        Collections.sort(possibleConfigurationFiles, new Comparator<PrebundledConfiguration>() {
            public int compare(PrebundledConfiguration lhs, PrebundledConfiguration rhs) {
                return lhs.getConfigDate().compareTo(rhs.getConfigDate()) * -1;
            }
        });
        return (PrebundledConfiguration) possibleConfigurationFiles.get(0);
    }

    protected List<PrebundledConfiguration> getPossiblePrebundledConfigurationFiles(Environment environment) {
        String environmentName = environment.getEnvironmentName().toLowerCase(Locale.US);
        return new ArrayList(Arrays.asList(new PrebundledConfiguration[]{new PrebundledConfiguration(this._applicationContext, "msa-sdk/config/ppcrlconfig600-" + environmentName), new PrebundledConfiguration(this._applicationContext, "msa/config/ppcrlconfig600-" + environmentName)}));
    }

    static long compareVersions(String left, String right) {
        int diff = 0;
        String[] leftTokens = TextUtils.isEmpty(left) ? new String[0] : left.split("\\.");
        String[] rightTokens = TextUtils.isEmpty(right) ? new String[0] : right.split("\\.");
        int index = 0;
        while (true) {
            if (index >= leftTokens.length && index >= rightTokens.length) {
                break;
            }
            int leftValue = 0;
            int rightValue = 0;
            if (index < leftTokens.length) {
                leftValue = Integer.parseInt(leftTokens[index]);
            }
            if (index < rightTokens.length) {
                rightValue = Integer.parseInt(rightTokens[index]);
            }
            diff = leftValue - rightValue;
            if (diff != 0) {
                break;
            }
            index++;
        }
        return (long) diff;
    }

    protected boolean parseConfig(InputStream stream, Environment environment) throws IOException, XmlPullParserException, StsParseException {
        boolean result = false;
        try {
            XmlPullParser rawParser = Xml.newPullParser();
            rawParser.setInput(stream, null);
            Integer cloudPinLength = getConfig().getNgcCloudPinLength();
            Editor editor = getConfig().edit();
            editor.clear();
            editor.setString(ServerConfig.EnvironmentName, environment.getEnvironmentName());
            editor.setUrl(Endpoint.Configuration, environment.getConfigUrl());
            editor.setInt(ServerConfig.NgcCloudPinLength, cloudPinLength.intValue());
            new ConfigParser(rawParser, editor).parse();
            result = editor.commit();
            return result;
        } finally {
            stream.close();
        }
    }
}
