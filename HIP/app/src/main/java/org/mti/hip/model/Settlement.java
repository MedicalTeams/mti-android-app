package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by r624513 on 11/21/15.
 */
public class Settlement {

    private int facilityCount;
    private String name;

    public int getFacilityCount() {
        return facilityCount;
    }

    public void setFacilityCount(int facilityCount) {
        this.facilityCount = facilityCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String toString() {
        return name;
    }

}

