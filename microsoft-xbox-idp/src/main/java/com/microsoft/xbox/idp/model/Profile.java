package com.microsoft.xbox.idp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Profile {

    public static final class GamerpicChangeRequest {
        public UserSetting userSetting;

        public GamerpicChangeRequest(String newUrl) {
            this.userSetting = new UserSetting("PublicGamerpic", newUrl);
        }
    }

    public static final class GamerpicChoiceList {
        public List<GamerpicListEntry> gamerpics;
    }

    public static final class GamerpicListEntry {
        public String id;
    }

    public static final class GamerpicUpdateResponse {
    }

    public static final class Response {
        public User[] profileUsers;
    }

    public static final class Setting {
        public SettingId id;
        public String value;
    }

    public enum SettingId {
        AppDisplayName,
        GameDisplayName,
        Gamertag,
        RealName,
        FirstName,
        LastName,
        AppDisplayPicRaw,
        GameDisplayPicRaw,
        AccountTier,
        TenureLevel,
        Gamerscore,
        PreferredColor,
        Watermarks,
        XboxOneRep,
        Background,
        PublicGamerpicType,
        ShowUserAsAvatar,
        TileTransparency
    }

    private static class SettingsAdapter extends TypeAdapter<Map<SettingId, String>> {
        private SettingsAdapter() {
        }

        public void write(JsonWriter out, Map<SettingId, String> value) throws IOException {
            Setting[] settings = new Setting[value.size()];
            int i = -1;
            for (Entry<SettingId, String> e : value.entrySet()) {
                Setting s = new Setting();
                s.id = (SettingId) e.getKey();
                s.value = (String) e.getValue();
                i++;
                settings[i] = s;
            }
            new Gson().toJson((Object) settings, (Type) Setting[].class, out);
        }

        public Map<SettingId, String> read(JsonReader in) throws IOException {
            Setting[] settings = (Setting[]) new Gson().fromJson(in, (Type) Setting[].class);
            Map<SettingId, String> map = new HashMap<>();
            for (Setting s : settings) {
                map.put(s.id, s.value);
            }
            return map;
        }
    }

    public static final class User {
        public String id;
        public boolean isSponsoredUser;
        public Map<SettingId, String> settings;
    }

    public static final class UserSetting {
        public String id;
        public String value;

        public UserSetting(String idParam, String valueParam) {
            this.id = idParam;
            this.value = valueParam;
        }
    }

    public static GsonBuilder registerAdapters(GsonBuilder gson) {
        return gson.registerTypeAdapter(new TypeToken<Map<SettingId, String>>() {
        }.getType(), new SettingsAdapter());
    }
}
