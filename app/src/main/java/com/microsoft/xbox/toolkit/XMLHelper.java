package com.microsoft.xbox.toolkit;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XMLHelper {
    private static final int XML_WAIT_TIMEOUT_MS = 1000;
    private static XMLHelper instance = new XMLHelper();
    private Serializer serializer;

    private XMLHelper() {
        serializer = null;
        serializer = new Persister(new AnnotationStrategy());
    }

    public static XMLHelper instance() {
        return instance;
    }

    public <T> T load(InputStream input, Class<T> type) throws XLEException {
        ClassLoader clsLoader = null;
        if (ThreadManager.UIThread != Thread.currentThread()) {
            BackgroundThreadWaitor.getInstance().waitForReady(1000);
        }
        new TimeMonitor();
        try {
            clsLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(type.getClassLoader());
            T rv = serializer.read(type, input, false);
            Thread.currentThread().setContextClassLoader(clsLoader);
            return rv;
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(clsLoader);
            throw th;
        }
    }

    public <T> String save(T output) throws XLEException {
        new TimeMonitor();
        StringWriter writer = new StringWriter();
        try {
            serializer.write(output, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        }
    }

    public <T> void save(T output, OutputStream outStream) throws XLEException {
        new TimeMonitor();
        try {
            serializer.write(output, outStream);
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        }
    }
}
