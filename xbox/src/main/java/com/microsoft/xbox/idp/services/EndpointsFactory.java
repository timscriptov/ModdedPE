package com.microsoft.xbox.idp.services;

import org.jetbrains.annotations.Nullable;

/**
 * 07.01.2021
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