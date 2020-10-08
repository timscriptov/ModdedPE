package com.microsoft.xbox.toolkit.ui;

import java.util.HashMap;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ActivityParameters extends HashMap<String, Object> {
    private static final String FF_INFO_TYPE = "InfoType";
    private static final String FORCE_RELOAD = "ForceReload";
    private static final String FROM_SCREEN = "FromScreen";
    private static final String ME_XUID = "MeXuid";
    private static final String ORIGINATING_PAGE = "OriginatingPage";
    private static final String PRIVILEGES = "Privileges";
    private static final String SELECTED_PROFILE = "SelectedProfile";

    public String getMeXuid() {
        return (String) get(ME_XUID);
    }

    public void putMeXuid(String xuid) {
        put(ME_XUID, xuid);
    }

    public String getSelectedProfile() {
        return (String) get(SELECTED_PROFILE);
    }

    public void putSelectedProfile(String profileXuid) {
        put(SELECTED_PROFILE, profileXuid);
    }

    public String getPrivileges() {
        return (String) get(PRIVILEGES);
    }

    public void putPrivileges(String privileges) {
        put(PRIVILEGES, privileges);
    }

    public void putFromScreen(ScreenLayout screen) {
        put(FROM_SCREEN, screen);
    }

    public boolean isForceReload() {
        if (containsKey(FORCE_RELOAD)) {
            return ((Boolean) get(FORCE_RELOAD)).booleanValue();
        }
        return false;
    }

    public void putSourcePage(String pageName) {
        put(ORIGINATING_PAGE, pageName);
    }
}
