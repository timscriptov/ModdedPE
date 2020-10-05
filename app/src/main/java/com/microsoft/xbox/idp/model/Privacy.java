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
import java.util.Map;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class Privacy {

    @NotNull
    public static GsonBuilder registerAdapters(@NotNull GsonBuilder gson) {
        return gson.registerTypeAdapter(new TypeToken<Map<Key, Value>>() {
        }.getType(), new SettingsAdapter());
    }

    public enum Key {
        None,
        ShareFriendList,
        ShareGameHistory,
        CommunicateUsingTextAndVoice,
        SharePresence,
        ShareProfile,
        ShareVideoAndMusicStatus,
        CommunicateUsingVideo,
        CollectVoiceData,
        ShareXboxMusicActivity,
        ShareExerciseInfo,
        ShareIdentity,
        ShareRecordedGameSessions,
        ShareIdentityTransitively,
        CanShareIdentity
    }

    public enum Value {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked
    }

    public static class Setting {
        public Key setting;
        public Value value;
    }

    public static class Settings {
        public Map<Key, Value> settings;

        @NotNull
        public static Settings newWithMap() {
            Settings s = new Settings();
            s.settings = new HashMap();
            return s;
        }

        public boolean isSettingSet(Key key) {
            Value value;
            if (settings == null || (value = settings.get(key)) == null || value == Value.NotSet) {
                return false;
            }
            return true;
        }
    }

    private static class SettingsAdapter extends TypeAdapter<Map<Key, Value>> {
        private SettingsAdapter() {
        }

        public void write(JsonWriter out, @NotNull Map<Key, Value> value) throws IOException {
            Setting[] settings = new Setting[value.size()];
            int idx = -1;
            for (Map.Entry<Key, Value> e : value.entrySet()) {
                Setting s = new Setting();
                s.setting = e.getKey();
                s.value = e.getValue();
                idx++;
                settings[idx] = s;
            }
            new Gson().toJson(settings, Setting[].class, out);
        }

        public Map<Key, Value> read(JsonReader in) throws IOException {
            Setting[] settings = new Gson().fromJson(in, Setting[].class);
            Map<Key, Value> map = new HashMap<>();
            for (Setting s : settings) {
                if (!(s.setting == null || s.value == null)) {
                    map.put(s.setting, s.value);
                }
            }
            return map;
        }
    }
}