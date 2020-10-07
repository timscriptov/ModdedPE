package com.microsoft.xbox.toolkit;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProjectSpecificDataProvider implements IProjectSpecificDataProvider {
    private static ProjectSpecificDataProvider instance = new ProjectSpecificDataProvider();
    private IProjectSpecificDataProvider provider;

    public static ProjectSpecificDataProvider getInstance() {
        return instance;
    }

    public void setProvider(IProjectSpecificDataProvider provider2) {
        provider = provider2;
    }

    public String getLegalLocale() {
        checkProvider();
        if (provider != null) {
            return provider.getLegalLocale();
        }
        return null;
    }

    public String getCombinedContentRating() {
        checkProvider();
        if (provider != null) {
            return provider.getCombinedContentRating();
        }
        return null;
    }

    public String getMembershipLevel() {
        checkProvider();
        if (provider != null) {
            return provider.getMembershipLevel();
        }
        return null;
    }

    public String getXuidString() {
        checkProvider();
        if (provider != null) {
            return provider.getXuidString();
        }
        return null;
    }

    public void setXuidString(String xuid) {
        checkProvider();
        if (provider != null) {
            provider.setXuidString(xuid);
        }
    }

    public String getSCDRpsTicket() {
        checkProvider();
        if (provider != null) {
            return provider.getSCDRpsTicket();
        }
        return null;
    }

    public void setSCDRpsTicket(String rpsTicket) {
        checkProvider();
        if (provider != null) {
            provider.setSCDRpsTicket(rpsTicket);
        }
    }

    public String getPrivileges() {
        checkProvider();
        if (provider != null) {
            return provider.getPrivileges();
        }
        return "";
    }

    public void setPrivileges(String privileges) {
        checkProvider();
        if (provider != null) {
            provider.setPrivileges(privileges);
        }
    }

    public boolean getAllowExplicitContent() {
        checkProvider();
        if (provider != null) {
            return provider.getAllowExplicitContent();
        }
        return false;
    }

    public String getAutoSuggestdDataSource() {
        checkProvider();
        if (provider != null) {
            return provider.getAutoSuggestdDataSource();
        }
        return null;
    }

    public boolean getInitializeComplete() {
        checkProvider();
        if (provider != null) {
            return provider.getInitializeComplete();
        }
        return false;
    }

    public boolean getIsFreeAccount() {
        checkProvider();
        if (provider != null) {
            return provider.getIsFreeAccount();
        }
        return true;
    }

    private void checkProvider() {
    }

    public boolean getIsXboxMusicSupported() {
        checkProvider();
        if (provider != null) {
            return provider.getIsXboxMusicSupported();
        }
        return false;
    }

    public String getWindowsLiveClientId() {
        checkProvider();
        if (provider != null) {
            return provider.getWindowsLiveClientId();
        }
        return null;
    }

    public String getVersionCheckUrl() {
        checkProvider();
        if (provider != null) {
            return provider.getVersionCheckUrl();
        }
        return null;
    }

    public void resetModels(boolean clearEverything) {
        checkProvider();
        if (provider != null) {
            provider.resetModels(clearEverything);
        }
    }

    public boolean getIsForXboxOne() {
        checkProvider();
        if (provider != null) {
            return provider.getIsForXboxOne();
        }
        return false;
    }

    public String getCurrentSandboxID() {
        checkProvider();
        if (provider != null) {
            return provider.getCurrentSandboxID();
        }
        return null;
    }

    public boolean isDeviceLocaleKnown() {
        checkProvider();
        if (provider != null) {
            return provider.isDeviceLocaleKnown();
        }
        return true;
    }

    public String getConnectedLocale() {
        checkProvider();
        if (provider != null) {
            return provider.getConnectedLocale();
        }
        return null;
    }

    public String getConnectedLocale(boolean fromEdsCall) {
        checkProvider();
        if (provider != null) {
            return provider.getConnectedLocale(fromEdsCall);
        }
        return null;
    }

    public int getVersionCode() {
        checkProvider();
        if (provider != null) {
            return provider.getVersionCode();
        }
        return 0;
    }

    public String getContentRestrictions() {
        checkProvider();
        if (provider != null) {
            return provider.getContentRestrictions();
        }
        return null;
    }

    public String getRegion() {
        checkProvider();
        if (provider != null) {
            return provider.getRegion();
        }
        return null;
    }
}
