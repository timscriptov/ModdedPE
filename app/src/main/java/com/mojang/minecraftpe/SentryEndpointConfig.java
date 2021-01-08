package com.mojang.minecraftpe;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SentryEndpointConfig {
    public String projectId;
    public String publicKey;
    public String url;

    public SentryEndpointConfig(String str, String str2, String str3) {
        url = str;
        projectId = str2;
        publicKey = str3;
    }
}