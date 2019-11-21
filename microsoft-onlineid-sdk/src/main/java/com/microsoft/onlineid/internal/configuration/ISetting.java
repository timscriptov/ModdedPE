package com.microsoft.onlineid.internal.configuration;

public interface ISetting<T> {
    T getDefaultValue();

    String getSettingName();
}
