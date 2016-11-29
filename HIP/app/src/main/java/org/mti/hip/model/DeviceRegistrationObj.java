package org.mti.hip.model;

import org.mti.hip.utils.StorageManager;

import java.util.Date;

public class DeviceRegistrationObj {

    private String uuid = StorageManager.getSerialNumber();
    private String appVersion;
    private String description = "Device serial number last created/updated on " + new Date();
    private int facility;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFacility() {
        return facility;
    }

    public void setFacility(int facility) {
        this.facility = facility;
    }
}