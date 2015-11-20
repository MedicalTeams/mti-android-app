package org.mti.hip.model;

import java.util.HashSet;

/**
 * Created by r624513 on 11/4/15.
 */
public class Diagnosis {

    private String name;
    private int id;

//    private HashMap<Integer, ArrayList<Supplemental>> diagMap = new HashMap<>();
    private HashSet<Supplemental> supplementals = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public HashSet<Supplemental> getSupplementals() {
        return supplementals;
    }

    public void setSupplementals(HashSet<Supplemental> supplementals) {
        this.supplementals = supplementals;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diagnosis diagnosis = (Diagnosis) o;

        if (id != diagnosis.id) return false;
        return !(name != null ? !name.equals(diagnosis.name) : diagnosis.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //    public HashMap<Integer, ArrayList<Supplemental>> getDiagMap() {
//        return diagMap;
//    }
//
//    public void setDiagMap(HashMap<Integer, ArrayList<Supplemental>> diagMap) {
//        this.diagMap = diagMap;
//    }
}
