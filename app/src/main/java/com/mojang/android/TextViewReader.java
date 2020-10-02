/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.android;

import android.widget.TextView;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextViewReader implements StringValue {
    private TextView _view;

    public TextViewReader(TextView view) {
        _view = view;
    }

    public String getStringValue() {
        return _view.getText().toString();
    }
}