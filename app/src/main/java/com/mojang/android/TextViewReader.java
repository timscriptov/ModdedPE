/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.android;

import android.widget.TextView;

public class TextViewReader implements StringValue {
    private TextView _view;

    public TextViewReader(TextView view) {
        _view = view;
    }

    public String getStringValue() {
        return _view.getText().toString();
    }
}