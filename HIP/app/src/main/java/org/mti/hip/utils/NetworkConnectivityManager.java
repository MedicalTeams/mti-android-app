package org.mti.hip.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by r624513 on 11/4/15.
 * Updated by r625361 on 11/19/15.
 */
public class NetworkConnectivityManager {

    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;

    /**
     * Constructor
     *
     * @param context
     */
    public NetworkConnectivityManager(Context context) {
        activeNetwork = connectivityManager.getActiveNetworkInfo();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     *
     * @return true if network is available
     */
    public Boolean isConnected() {
        return ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
    }

    /**
     *
     * @return true if the devide is connected to a WiFi network
     */
    public Boolean isWifi() {
        return (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     *
     * @return true if the devide is connected to a Mobile network
     */
    public Boolean isMobile() {
        return (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     *
     * @return true if the devide is connected to a WiMax network
     */
    public Boolean isWiMax() {
        return (activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX);
    }

}
