package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.mti.hip.SuperActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by r624513 on 11/4/15.
 */
public class Diagnosis {

    private String description;
    private int id;

    @JsonIgnore
    private boolean selected;
//    private HashMap<Integer, ArrayList<SupplementalDiagnosis>> diagMap = new HashMap<>();
    private HashSet<SupplementalDiagnosis> supplementalDiags = new HashSet<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public HashSet<SupplementalDiagnosis> getSupplementalDiags() {
        return supplementalDiags;
    }

    public void setSupplementalDiags(HashSet<SupplementalDiagnosis> supplementalDiags) {
        this.supplementalDiags = supplementalDiags;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diagnosis diagnosis = (Diagnosis) o;

        if (id != diagnosis.id) return false;
        return !(description != null ? !description.equals(diagnosis.description) : diagnosis.description != null);

    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //    public HashMap<Integer, ArrayList<SupplementalDiagnosis>> getDiagMap() {
//        return diagMap;
//    }
//
//    public void setDiagMap(HashMap<Integer, ArrayList<SupplementalDiagnosis>> diagMap) {
//        this.diagMap = diagMap;
//    }
}
