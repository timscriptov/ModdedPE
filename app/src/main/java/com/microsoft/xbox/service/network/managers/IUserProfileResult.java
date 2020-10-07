package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.sls.UserProfileSetting;
import com.microsoft.xbox.toolkit.GsonUtil;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface IUserProfileResult {

    public static class Settings {
        public String id;
        public String value;
    }

    public static class ProfileUser {
        private static final long FORCE_MATURITY_LEVEL_UPDATE_TIME = 10800000;
        public boolean canViewTVAdultContent;
        public ProfilePreferredColor colors;
        public String id;
        public ArrayList<Settings> settings;
        private int maturityLevel;
        private int[] privileges;
        private long updateMaturityLevelTimer = -1;

        private void fetchMaturityLevel() {
            try {
                FamilySettings familySettings = ServiceManagerFactory.getInstance().getSLSServiceManager().getFamilySettings(id);
                if (familySettings != null && familySettings.familyUsers != null) {
                    int i = 0;
                    while (true) {
                        if (i >= familySettings.familyUsers.size()) {
                            break;
                        } else if (familySettings.familyUsers.get(i).xuid.equalsIgnoreCase(id)) {
                            canViewTVAdultContent = familySettings.familyUsers.get(i).canViewTVAdultContent;
                            maturityLevel = familySettings.familyUsers.get(i).maturityLevel;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
            updateMaturityLevelTimer = System.currentTimeMillis();
        }

        public int getMaturityLevel() {
            if (updateMaturityLevelTimer < 0 || System.currentTimeMillis() - updateMaturityLevelTimer > FORCE_MATURITY_LEVEL_UPDATE_TIME) {
                fetchMaturityLevel();
            }
            return maturityLevel;
        }

        public void setmaturityLevel(int maturityLevel2) {
            maturityLevel = maturityLevel2;
            updateMaturityLevelTimer = System.currentTimeMillis();
        }

        public int[] getPrivileges() {
            return privileges;
        }

        public void setPrivilieges(int[] privileges2) {
            privileges = privileges2;
        }

        public String getSettingValue(UserProfileSetting settingId) {
            if (settings != null) {
                Iterator<Settings> it = settings.iterator();
                while (it.hasNext()) {
                    Settings setting = it.next();
                    if (setting.id != null && setting.id.equals(settingId.toString())) {
                        return setting.value;
                    }
                }
            }
            return null;
        }
    }

    public static class UserProfileResult {
        public ArrayList<ProfileUser> profileUsers;

        public static UserProfileResult deserialize(String input) {
            return GsonUtil.deserializeJson(input, UserProfileResult.class);
        }
    }
}
