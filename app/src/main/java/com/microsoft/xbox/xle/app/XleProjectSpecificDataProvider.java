package com.microsoft.xbox.xle.app;

import android.content.res.Configuration;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.IProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XleProjectSpecificDataProvider implements IProjectSpecificDataProvider {
    private static final String[][] displayLocales = {new String[]{"zh_SG", "zh", "CN"}, new String[]{"zh_CN", "zh", "CN"}, new String[]{"zh_HK", "zh", "TW"}, new String[]{"zh_TW", "zh", "TW"}, new String[]{"da", "da", "DK"}, new String[]{"nl", "nl", "NL"}, new String[]{"en", "en", "GB"}, new String[]{"en_US", "en", "US"}, new String[]{"fi", "fi", "FI"}, new String[]{"fr", "fr", "FR"}, new String[]{"de", "de", "DE"}, new String[]{"it", "it", "IT"}, new String[]{"ja", "ja", "JP"}, new String[]{"ko", "ko", "KR"}, new String[]{"nb", "nb", "NO"}, new String[]{"pl", "pl", "PL"}, new String[]{"pt_PT", "pt", "PT"}, new String[]{"pt", "pt", "BR"}, new String[]{"ru", "ru", "RU"}, new String[]{"es_ES", "es", "ES"}, new String[]{"es", "es", "MX"}, new String[]{"sv", "sv", "SE"}, new String[]{"tr", "tr", "TR"}};
    private static XleProjectSpecificDataProvider instance = new XleProjectSpecificDataProvider();
    private String androidId;
    private Set<String> blockFeaturedChild = new HashSet();
    private boolean gotSettings;
    private boolean isMeAdult;
    private String meXuid;
    private Set<String> musicBlocked = new HashSet();
    private String privileges;
    private Set<String> promotionalRestrictedRegions = new HashSet();
    private Set<String> purchaseBlocked = new HashSet();
    private String scdRpsTicket;
    private Hashtable<String, String> serviceLocaleMapTable = new Hashtable<>();
    private String[][] serviceLocales = {new String[]{"es_AR", "es-AR"}, new String[]{"AR", "es-AR"}, new String[]{"en_AU", "en-AU"}, new String[]{"AU", "en-AU"}, new String[]{"de_AT", "de-AT"}, new String[]{"AT", "de-AT"}, new String[]{"fr_BE", "fr-BE"}, new String[]{"nl_BE", "nl-BE"}, new String[]{"BE", "fr-BE"}, new String[]{"pt_BR", "pt-BR"}, new String[]{"BR", "pt-BR"}, new String[]{"en_CA", "en-CA"}, new String[]{"fr_CA", "fr-CA"}, new String[]{"CA", "en-CA"}, new String[]{"en_CZ", "en-CZ"}, new String[]{"CZ", "en-CZ"}, new String[]{"da_DK", "da-DK"}, new String[]{"DK", "da-DK"}, new String[]{"fi_FI", "fi-FI"}, new String[]{"FI", "fi-FI"}, new String[]{"fr_FR", "fr-FR"}, new String[]{"FR", "fr-FR"}, new String[]{"de_DE", "de-DE"}, new String[]{"DE", "de-DE"}, new String[]{"en_GR", "en-GR"}, new String[]{"GR", "en-GR"}, new String[]{"en_HK", "en-HK"}, new String[]{"zh_HK", "zh-HK"}, new String[]{"HK", "en-HK"}, new String[]{"en_HU", "en-HU"}, new String[]{"HU", "en-HU"}, new String[]{"en_IN", "en-IN"}, new String[]{"IN", "en-IN"}, new String[]{"en_GB", "en-GB"}, new String[]{"GB", "en-GB"}, new String[]{"en_IL", "en-IL"}, new String[]{"IL", "en-IL"}, new String[]{"it_IT", "it-IT"}, new String[]{"IT", "it-IT"}, new String[]{"ja_JP", "ja-JP"}, new String[]{"JP", "ja-JP"}, new String[]{"zh_CN", "zh-CN"}, new String[]{"CN", "zh-CN"}, new String[]{"es_MX", "es-MX"}, new String[]{"MX", "es-MX"}, new String[]{"es_CL", "es-CL"}, new String[]{"CL", "es-CL"}, new String[]{"es_CO", "es-CO"}, new String[]{"CO", "es-CO"}, new String[]{"nl_NL", "nl-NL"}, new String[]{"NL", "nl-NL"}, new String[]{"en_NZ", "en-NZ"}, new String[]{"NZ", "en-NZ"}, new String[]{"nb_NO", "nb-NO"}, new String[]{"NO", "nb-NO"}, new String[]{"pl_PL", "pl-PL"}, new String[]{"PL", "pl-PL"}, new String[]{"pt_PT", "pt-PT"}, new String[]{"PT", "pt-PT"}, new String[]{"ru_RU", "ru-RU"}, new String[]{"RU", "ru-RU"}, new String[]{"en_SA", "en-SA"}, new String[]{"SA", "en-SA"}, new String[]{"en_SG", "en-SG"}, new String[]{"zh_SG", "zh-SG"}, new String[]{"SG", "en-SG"}, new String[]{"en_SK", "en-SK"}, new String[]{"SK", "en-SK"}, new String[]{"en_ZA", "en-ZA"}, new String[]{"ZA", "en-ZA"}, new String[]{"ko_KR", "ko-KR"}, new String[]{"KR", "ko-KR"}, new String[]{"es_ES", "es-ES"}, new String[]{"es", "es-ES"}, new String[]{"de_CH", "de-CH"}, new String[]{"fr_CH", "fr-CH"}, new String[]{"CH", "fr-CH"}, new String[]{"zh_TW", "zh-TW"}, new String[]{"TW", "zh-TW"}, new String[]{"en_AE", "en-AE"}, new String[]{"AE", "en-AE"}, new String[]{"en_US", "en-US"}, new String[]{"US", "en-US"}, new String[]{"sv_SE", "sv-SE"}, new String[]{"SE", "sv-SE"}, new String[]{"tr_Tr", "tr-TR"}, new String[]{"Tr", "tr-TR"}, new String[]{"en_IE", "en-IE"}, new String[]{"IE", "en-IE"}};
    private Set<String> videoBlocked = new HashSet();

    private XleProjectSpecificDataProvider() {
        for (int i = 0; i < serviceLocales.length; i++) {
            serviceLocaleMapTable.put(serviceLocales[i][0], serviceLocales[i][1]);
        }
        serviceLocales = null;
    }

    public static XleProjectSpecificDataProvider getInstance() {
        return instance;
    }

    public void ensureDisplayLocale() {
        Locale mapLocale = null;
        Locale deviceLocale = Locale.getDefault();
        String localeStr = deviceLocale.toString();
        String language = deviceLocale.getLanguage();
        String region = deviceLocale.getCountry();
        int i = 0;
        while (true) {
            if (i >= displayLocales.length) {
                break;
            } else if (!displayLocales[i][0].equals(localeStr)) {
                i++;
            } else if (!displayLocales[i][1].equals(language) || !displayLocales[i][2].equals(region)) {
                mapLocale = new Locale(displayLocales[i][1], displayLocales[i][2]);
            } else {
                return;
            }
        }
        if (mapLocale == null) {
            int i2 = 0;
            while (true) {
                if (i2 >= displayLocales.length) {
                    break;
                } else if (displayLocales[i2][0].equals(language)) {
                    mapLocale = new Locale(displayLocales[i2][1], displayLocales[i2][2]);
                    break;
                } else {
                    i2++;
                }
            }
        }
        if (mapLocale != null) {
            DisplayMetrics dm = XboxTcuiSdk.getResources().getDisplayMetrics();
            Configuration conf = XboxTcuiSdk.getResources().getConfiguration();
            conf.locale = mapLocale;
            XboxTcuiSdk.getResources().updateConfiguration(conf, dm);
        }
    }

    private void addRegions(String locales, Set<String> blockSet) {
        if (!JavaUtil.isNullOrEmpty(locales)) {
            String[] list = locales.split("[|]");
            if (!XLEUtil.isNullOrEmpty(list)) {
                blockSet.clear();
                for (String region : list) {
                    if (!JavaUtil.isNullOrEmpty(region)) {
                        blockSet.add(region);
                    }
                }
            }
        }
    }

    public void processContentBlockedList(@NotNull SmartglassSettings settings) {
        addRegions(settings.VIDEO_BLOCKED, videoBlocked);
        addRegions(settings.MUSIC_BLOCKED, musicBlocked);
        addRegions(settings.PURCHASE_BLOCKED, purchaseBlocked);
        addRegions(settings.BLOCK_FEATURED_CHILD, blockFeaturedChild);
        addRegions(settings.PROMOTIONAL_CONTENT_RESTRICTED_REGIONS, promotionalRestrictedRegions);
        gotSettings = true;
    }

    public boolean gotSettings() {
        return gotSettings;
    }

    public void setIsMeAdult(boolean isAdult) {
        isMeAdult = isAdult;
    }

    public boolean isMeAdult() {
        return isMeAdult;
    }

    public int getMeMaturityLevel() {
        ProfileModel meProfile = ProfileModel.getMeProfileModel();
        if (meProfile != null) {
            return meProfile.getMaturityLevel();
        }
        return 0;
    }

    public String getRegion() {
        return Locale.getDefault().getCountry();
    }

    public boolean isMusicBlocked() {
        return true;
    }

    public boolean isVideoBlocked() {
        return true;
    }

    public boolean isPurchaseBlocked() {
        return purchaseBlocked.contains(getRegion());
    }

    public boolean isFeaturedBlocked() {
        return !isMeAdult() && blockFeaturedChild.contains(getRegion());
    }

    public boolean isPromotionalRestricted() {
        return !isMeAdult() && promotionalRestrictedRegions.contains(getRegion());
    }

    public String getXuidString() {
        return meXuid;
    }

    public void setXuidString(String xuid) {
        meXuid = xuid;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges2) {
        privileges = privileges2;
    }

    public String getSCDRpsTicket() {
        return scdRpsTicket;
    }

    public void setSCDRpsTicket(String ticket) {
        scdRpsTicket = ticket;
    }

    public String getLegalLocale() {
        return getConnectedLocale();
    }

    public String getCombinedContentRating() {
        return "";
    }

    public String getMembershipLevel() {
        if (ProfileModel.getMeProfileModel().getAccountTier() == null) {
            return "Gold";
        }
        return ProfileModel.getMeProfileModel().getAccountTier();
    }

    public boolean getAllowExplicitContent() {
        return true;
    }

    public boolean getInitializeComplete() {
        return getXuidString() != null;
    }

    public boolean getIsFreeAccount() {
        return false;
    }

    public boolean getIsXboxMusicSupported() {
        return true;
    }

    public String getWindowsLiveClientId() {
        switch (XboxLiveEnvironment.Instance().getEnvironment()) {
            case PROD:
                return "0000000048093EE3";
            case VINT:
            case DNET:
            case PARTNERNET:
                return "0000000068036303";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getVersionCheckUrl() {
        switch (XboxLiveEnvironment.Instance().getEnvironment()) {
            case PROD:
            case PARTNERNET:
                return "http://www.xbox.com/en-US/Platform/Android/XboxLIVE/sgversion";
            case VINT:
            case DNET:
                return "http://www.rtm.vint.xbox.com/en-US/Platform/Android/XboxLIVE/sgversion";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getAutoSuggestdDataSource() {
        return "bbxall2";
    }

    public void resetModels(boolean clearEverything) {
        ProfileModel.reset();
    }

    public boolean getIsForXboxOne() {
        return true;
    }

    public String getCurrentSandboxID() {
        return "PROD";
    }

    private String getDeviceLocale() {
        Locale deviceLocale = Locale.getDefault();
        String localeStr = deviceLocale.toString();
        if (serviceLocaleMapTable.containsKey(localeStr)) {
            return serviceLocaleMapTable.get(localeStr);
        }
        String region = deviceLocale.getCountry();
        if (JavaUtil.isNullOrEmpty(region) || !serviceLocaleMapTable.containsKey(region)) {
            return "en-US";
        }
        return serviceLocaleMapTable.get(region);
    }

    public boolean isDeviceLocaleKnown() {
        Locale deviceLocale = Locale.getDefault();
        if (serviceLocaleMapTable.containsKey(deviceLocale.toString())) {
            return true;
        }
        String region = deviceLocale.getCountry();
        if (JavaUtil.isNullOrEmpty(region) || !serviceLocaleMapTable.containsKey(region)) {
            return false;
        }
        return true;
    }

    public String getConnectedLocale() {
        return getDeviceLocale();
    }

    public String getConnectedLocale(boolean fromEdsCall) {
        return getConnectedLocale();
    }

    public int getVersionCode() {
        return 1;
    }

    public String getContentRestrictions() {
        String region = getRegion();
        int maturityLevel = getMeMaturityLevel();
        if (!JavaUtil.isNullOrEmpty(region) && maturityLevel != 255) {
            String jsonString = GsonUtil.toJsonString(new ContentRestrictions(region, maturityLevel, isPromotionalRestricted()));
            if (!JavaUtil.isNullOrEmpty(jsonString)) {
                return Base64.encodeToString(jsonString.getBytes(), 2);
            }
        }
        return null;
    }

    private class ContentRestrictions {
        public Data data = new Data();
        public int version = 2;

        public ContentRestrictions(String region, int ageRating, boolean restrictPromotionalContent) {
            data.geographicRegion = region;
            Data data2 = data;
            data.preferredAgeRating = ageRating;
            data2.maxAgeRating = ageRating;
            data.restrictPromotionalContent = restrictPromotionalContent;
        }

        public class Data {
            public String geographicRegion;
            public int maxAgeRating;
            public int preferredAgeRating;
            public boolean restrictPromotionalContent;

            public Data() {
            }
        }
    }
}
