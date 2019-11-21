package com.microsoft.onlineid.internal.transport;

import android.content.Context;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Int;

public class TransportFactory {
    private final Context _applicationContext;

    public TransportFactory(Context applicationContext) {
        this._applicationContext = applicationContext;
    }

    public Transport createTransport() {
        Transport transport = new Transport();
        configureTransport(transport);
        return transport;
    }

    protected void configureTransport(Transport transport) {
        ServerConfig config = getServerConfig();
        transport.setConnectionTimeoutMilliseconds(config.getInt(Int.ConnectTimeout));
        transport.setReadTimeoutMilliseconds(config.getInt(Int.ReceiveTimeout));
        transport.appendCustomUserAgentString(Transport.buildUserAgentString(this._applicationContext));
    }

    protected ServerConfig getServerConfig() {
        return new ServerConfig(this._applicationContext);
    }
}
