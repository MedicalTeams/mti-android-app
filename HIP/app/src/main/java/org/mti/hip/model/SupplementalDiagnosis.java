package org.mti.hip.model;

/**
 * Created by r624513 on 11/4/15.
 */
public class SupplementalDiagnosis extends Diagnosis {

    private String description;
    private boolean selected;

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
}
