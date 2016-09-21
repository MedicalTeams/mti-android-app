package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.mti.hip.R;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by r624513 on 11/4/15.
 */
public class Visit {

//    private boolean sent;

    private int status;
    public static final int national = 1;
    public static final int refugee = 2;

    private String staffMemberName;
    private String facilityName;
    private int facility;
    private char gender;
    private int beneficiaryType;
    private long OPD;
    private int patientAgeYears;
    private int patientAgeMonths;
    private int patientAgeDays;
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

    public long getOPD() {
        return OPD;
    }

    public void setOPD(long OPD) {
        this.OPD = OPD;
    }

    /**
     * Gets age low so the system can handle age ranges.
     * Age range is not implemented in the UI.
     * @return Age as a decimal in years.
     */
    public double getPatientAgeMonthsLow(){
        return patientAgeYears + patientAgeMonths / 12.0 + patientAgeDays / (12.0 * 30.0);
    }

    /**
     * Gets and sets age low and high so the system can handle age ranges.
     * Age range is not implemented in the UI.
     * @return Age as a decimal in years.
     */
    public void setPatientAgeMonthsLow(double ageMonths){
        patientAgeYears = (int)Math.floor(ageMonths / 12.0);
        patientAgeMonths = (int)Math.floor(ageMonths - patientAgeYears * 12.0);
        patientAgeDays = (int)Math.floor(ageMonths - patientAgeYears * 12.0 * 30.0 - patientAgeMonths * 30.0);
    }

    public double getPatientAgeMonthsHigh(){
        return patientAgeYears * 12.0 + patientAgeMonths + patientAgeDays / (30.0);
    }

    @JsonIgnore
    public int getPatientAgeYears() { return patientAgeYears; }

    @JsonIgnore
    public void setPatientAgeYears(int patientAgeYears) { this.patientAgeYears = patientAgeYears; }

    @JsonIgnore
    public int getPatientAgeMonths() { return patientAgeMonths; }

    @JsonIgnore
    public void setPatientAgeMonths(int patientAgeMonths) { this.patientAgeMonths = patientAgeMonths; }

    @JsonIgnore
    public int getPatientAgeDays() { return patientAgeDays; }

    @JsonIgnore
    public void setPatientAgeDays(int patientAgeDays) { this.patientAgeDays = patientAgeDays; }


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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
