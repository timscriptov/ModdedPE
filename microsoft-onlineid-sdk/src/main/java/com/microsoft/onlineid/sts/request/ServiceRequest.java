package com.microsoft.onlineid.sts.request;

import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.configuration.Experiment;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.XmlSigner;
import com.microsoft.onlineid.sts.exception.CorruptedUserDATokenException;
import com.microsoft.onlineid.sts.response.ServiceResponse;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ServiceRequest extends AbstractTokenRequest<ServiceResponse> implements ISignableRequest {
    private String _clientAppUri;
    private DAToken _deviceDA;
    private String _flowToken;
    private Element _parentOfSignatureNode;
    private boolean _requestFlights = false;
    protected List<ISecurityScope> _requestedScopes = new ArrayList();
    private XmlSigner _signer;
    private String _telemetry;
    private DAToken _userDA;

    public void setRequestFlights(boolean requestFlights) {
        this._requestFlights = requestFlights;
    }

    public void setUserDA(DAToken userDA) {
        this._userDA = userDA;
    }

    public void setDeviceDA(DAToken deviceDA) {
        this._deviceDA = deviceDA;
    }

    public void setFlowToken(String flowToken) {
        this._flowToken = flowToken;
    }

    public void setClientAppUri(String clientAppUri) {
        this._clientAppUri = clientAppUri;
    }

    public void setTelemetry(String telemetry) {
        this._telemetry = telemetry;
    }

    public void addRequest(ISecurityScope request) {
        boolean z = true;
        if (request == null) {
            throw new IllegalArgumentException("Cannot request a null scope.");
        }
        Assertion.check(this._requestedScopes.size() < 2);
        if (request.equals(DAToken.Scope)) {
            z = false;
        }
        Assertion.check(z);
        if (!this._requestedScopes.contains(request)) {
            this._requestedScopes.add(request);
        }
    }

    public ServiceRequest() {
        this._requestedScopes.add(DAToken.Scope);
    }

    protected List<ISecurityScope> getRequestedScopes() {
        return this._requestedScopes;
    }

    protected void buildAuthInfo(Element authInfo) {
        super.buildAuthInfo(authInfo);
        Requests.appendElement(authInfo, "ps:InlineUX", AbstractStsRequest.DeviceType);
        Requests.appendElement(authInfo, "ps:ConsentFlags", ServerConfig.DefaultConfigVersion);
        Requests.appendElement(authInfo, "ps:IsConnected", ServerConfig.DefaultConfigVersion);
        if (this._requestFlights) {
            Requests.appendElement(authInfo, "ps:Experiments", Experiment.getExperimentList());
        }
        if (this._flowToken != null) {
            Requests.appendElement(authInfo, "ps:InlineFT", this._flowToken);
        }
        Requests.appendElement(authInfo, "ps:ClientAppURI", this._clientAppUri);
        if (!TextUtils.isEmpty(this._telemetry)) {
            Requests.appendElement(authInfo, "ps:Telemetry", this._telemetry);
        }
    }

    protected void buildSecurityNode(Element security) {
        try {
            security.appendChild(security.getOwnerDocument().importNode(Requests.xmlStringToElement(this._userDA.getToken()), true));
            appendDeviceDAToken(security, this._deviceDA);
            Element derivedToken = Requests.appendElement(security, "wssc:DerivedKeyToken");
            derivedToken.setAttribute("wsu:Id", "SignKey");
            derivedToken.setAttribute("Algorithm", "urn:liveid:SP800-108CTR-HMAC-SHA256");
            Element tokenReference = Requests.appendElement(derivedToken, "wsse:RequestedTokenReference");
            Requests.appendElement(tokenReference, "wsse:KeyIdentifier").setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/XX/oasis-2004XX-wss-saml-token-profile-1.0#SAMLAssertionID");
            Requests.appendElement(tokenReference, "wsse:Reference").setAttribute("URI", BuildConfig.VERSION_NAME);
            Requests.appendElement(derivedToken, "wssc:Nonce", this._signer.getEncodedNonce());
            appendTimestamp(security);
            this._parentOfSignatureNode = security;
        } catch (SAXException e) {
            throw new CorruptedUserDATokenException("Unable to parse user DAToken blob into XML, possibly corrupt.", e);
        }
    }

    public Endpoint getEndpoint() {
        return Endpoint.Sts;
    }

    public ServiceResponse instantiateResponse() {
        Assertion.check(getRequestedScopes().size() == 2);
        ISecurityScope ticketScope = null;
        for (ISecurityScope scope : getRequestedScopes()) {
            if (!scope.equals(DAToken.Scope)) {
                ticketScope = scope;
                break;
            }
        }
        return new ServiceResponse(getSigningSessionKey(), ticketScope, getClockSkewManager());
    }

    public void setXmlSigner(XmlSigner signer) {
        this._signer = signer;
    }

    public XmlSigner getXmlSigner() {
        return this._signer;
    }

    public Element getParentOfSignatureNode() {
        return this._parentOfSignatureNode;
    }

    public byte[] getSigningSessionKey() {
        return this._userDA.getSessionKey();
    }
}
