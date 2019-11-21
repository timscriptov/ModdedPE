package com.microsoft.onlineid.internal.ui;

import com.microsoft.onlineid.internal.ui.PropertyBag.Key;

public interface IWebPropertyProvider {
    String getProperty(Key key);

    boolean handlesProperty(Key key);

    void setProperty(Key key, String str);
}
