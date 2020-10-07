package com.microsoft.xbox.service.model;

import android.os.Bundle;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class UpdateData {
    private final Bundle extra;
    private final boolean isFinal;
    private final UpdateType updateType;

    public UpdateData(UpdateType updateType2, boolean isFinal2) {
        updateType = updateType2;
        isFinal = isFinal2;
        extra = null;
    }

    public UpdateData(UpdateType updateType2, boolean isFinal2, Bundle extra2) {
        updateType = updateType2;
        isFinal = isFinal2;
        extra = extra2;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public boolean getIsFinal() {
        return isFinal;
    }

    public Bundle getExtra() {
        return extra;
    }
}
