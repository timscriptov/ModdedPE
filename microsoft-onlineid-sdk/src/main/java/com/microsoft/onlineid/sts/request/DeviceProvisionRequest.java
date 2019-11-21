package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.response.DeviceProvisionResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DeviceProvisionRequest extends AbstractStsRequest<DeviceProvisionResponse> {
    private DeviceCredentials _credentials;

    public void setDeviceCredentials(DeviceCredentials credentials) {
        this._credentials = credentials;
    }

    public Document buildRequest() {
        Document doc = createBlankDocument(null, "DeviceAddRequest");
        Element addRequestElement = doc.getDocumentElement();
        Element clientInfo = Requests.appendElement(addRequestElement, "ClientInfo");
        clientInfo.setAttribute("name", AbstractStsRequest.AppIdentifier);
        clientInfo.setAttribute("version", "1.0");
        Element authentication = Requests.appendElement(addRequestElement, "Authentication");
        Requests.appendElement(authentication, "Membername", this._credentials.getUsername());
        Requests.appendElement(authentication, "Password", this._credentials.getPassword());
        return doc;
    }

    public Endpoint getEndpoint() {
        return Endpoint.DeviceProvision;
    }

    public DeviceProvisionResponse instantiateResponse() {
        return new DeviceProvisionResponse();
    }
}
