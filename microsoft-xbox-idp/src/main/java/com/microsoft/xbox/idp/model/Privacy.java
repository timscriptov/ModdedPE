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
import java.util.Map;
import java.util.Map.Entry;

public final class Privacy {

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

    public static class Setting {
        public Key setting;
        public Value value;
    }

    public static class Settings {
        public Map<Key, Value> settings;

        public static Settings newWithMap() {
            Settings s = new Settings();
            s.settings = new HashMap();
            return s;
        }

        public boolean isSettingSet(Key key) {
            if (this.settings == null) {
                return false;
            }
            Value value = (Value) this.settings.get(key);
            if (value == null || value == Value.NotSet) {
                return false;
            }
            return true;
        }
    }

    private static class SettingsAdapter extends TypeAdapter<Map<Key, Value>> {
        private SettingsAdapter() {
        }

        public void write(JsonWriter out, Map<Key, Value> value) throws IOException {
            Setting[] settings = new Setting[value.size()];
            int idx = -1;
            for (Entry<Key, Value> e : value.entrySet()) {
                Setting s = new Setting();
                s.setting = (Key) e.getKey();
                s.value = (Value) e.getValue();
                idx++;
                settings[idx] = s;
            }
            new Gson().toJson((Object) settings, (Type) Setting[].class, out);
        }

        public Map<Key, Value> read(JsonReader in) throws IOException {
            Setting[] settings = (Setting[]) new Gson().fromJson(in, (Type) Setting[].class);
            Map<Key, Value> map = new HashMap<>();
            for (Setting s : settings) {
                if (!(s.setting == null || s.value == null)) {
                    map.put(s.setting, s.value);
                }
            }
            return map;
        }
    }

    public enum Value {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked
    }

    public static GsonBuilder registerAdapters(GsonBuilder gson) {
        return gson.registerTypeAdapter(new TypeToken<Map<Key, Value>>() {
        }.getType(), new SettingsAdapter());
    }
}
