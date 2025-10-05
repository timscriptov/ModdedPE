package com.microsoft.applications.events;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2025
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
class ConnectivityCallback extends ConnectivityManager.NetworkCallback {
    private final HttpClient m_parent;
    private boolean m_metered;

    public ConnectivityCallback(HttpClient httpClient, boolean z) {
        this.m_parent = httpClient;
        this.m_metered = z;
    }

    @Override
    public final void onCapabilitiesChanged(Network network, @NotNull NetworkCapabilities networkCapabilities) {
        boolean z = !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        if (z != this.m_metered) {
            this.m_metered = z;
            this.m_parent.onCostChange(z);
        }
    }
}