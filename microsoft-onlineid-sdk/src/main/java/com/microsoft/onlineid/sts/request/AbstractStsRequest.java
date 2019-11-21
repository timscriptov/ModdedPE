package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.analytics.ITimedAnalyticsEvent;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.RedactableXml;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.response.AbstractStsResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public abstract class AbstractStsRequest<ResponseType extends AbstractStsResponse> {
    public static final String AppIdentifier = "MSAAndroidApp";
    public static final String DeviceType = "Android";
    public static final String StsBinaryVersion = "11";
    private ClockSkewManager _clockSkewManager;
    private URL _destination;
    private int _msaAppVersionCode;
    private TransportFactory _transportFactory;

    public abstract Document buildRequest();

    public abstract Endpoint getEndpoint();

    protected abstract ResponseType instantiateResponse();

    public URL getDestination() {
        return this._destination;
    }

    public void setDestination(URL url) {
        this._destination = url;
    }

    public int getMsaAppVersionCode() {
        return this._msaAppVersionCode;
    }

    public void setMsaAppVersionCode(int versionCode) {
        this._msaAppVersionCode = versionCode;
    }

    void setTransportFactory(TransportFactory transportFactory) {
        this._transportFactory = transportFactory;
    }

    protected ClockSkewManager getClockSkewManager() {
        return this._clockSkewManager;
    }

    public void setClockSkewManager(ClockSkewManager clockSkewManager) {
        this._clockSkewManager = clockSkewManager;
    }

    protected final Document createBlankDocument(String rootNamespace, String rootElementName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            return factory.newDocumentBuilder().getDOMImplementation().createDocument(rootNamespace, rootElementName, null);
        } catch (ParserConfigurationException e) {
            Assertion.check(false);
            throw new RuntimeException("Invalid parser configuration.", e);
        }
    }

    public ResponseType send() throws NetworkException, InvalidResponseException {
        ResponseType response = instantiateResponse();
        Transport transport = this._transportFactory.createTransport();
        transport.openPostRequest(getDestination());
        OutputStream requestStream = transport.getRequestStream();
        ITimedAnalyticsEvent timer = ClientAnalytics.get().createTimedEvent(ClientAnalytics.StsRequestCategory, getClass().getSimpleName(), getAnalyticsRequestType());
        timer.start();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            if (Settings.isDebugBuild()) {
                CharArrayWriter writer = new CharArrayWriter();
                transformer.transform(new DOMSource(buildRequest()), new StreamResult(writer));
                Logger.info(new RedactableXml(String.format(Locale.US, "%s: %s", new Object[]{getClass().getSimpleName(), writer.toString()}), new String[0]));
                requestStream.write(request.getBytes(Strings.Utf8Charset));
            } else {
                transformer.transform(new DOMSource(buildRequest()), new StreamResult(requestStream));
            }
            requestStream.close();
            InputStream responseStream = transport.getResponseStream();
            updateClockSkew(transport.getResponseDate());
            try {
                response.parse(responseStream);
                timer.end();
                responseStream.close();
                transport.closeConnection();
                return response;
            } catch (IOException ex) {
                Logger.error("Unable to parse stream.", ex);
                throw new NetworkException("Unable to parse stream.", ex);
            } catch (Throwable th) {
                timer.end();
                responseStream.close();
            }
        } catch (TransformerConfigurationException ex2) {
            Logger.error("Unable to configure Transformer", ex2);
            throw new RuntimeException("Unable to configure Transformer", ex2);
        } catch (TransformerException ex3) {
            Logger.error("Problem occurred transforming XML document", ex3);
            throw new RuntimeException("Problem occurred transforming XML document", ex3);
        } catch (IOException ex4) {
            Logger.error("Unable to close stream", ex4);
            throw new NetworkException("Unable to close stream", ex4);
        } catch (Throwable th2) {
            transport.closeConnection();
        }
    }

    private void updateClockSkew(long serverTime) {
        if (serverTime != 0) {
            getClockSkewManager().onTimestampReceived(serverTime);
            ClientAnalytics.get().logClockSkew(getClockSkewManager().getSkewMilliseconds());
        }
    }

    protected String getAnalyticsRequestType() {
        return "(none)";
    }
}
