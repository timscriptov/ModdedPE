package com.mojang.minecraftpe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;

public class NetworkMonitor {
    private static final int NETWORK_CATEGORY_ETHERNET = 0;
    private static final int NETWORK_CATEGORY_OTHER = 2;
    private static final int NETWORK_CATEGORY_WIFI = 1;
    private HashMap<Integer, HashSet<Network>> mAvailableNetworksPerCategory;
    private Context mContext;

    public NetworkMonitor(Context context) {
        this.mContext = context;
        HashMap<Integer, HashSet<Network>> hashMap = new HashMap<>();
        this.mAvailableNetworksPerCategory = hashMap;
        hashMap.put(0, new HashSet<>());
        this.mAvailableNetworksPerCategory.put(1, new HashSet<>());
        this.mAvailableNetworksPerCategory.put(2, new HashSet<>());
        _addNetworkCallbacksForTransport(3, 0);
        _addNetworkCallbacksForTransport(1, 1);
        _addNetworkCallbacksForTransport(0, 2);
        _addNetworkCallbacksForTransport(2, 2);
        if (Build.VERSION.SDK_INT >= 31) {
            _addNetworkCallbacksForTransport(8, 2);
        }
    }

    private native void nativeUpdateNetworkStatus(boolean isEthernetConnected, boolean isWifiConnected, boolean isOtherConnected);

    private void _addNetworkCallbacksForTransport(int transport, final int networkCategory) {
        ((ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).registerNetworkCallback(_createNetworkRequestForTransport(transport), new ConnectivityManager.NetworkCallback() { // from class: com.mojang.minecraftpe.NetworkMonitor.1
            @Override
            public void onAvailable(@NonNull Network network) {
                ((HashSet<Network>) mAvailableNetworksPerCategory.get(networkCategory)).add(network);
                _updateStatus();
            }

            @Override
            public void onLost(@NonNull Network network) {
                ((HashSet<Network>) mAvailableNetworksPerCategory.get(networkCategory)).remove(network);
                _updateStatus();
            }
        });
    }

    private NetworkRequest _createNetworkRequestForTransport(int transport) {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        if (Build.VERSION.SDK_INT >= 23) {
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        builder.addTransportType(transport);
        return builder.build();
    }

    public void _updateStatus() {
        nativeUpdateNetworkStatus(!this.mAvailableNetworksPerCategory.get(0).isEmpty(), !this.mAvailableNetworksPerCategory.get(1).isEmpty(), !this.mAvailableNetworksPerCategory.get(2).isEmpty());
    }
}