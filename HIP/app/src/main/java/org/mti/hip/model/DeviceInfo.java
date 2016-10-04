package org.mti.hip.model;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class DeviceInfo {

    private String deviceId;
    private String versionName;
    private int versionCode;

    public DeviceInfo(Activity activity) {
        String serialNumber = Build.SERIAL;
        if(serialNumber == null) {
            deviceId = "";
        } else {
            deviceId = serialNumber;
        }
        PackageInfo pInfo = null;
        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getDeviceId() {
        return deviceId;
    }
    public String getVersionName() {
        return versionName;
    }
    public int getVersionCode() {
        return versionCode;
    }

}