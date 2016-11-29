package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Facility {

    private int id;
    private String name;
    private String settlement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    @JsonIgnore
    public String toString() {
        return name;
    }
}
