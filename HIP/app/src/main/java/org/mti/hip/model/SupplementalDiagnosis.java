package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by r624513 on 11/4/15.
 */
public class SupplementalDiagnosis {

    private String description;

    private int id;

    @JsonIgnore
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupplementalDiagnosis that = (SupplementalDiagnosis) o;

        if (id != that.id) return false;
        return !(description != null ? !description.equals(that.description) : that.description != null);

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
}
