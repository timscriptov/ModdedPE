package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.response.AbstractSoapResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractSoapRequest<ResponseType extends AbstractSoapResponse> extends AbstractStsRequest<ResponseType> {
    public static final String MsaAppGuid = "{F501FD64-9070-46AB-993C-6F7B71D8D883}";
    public static final String PSNamespace = "http://schemas.microsoft.com/Passport/SoapServices/PPCRL";
    public static final String PsfNamespace = "http://schemas.microsoft.com/Passport/SoapServices/SOAPFault";
    private static final int RequestExpiryMilliseconds = 300000;
    public static final String SamlNamespace = "urn:oasis:names:tc:SAML:1.0:assertion";
    public static final String SoapNamespace = "http://www.w3.org/2003/05/soap-envelope";
    public static final String WsaNamespace = "http://www.w3.org/2005/08/addressing";
    public static final String WspNamespace = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    public static final String WsscNamespace = "http://schemas.xmlsoap.org/ws/2005/02/sc";
    public static final String WsseNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WstNamespace = "http://schemas.xmlsoap.org/ws/2005/02/trust";
    public static final String WsuNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

    protected abstract void buildSecurityNode(Element element);

    protected abstract void buildSoapBody(Element element);

    public Document buildRequest() {
        Document doc = createBlankDocument(SoapNamespace, "s:Envelope");
        Element envelope = doc.getDocumentElement();
        addEnvelopeNamespaces(envelope);
        buildSoapHeader(Requests.appendElement(envelope, "s:Header"));
        buildSoapBody(Requests.appendElement(envelope, "s:Body"));
        if (this instanceof ISignableRequest) {
            ISignableRequest signableRequest = (ISignableRequest) this;
            signableRequest.getXmlSigner().sign(signableRequest);
        }
        return doc;
    }

    protected void addEnvelopeNamespaces(Element envelope) {
        envelope.setAttribute("xmlns:ps", PSNamespace);
        envelope.setAttribute("xmlns:wsse", WsseNamespace);
        envelope.setAttribute("xmlns:saml", SamlNamespace);
        envelope.setAttribute("xmlns:wsp", WspNamespace);
        envelope.setAttribute("xmlns:wsu", WsuNamespace);
        envelope.setAttribute("xmlns:wsa", WsaNamespace);
        envelope.setAttribute("xmlns:wssc", WsscNamespace);
        envelope.setAttribute("xmlns:wst", WstNamespace);
    }

    protected void buildSoapHeader(Element header) {
        Requests.appendElement(header, "wsa:Action", "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue").setAttribute("s:mustUnderstand", ServerConfig.DefaultConfigVersion);
        Requests.appendElement(header, "wsa:To", getDestination().toString()).setAttribute("s:mustUnderstand", ServerConfig.DefaultConfigVersion);
        Requests.appendElement(header, "wsa:MessageID", String.valueOf(System.currentTimeMillis()));
        Element authInfo = Requests.appendElement(header, "ps:AuthInfo");
        authInfo.setAttribute("xmlns:ps", PSNamespace);
        authInfo.setAttribute("Id", "PPAuthInfo");
        buildAuthInfo(authInfo);
        if (this instanceof ISignableRequest) {
            ((ISignableRequest) this).getXmlSigner().addElementToSign(authInfo);
        }
        buildSecurityNode(Requests.appendElement(header, "wsse:Security"));
    }

    protected void buildAuthInfo(Element authInfo) {
        Requests.appendElement(authInfo, "ps:BinaryVersion", AbstractStsRequest.StsBinaryVersion);
        Requests.appendElement(authInfo, "ps:DeviceType", AbstractStsRequest.DeviceType);
    }

    protected final void appendTimestamp(Element parent) {
        Element timestamp = Requests.appendElement(parent, "wsu:Timestamp");
        timestamp.setAttribute("wsu:Id", "Timestamp");
        timestamp.setAttribute("xmlns:wsu", WsuNamespace);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentTime = getClockSkewManager().getCurrentServerTime();
        Date expiry = new Date(currentTime.getTime() + 300000);
        Requests.appendElement(timestamp, "wsu:Created", format.format(currentTime));
        Requests.appendElement(timestamp, "wsu:Expires", format.format(expiry));
        if (this instanceof ISignableRequest) {
            ((ISignableRequest) this).getXmlSigner().addElementToSign(timestamp);
        }
    }

    protected final void appendDeviceDAToken(Element parent, DAToken deviceDAToken) {
        Element securityTokenElement = Requests.appendElement(parent, "wsse:BinarySecurityToken", deviceDAToken.getOneTimeSignedCredential(getClockSkewManager().getCurrentServerTime(), MsaAppGuid));
        securityTokenElement.setAttribute("ValueType", "urn:liveid:sha1device");
        securityTokenElement.setAttribute("Id", "DeviceDAToken");
    }
}
