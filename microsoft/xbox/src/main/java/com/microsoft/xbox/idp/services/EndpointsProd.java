package com.microsoft.xbox.idp.services;

/**
 * 07.01.2021
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */

class EndpointsProd implements Endpoints {
    EndpointsProd() {
    }

    public String accounts() {
        return "https://accounts.xboxlive.com";
    }

    public String privacy() {
        return "https://privacy.xboxlive.com";
    }

    public String profile() {
        return "https://profile.xboxlive.com";
    }

    public String userAccount() {
        return "https://accountstroubleshooter.xboxlive.com";
    }

    public String userManagement() {
        return "https://user.mgt.xboxlive.com";
    }
}
