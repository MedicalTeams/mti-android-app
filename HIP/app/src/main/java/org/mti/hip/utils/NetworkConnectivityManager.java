package org.mti.hip.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Created by r624513 on 11/4/15.
 * Updated by r625361 on 11/19/15.
 */
public class NetworkConnectivityManager {

    /**
     *
     * @param context
     * @return The MAC address of the mobile device
     */
    public static String getMacAddress(Context context) {
        String macAddress = ((WifiManager) (context.getSystemService(Context.WIFI_SERVICE))).getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "";
        }
        return macAddress;
    }

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
