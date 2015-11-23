package org.mti.hip.model;

/**
 * Created by r624513 on 11/21/15.
 */
public class InjuryLocation {

    private int id;
    private String name;
    private int diagnosis;

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

    public int getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(int diagnosis) {
        this.diagnosis = diagnosis;
    }

}
