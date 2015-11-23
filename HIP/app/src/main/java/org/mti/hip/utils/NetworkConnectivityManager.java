package org.mti.hip.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by r624513 on 11/4/15.
 * Updated by r625361 on 11/19/15.
 */
public class NetworkConnectivityManager {

    /**
     *
     * @return true if network is available
     */
    public static Boolean isConnected(NetworkInfo activeNetwork) {
        return ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
    }

    /**
     *
     * @return true if the device is connected to a WiFi network
     */
    public static Boolean isWifi(NetworkInfo activeNetwork) {
        return (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     *
     * @return true if the device is connected to a Mobile network
     */
    public static Boolean isMobile(NetworkInfo activeNetwork) {
        return (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     *
     * @return true if the device is connected to a WiMax network
     */
    public static Boolean isWiMax(NetworkInfo activeNetwork) {
        return (activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX);
    }

}