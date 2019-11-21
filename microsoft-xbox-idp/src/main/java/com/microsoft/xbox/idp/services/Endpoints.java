package com.microsoft.xbox.idp.services;

public interface Endpoints {

    public enum Type {
        PROD,
        DNET
    }

    String accounts();

    String privacy();

    String profile();

    String userAccount();

    String userManagement();
}
