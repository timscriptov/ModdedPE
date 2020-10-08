package com.microsoft.xbox.toolkit.ui;

import java.net.URI;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEURIArg {
    private final int errorResourceId;
    private final int loadingResourceId;
    private final URI uri;

    public XLEURIArg(URI uri2, int loadingResourceId2, int errorResourceId2) {
        uri = uri2;
        loadingResourceId = loadingResourceId2;
        errorResourceId = errorResourceId2;
    }

    public XLEURIArg(URI uri2) {
        this(uri2, -1, -1);
    }

    public URI getUri() {
        return uri;
    }

    public int getLoadingResourceId() {
        return loadingResourceId;
    }

    public int getErrorResourceId() {
        return errorResourceId;
    }

    public TextureBindingOption getTextureBindingOption() {
        return new TextureBindingOption(-1, -1, loadingResourceId, errorResourceId, false);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof XLEURIArg)) {
            return false;
        }
        XLEURIArg other = (XLEURIArg) o;
        if (loadingResourceId != other.loadingResourceId || errorResourceId != other.errorResourceId) {
            return false;
        }
        if (uri == other.uri || (uri != null && uri.equals(other.uri))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hash = ((loadingResourceId + 13) * 17) + errorResourceId;
        if (uri != null) {
            return (hash * 23) + uri.hashCode();
        }
        return hash;
    }
}
