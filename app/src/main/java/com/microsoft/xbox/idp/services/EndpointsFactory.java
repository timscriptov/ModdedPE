package com.microsoft.xbox.idp.services;

import org.jetbrains.annotations.Nullable;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class EndpointsFactory {
    @Nullable
    public static Endpoints get() {
        switch (Config.endpointType) {
            case PROD:
                return new EndpointsProd();
            case DNET:
                return new EndpointsDnet();
            default:
                return null;
        }
    }
}
