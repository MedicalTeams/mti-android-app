package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.mti.hip.SuperActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by r624513 on 11/4/15.
 */
public class Diagnosis {

    private String description;
    private boolean selected;
//    private HashMap<Integer, ArrayList<SupplementalDiagnosis>> diagMap = new HashMap<>();
    private ArrayList<SupplementalDiagnosis> supplementalDiags = new ArrayList<>();

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

    public ArrayList<SupplementalDiagnosis> getSupplementalDiags() {
        return supplementalDiags;
    }

    public void setSupplementalDiags(ArrayList<SupplementalDiagnosis> supplementalDiags) {
        this.supplementalDiags = supplementalDiags;
    }

//    public HashMap<Integer, ArrayList<SupplementalDiagnosis>> getDiagMap() {
//        return diagMap;
//    }
//
//    public void setDiagMap(HashMap<Integer, ArrayList<SupplementalDiagnosis>> diagMap) {
//        this.diagMap = diagMap;
//    }
}
