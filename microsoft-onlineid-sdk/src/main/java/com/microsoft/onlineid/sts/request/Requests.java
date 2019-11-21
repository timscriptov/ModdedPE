package com.microsoft.onlineid.sts.request;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Requests {
    public static Element appendElement(Node parent, String name) {
        Element element = parent.getOwnerDocument().createElement(name);
        parent.appendChild(element);
        return element;
    }

    public static Element appendElement(Node parent, String name, String text) {
        Element element = appendElement(parent, name);
        element.setTextContent(text);
        return element;
    }

    public static Element xmlStringToElement(String xml) throws SAXException {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
}
