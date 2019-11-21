package com.microsoft.onlineid.internal.log;

public class RedactableResponse extends RedactableXml {
    private static final String[] TagsToKeep = new String[]{"ErrorSubcode", "ServerInfo", "S:Text", "S:Value", "ps:DisplaySessionID", "ps:ExpirationTime", "ps:RequestTime", "ps:SessionID", "ps:State", "psf:authstate", "psf:code", "psf:configVersion", "psf:reqstatus", "psf:serverInfo", "psf:text", "psf:value", "wsa:Address", "wst:TokenType", "wsu:Created", "wsu:Expires"};

    public RedactableResponse(String response) {
        super(response, TagsToKeep);
    }
}
