package org.mti.hip.model;

import org.mti.hip.SuperActivity;

import java.util.ArrayList;

/**
 * Created by r624513 on 11/4/15.
 */
public class Diagnosis {

    private String description;
    private ArrayList<SupplementalDiagnosis> supplementalDiagnoses;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<SupplementalDiagnosis> getSupplementalDiagnoses() {
        return supplementalDiagnoses;
    }

    public void setSupplementalDiagnoses(ArrayList<SupplementalDiagnosis> supplementalDiagnoses) {
        this.supplementalDiagnoses = supplementalDiagnoses;
    }
}
