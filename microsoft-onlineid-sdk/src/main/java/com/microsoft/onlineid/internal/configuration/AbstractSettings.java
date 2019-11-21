package com.microsoft.onlineid.internal.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public abstract class AbstractSettings {
    protected final SharedPreferences _preferences;

    public static class Editor {
        protected final android.content.SharedPreferences.Editor _editor;

        protected Editor(android.content.SharedPreferences.Editor editor) {
            this._editor = editor;
        }

        public boolean commit() {
            return this._editor.commit();
        }

        protected Editor clear() {
            this._editor.clear();
            return this;
        }

        protected Editor setBoolean(ISetting<? extends Boolean> setting, boolean value) {
            this._editor.putBoolean(setting.getSettingName(), value);
            return this;
        }

        protected Editor setInt(ISetting<? extends Integer> setting, int value) {
            this._editor.putInt(setting.getSettingName(), value);
            return this;
        }

        protected Editor setString(ISetting<? extends String> setting, String value) {
            this._editor.putString(setting.getSettingName(), value);
            return this;
        }

        protected Editor setStringSet(ISetting<? extends Set<String>> setting, Set<String> value) {
            this._editor.putStringSet(setting.getSettingName(), value);
            return this;
        }
    }

    protected abstract Editor edit();

    protected AbstractSettings(Context applicationContext, String storageName) {
        this._preferences = applicationContext.getSharedPreferences(storageName, 0);
    }

    protected boolean getBoolean(ISetting<? extends Boolean> setting) {
        return this._preferences.getBoolean(setting.getSettingName(), ((Boolean) setting.getDefaultValue()).booleanValue());
    }

    protected int getInt(ISetting<? extends Integer> setting) {
        return this._preferences.getInt(setting.getSettingName(), ((Integer) setting.getDefaultValue()).intValue());
    }

    protected String getString(ISetting<? extends String> setting) {
        return this._preferences.getString(setting.getSettingName(), (String) setting.getDefaultValue());
    }

    protected Set<String> getStringSet(ISetting<? extends Set<String>> setting) {
        return this._preferences.getStringSet(setting.getSettingName(), (Set) setting.getDefaultValue());
    }
}
