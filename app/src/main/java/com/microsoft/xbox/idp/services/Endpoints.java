package com.microsoft.xbox.idp.services;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface Endpoints {

    String accounts();

    String privacy();

    String profile();

    String userAccount();

    String userManagement();

    public enum Type {
        PROD,
        DNET
    }
}
