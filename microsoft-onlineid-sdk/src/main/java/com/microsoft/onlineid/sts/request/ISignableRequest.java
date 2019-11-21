package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.sts.XmlSigner;
import org.w3c.dom.Element;

public interface ISignableRequest {
    Element getParentOfSignatureNode();

    byte[] getSigningSessionKey();

    XmlSigner getXmlSigner();

    void setXmlSigner(XmlSigner xmlSigner);
}
