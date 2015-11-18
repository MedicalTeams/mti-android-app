package org.mti.hip.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by r624513 on 11/4/15.
 */
public class Visit {

    private String clinician;
    private String facility;
    private char gender;
    private Boolean isNational;
    private int opId;
    private int ageMonths;
    private Boolean isRevisit = false;
    private ArrayList<Diagnosis> diags = new ArrayList<>();

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClinician() {
        return clinician;
    }

    public void setClinician(String clinician) {
        this.clinician = clinician;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public Boolean getIsNational() {
        return isNational;
    }

    public void setIsNational(Boolean isNational) {
        this.isNational = isNational;
    }

    public int getOpId() {
        return opId;
    }

    public void setOpId(int opId) {
        this.opId = opId;
    }

    public int getAgeMonths() {
        return ageMonths;
    }

    public void setAgeMonths(int ageMonths) {
        this.ageMonths = ageMonths;
    }

    public Boolean getIsRevisit() {
        return isRevisit;
    }

    public void setIsRevisit(Boolean isRevisit) {
        this.isRevisit = isRevisit;
    }

    public ArrayList<Diagnosis> getDiags() {
        return diags;
    }

    public void setDiags(ArrayList<Diagnosis> diags) {
        this.diags = diags;
    }


}
