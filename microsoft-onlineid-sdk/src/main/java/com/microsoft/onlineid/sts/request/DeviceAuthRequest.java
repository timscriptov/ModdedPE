package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.response.DeviceAuthResponse;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;

public class DeviceAuthRequest extends AbstractTokenRequest<DeviceAuthResponse> {
    private DeviceCredentials _credentials;

    public void setDeviceCredentials(DeviceCredentials credentials) {
        this._credentials = credentials;
    }

    protected void buildSecurityNode(Element security) {
        Element credentials = Requests.appendElement(security, "wsse:UsernameToken");
        credentials.setAttribute("wsu:Id", "devicesoftware");
        Requests.appendElement(credentials, "wsse:Username", this._credentials.getUsername());
        Requests.appendElement(credentials, "wsse:Password", this._credentials.getPassword());
        appendTimestamp(security);
    }

    protected final List<ISecurityScope> getRequestedScopes() {
        return Collections.singletonList(DAToken.Scope);
    }

    public Endpoint getEndpoint() {
        return Endpoint.Sts;
    }

    public DeviceAuthResponse instantiateResponse() {
        return new DeviceAuthResponse(getClockSkewManager());
    }
}
