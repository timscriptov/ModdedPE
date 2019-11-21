package com.microsoft.onlineid.internal.configuration;

public class Setting<T> implements ISetting<T> {
    private final T _defaultValue;
    private final String _settingName;

    public Setting(String settingName, T defaultValue) {
        this._settingName = settingName;
        this._defaultValue = defaultValue;
    }

    public String getSettingName() {
        return this._settingName;
    }

    public T getDefaultValue() {
        return this._defaultValue;
    }
}
