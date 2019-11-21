package com.microsoft.onlineid.internal.ui;

import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import java.util.HashMap;

public enum Mimetype {
    JAVASCRIPT(".js", "application/javascript"),
    PNG(DownloadProfileImageTask.UserTileExtension, "image/png"),
    SVG(".svg", "image/svg+xml"),
    CSS(".css", "text/css"),
    FONT(".woff", "application/x-font-woff");
    
    private static final HashMap<String, Mimetype> _map = null;
    private final String _mimetype;
    private final String _suffix;

    static {
        _map = new HashMap();
        Mimetype[] values = values();
        int length = values.length;
        int i;
        while (i < length) {
            Mimetype mimetype = values[i];
            _map.put(mimetype._suffix, mimetype);
            i++;
        }
    }

    private Mimetype(String suffix, String mimetype) {
        this._suffix = suffix;
        this._mimetype = mimetype;
    }

    public String toString() {
        return this._mimetype;
    }

    public static Mimetype findFromFilename(String filename) {
        if (filename == null) {
            return null;
        }
        return (Mimetype) _map.get(filename.substring(filename.lastIndexOf(46)));
    }
}
