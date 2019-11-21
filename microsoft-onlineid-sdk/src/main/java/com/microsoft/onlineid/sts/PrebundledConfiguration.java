package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.internal.Assets;
import com.microsoft.onlineid.internal.log.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PrebundledConfiguration {
    private static final String ConfigExtension = ".xml";
    private static final String DateFormat = "MM/dd/yyyy";
    private static final int MaxConfigAge = 30;
    private static final String TimestampExtension = ".timestamp";
    private final Context _applicationContext;
    private Date _cachedDate = null;
    private boolean _dateRead = false;
    private final String _localFilePath;

    public PrebundledConfiguration(Context applicationContext, String localFilePath) {
        this._localFilePath = localFilePath;
        this._applicationContext = applicationContext;
    }

    private boolean isDateValid() {
        Date configDate = getConfigDate();
        if (configDate != null) {
            Calendar oldestAllowedDate = Calendar.getInstance();
            oldestAllowedDate.add(5, -30);
            if (configDate.after(oldestAllowedDate.getTime())) {
                return true;
            }
        }
        return false;
    }

    public boolean exists() {
        return getConfigDate() != null;
    }

    public boolean isExpired() {
        return !isDateValid();
    }

    public String getFilePath() {
        return this._localFilePath;
    }

    public Date getConfigDate() {
        if (!this._dateRead) {
            this._dateRead = true;
            try {
                this._cachedDate = new SimpleDateFormat(DateFormat).parse(Assets.readAsset(this._applicationContext, this._localFilePath + TimestampExtension));
            } catch (FileNotFoundException e) {
            } catch (Exception e2) {
                Logger.error("Error reading timestamp of bundled configuration at: " + this._localFilePath, e2);
            }
        }
        return this._cachedDate;
    }

    public InputStream getConfigFileStream() throws IOException {
        return this._applicationContext.getAssets().open(this._localFilePath + ConfigExtension);
    }
}
