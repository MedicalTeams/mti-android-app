package org.mti.hip.model;

import java.util.ArrayList;
import java.util.HashSet;

public class Diagnosis {

    private String name;
    private int id;
    private ArrayList<Supplemental> supplementals = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Supplemental> getSupplementals() {
        return supplementals;
    }

    public void setSupplementals(ArrayList<Supplemental> supplementals) { this.supplementals = supplementals; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diagnosis diagnosis = (Diagnosis) o;

        return !(name != null ? !name.equals(diagnosis.name) : diagnosis.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
