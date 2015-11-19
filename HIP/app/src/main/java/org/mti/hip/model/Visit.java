package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by r624513 on 11/4/15.
 */
public class Visit {

            /*

        supplementals (Array[supplemental], optional)
             }
             supplemental {
             id (id),
             name (string, optional)
        }
             */

    public static final int national = 1;
    public static final int refugee = 2;


    private String staffMemberName;
    private String facilityName;
    private int facility;
    private char gender;
    private int beneficiaryType;
    private int OPD;
    private int patientAgeMonths;
    private Boolean isRevisit = false;
    private HashSet<Diagnosis> patientDiagnosis = new HashSet<>();
    private int stiContactsTreated;
    private int injuryLocation;
    private Date visitDate;
    private String deviceId;


    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getStaffMemberName() {
        return staffMemberName;
    }

    public void setStaffMemberName(String staffMemberName) {
        this.staffMemberName = staffMemberName;
    }

    public int getFacility() {
        return facility;
    }

    public void setFacility(int facility) {
        this.facility = facility;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(int beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public int getOPD() {
        return OPD;
    }

    public void setOPD(int OPD) {
        this.OPD = OPD;
    }

    public int getPatientAgeMonths() {
        return patientAgeMonths;
    }

    public void setPatientAgeMonths(int patientAgeMonths) {
        this.patientAgeMonths = patientAgeMonths;
    }

    public Boolean getIsRevisit() {
        return isRevisit;
    }

    public void setIsRevisit(Boolean isRevisit) {
        this.isRevisit = isRevisit;
    }

    public HashSet<Diagnosis> getPatientDiagnosis() {
        return patientDiagnosis;
    }

    public void setPatientDiagnosis(HashSet<Diagnosis> patientDiagnosis) {
        this.patientDiagnosis = patientDiagnosis;
    }


    public int getStiContactsTreated() {
        return stiContactsTreated;
    }

    public void setStiContactsTreated(int stiContactsTreated) {
        this.stiContactsTreated = stiContactsTreated;
    }

    public int getInjuryLocation() {
        return injuryLocation;
    }

    public void setInjuryLocation(int injuryLocation) {
        this.injuryLocation = injuryLocation;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonIgnore
    public String getFacilityName() {
        return facilityName;
    }

    @JsonIgnore
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
