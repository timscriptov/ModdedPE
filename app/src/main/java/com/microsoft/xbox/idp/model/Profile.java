package com.microsoft.xbox.idp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class Profile {

    @NotNull
    public static GsonBuilder registerAdapters(@NotNull GsonBuilder gson) {
        return gson.registerTypeAdapter(new TypeToken<Map<SettingId, String>>() {
        }.getType(), new SettingsAdapter());
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

    public static final class User {
        public String id;
        public boolean isSponsoredUser;
        public Map<SettingId, String> settings;
    }

    public static final class GamerpicChangeRequest {
        public UserSetting userSetting;

        public GamerpicChangeRequest(String newUrl) {
            userSetting = new UserSetting("PublicGamerpic", newUrl);
        }
    }

    public static final class UserSetting {
        public String id;
        public String value;

        public UserSetting(String idParam, String valueParam) {
            id = idParam;
            value = valueParam;
        }
    }

    private static class SettingsAdapter extends TypeAdapter<Map<SettingId, String>> {
        private SettingsAdapter() {
        }

        public void write(JsonWriter out, @NotNull Map<SettingId, String> value) throws IOException {
            Setting[] settings = new Setting[value.size()];
            int i = -1;
            for (Map.Entry<SettingId, String> e : value.entrySet()) {
                Setting s = new Setting();
                s.id = e.getKey();
                s.value = e.getValue();
                i++;
                settings[i] = s;
            }
            new Gson().toJson(settings, Setting[].class, out);
        }

        public Map<SettingId, String> read(JsonReader in) throws IOException {
            Setting[] settings = new Gson().fromJson(in, Setting[].class);
            Map<SettingId, String> map = new HashMap<>();
            for (Setting s : settings) {
                map.put(s.id, s.value);
            }
            return map;
        }
    }
}
