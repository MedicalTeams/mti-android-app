package org.mti.hip.model;

/**
 * Created by r624513 on 11/4/15.
 */
public class Supplemental {

    private String name;

    private int id;

    private int diagnosis;

    public String getName() {
        return name;
    }

    public void setName(String description) {
        this.name = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supplemental that = (Supplemental) o;

        if (id != that.id) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

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

    public int getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(int diagnosis) {
        this.diagnosis = diagnosis;
    }
}
